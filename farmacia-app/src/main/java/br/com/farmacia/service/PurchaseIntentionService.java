package br.com.farmacia.service;

import br.com.farmacia.dto.BulkOrderRequest;
import br.com.farmacia.dto.BulkOrderResponse;
import br.com.farmacia.dto.OrderRequest;
import br.com.farmacia.integration.fornecedor.SupplierPort;
import br.com.farmacia.model.Product;
import br.com.farmacia.model.PurchaseIntention;
import br.com.farmacia.model.SupplierType;
import br.com.farmacia.model.utils.PurchaseIntentionStatus;
import br.com.farmacia.repository.PurchaseIntentionRepository;
import br.com.farmacia.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurchaseIntentionService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseIntentionService.class);

    private final PurchaseIntentionRepository intentionRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final List<SupplierPort> supplierPorts;

    public PurchaseIntentionService(PurchaseIntentionRepository intentionRepository,
                                    ProductService productService,
                                    ProductRepository productRepository,
                                    List<SupplierPort> supplierPorts) {
        this.intentionRepository = intentionRepository;
        this.productService = productService;
        this.productRepository = productRepository;
        this.supplierPorts = supplierPorts;
    }

    @Transactional
    public PurchaseIntention createIntention(Long productId, Integer quantity) {
        Product product = productService.findById(productId);

        PurchaseIntention intention = PurchaseIntention.builder()
                .product(product)
                .quantity(quantity)
                .status(PurchaseIntentionStatus.PENDING)
                .build();

        return intentionRepository.save(intention);
    }

    public List<PurchaseIntention> findAll() {
        return intentionRepository.findAll();
    }

    public List<PurchaseIntention> findByStatus(PurchaseIntentionStatus status) {
        return intentionRepository.findByStatus(status);
    }

    public PurchaseIntention findById(Long id) {
        return intentionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intenção de compra não encontrada com id: " + id));
    }

    @Transactional
    public PurchaseIntention approve(Long id) {
        PurchaseIntention intention = findById(id);
        validateStatusTransition(intention, PurchaseIntentionStatus.PENDING, "aprovar");

        intention.setStatus(PurchaseIntentionStatus.APPROVED);
        intention.setReviewedAt(LocalDateTime.now());
        return intentionRepository.save(intention);
    }

    @Transactional
    public PurchaseIntention reject(Long id) {
        PurchaseIntention intention = findById(id);
        validateStatusTransition(intention, PurchaseIntentionStatus.PENDING, "reprovar");

        intention.setStatus(PurchaseIntentionStatus.REJECTED);
        intention.setReviewedAt(LocalDateTime.now());
        return intentionRepository.save(intention);
    }

    @Transactional
    public BulkPurchaseSummary executeBulkPurchase() {
        List<PurchaseIntention> approvedIntentions = intentionRepository.findByStatus(PurchaseIntentionStatus.APPROVED);

        if (approvedIntentions.isEmpty()) {
            return new BulkPurchaseSummary(0, 0, 0, List.of());
        }

        // Group intentions by supplier
        Map<SupplierType, List<PurchaseIntention>> groupedBySupplier = approvedIntentions.stream()
                .collect(Collectors.groupingBy(i -> i.getProduct().getSupplier()));

        List<SupplierResult> supplierResults = new ArrayList<>();
        int totalSuccess = 0;
        int totalFailed = 0;

        for (Map.Entry<SupplierType, List<PurchaseIntention>> entry : groupedBySupplier.entrySet()) {
            SupplierType supplierType = entry.getKey();
            List<PurchaseIntention> intentions = entry.getValue();

            SupplierPort supplierPort = supplierPorts.stream()
                    .filter(port -> port.supports(supplierType))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Nenhum adaptador encontrado para o fornecedor: " + supplierType));

            // Build bulk order request with all items for this supplier
            List<OrderRequest> orderItems = intentions.stream()
                    .map(i -> new OrderRequest(i.getProduct().getId(), i.getProduct().getName(), i.getQuantity()))
                    .toList();

            BulkOrderRequest bulkOrderRequest = new BulkOrderRequest(orderItems);

            log.info("Enviando pedido em lote para {} com {} itens", supplierType, orderItems.size());
            BulkOrderResponse response = supplierPort.sendBulkOrder(bulkOrderRequest);

            if ("SUCESSO".equals(response.getStatus())) {
                // Update stock and mark as ordered
                LocalDateTime now = LocalDateTime.now();
                for (PurchaseIntention intention : intentions) {
                    Product product = intention.getProduct();
                    product.setStock(product.getStock() + intention.getQuantity());
                    productRepository.save(product);

                    intention.setStatus(PurchaseIntentionStatus.ORDERED);
                    intention.setOrderedAt(now);
                    intention.setSupplierProtocol(response.getProtocol());
                    intentionRepository.save(intention);
                }
                totalSuccess += intentions.size();
                supplierResults.add(new SupplierResult(supplierType.name(), response.getProtocol(), "SUCESSO", null, intentions.size()));
            } else {
                // Failure — intentions remain APPROVED for retry
                totalFailed += intentions.size();
                supplierResults.add(new SupplierResult(supplierType.name(), null, "ERRO", response.getMessage(), intentions.size()));
                log.error("Falha no pedido em lote para {}: {}", supplierType, response.getMessage());
            }
        }

        return new BulkPurchaseSummary(approvedIntentions.size(), totalSuccess, totalFailed, supplierResults);
    }

    private void validateStatusTransition(PurchaseIntention intention, PurchaseIntentionStatus expectedStatus, String action) {
        if (intention.getStatus() != expectedStatus) {
            throw new IllegalArgumentException(
                    String.format("Não é possível %s uma intenção com status %s. Status esperado: %s",
                            action, intention.getStatus(), expectedStatus));
        }
    }

    // Inner classes for bulk purchase response

    public record SupplierResult(
            String supplier,
            String protocol,
            String status,
            String message,
            int itemCount
    ) {}

    public record BulkPurchaseSummary(
            int totalIntentionsProcessed,
            int totalSuccess,
            int totalFailed,
            List<SupplierResult> supplierResults
    ) {}
}
