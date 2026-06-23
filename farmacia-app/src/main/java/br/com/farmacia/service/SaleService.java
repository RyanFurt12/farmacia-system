package br.com.farmacia.service;

import br.com.farmacia.client.EstoqueGateway;
import br.com.farmacia.client.ProdutoClient;
import br.com.farmacia.cpf.CpfValidator;
import br.com.farmacia.dto.ProdutoDTO;
import br.com.farmacia.dto.SaleItemRequest;
import br.com.farmacia.dto.SaleRequest;
import br.com.farmacia.exception.InvalidCpfException;
import br.com.farmacia.integration.ans.AnsPort;
import br.com.farmacia.integration.sefaz.SefazPort;
import br.com.farmacia.model.Client;
import br.com.farmacia.model.Sale;
import br.com.farmacia.model.SaleItem;
import br.com.farmacia.repository.ClientRepository;
import br.com.farmacia.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ClientService clientService;
    private final ClientRepository clientRepository;
    private final ProdutoClient produtoClient;
    private final EstoqueGateway estoqueGateway;
    private final SefazPort sefazPort;
    private final AnsPort ansPort;
    private final DiscountService discountService;
    private final CpfValidator cpfValidator;

    public SaleService(SaleRepository saleRepository,
                       ClientService clientService,
                       ClientRepository clientRepository,
                       ProdutoClient produtoClient,
                       EstoqueGateway estoqueGateway,
                       SefazPort sefazPort,
                       AnsPort ansPort,
                       DiscountService discountService,
                       CpfValidator cpfValidator) {
        this.saleRepository = saleRepository;
        this.clientService = clientService;
        this.clientRepository = clientRepository;
        this.produtoClient = produtoClient;
        this.estoqueGateway = estoqueGateway;
        this.sefazPort = sefazPort;
        this.ansPort = ansPort;
        this.discountService = discountService;
        this.cpfValidator = cpfValidator;
    }

    @Transactional
    public Sale registerSale(SaleRequest request) {
        Sale sale = Sale.builder()
                .items(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;
        boolean hasControlledProduct = false;

        for (SaleItemRequest itemReq : request.getItems()) {
            ProdutoDTO product = produtoClient.buscarProduto(itemReq.getProductId());

            SaleItem item = SaleItem.builder()
                    .sale(sale)
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();

            sale.getItems().add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            if (Boolean.TRUE.equals(product.getControlled())) {
                hasControlledProduct = true;
            }
        }

        sale.setTotal(total);

        Client client = resolveClient(request.getCpf(), hasControlledProduct, sale);
        sale.setClient(client);

        DiscountService.DiscountResult discount = discountService.calculate(client, total);
        sale.setDiscountPercent(discount.percent());
        sale.setDiscountAmount(discount.amount());
        sale.setFinalTotal(total.subtract(discount.amount()));

        SefazPort.SefazResult sefazResult = sefazPort.generateInvoice(sale.getFinalTotal().doubleValue());
        sale.setInvoiceNumber(sefazResult.invoiceNumber());
        sale.setInvoiceStatus(sefazResult.status());

        if (hasControlledProduct && request.getPrescriptionId() != null) {
            AnsPort.AnsResult ansResult = ansPort.validatePrescription(request.getPrescriptionId());
            sale.setPrescriptionId(request.getPrescriptionId());
            sale.setPrescriptionStatus(ansResult.status());
        } else if (hasControlledProduct) {
            sale.setPrescriptionStatus("MISSING");
        }

        Sale saved = saleRepository.save(sale);

        for (SaleItem item : saved.getItems()) {
            estoqueGateway.baixarEstoque(item.getProductId(), item.getQuantity());
        }

        return saved;
    }

    private Client resolveClient(String cpf, boolean hasControlledProduct, Sale sale) {
        if (hasControlledProduct) {
            if (cpf == null || cpf.isBlank()) {
                throw new IllegalArgumentException("Venda de medicamento controlado exige o CPF do cliente");
            }
            return clientService.findOrCreateByCpf(cpf);
        }

        if (cpf == null || cpf.isBlank()) {
            return null;
        }

        if (!cpfValidator.isValid(cpf)) {
            throw new InvalidCpfException(cpf);
        }
        String cleanCpf = cpfValidator.unformat(cpf);

        Optional<Client> existing = clientRepository.findByCpf(cleanCpf);
        if (existing.isPresent()) {
            return existing.get();
        }

        sale.setInvoiceCpf(cleanCpf);
        return null;
    }

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    public Sale findById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada com id: " + id));
    }
}
