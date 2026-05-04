package br.com.farmacia.service;

import br.com.farmacia.dto.SaleItemRequest;
import br.com.farmacia.dto.SaleRequest;
import br.com.farmacia.integration.ans.AnsPort;
import br.com.farmacia.integration.sefaz.SefazPort;
import br.com.farmacia.model.*;
import br.com.farmacia.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {
    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final ClientService clientService;
    private final SefazPort sefazPort;
    private final AnsPort ansPort;

    public SaleService(SaleRepository saleRepository,
                       ProductService productService,
                       ClientService clientService,
                       SefazPort sefazPort,
                       AnsPort ansPort) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.clientService = clientService;
        this.sefazPort = sefazPort;
        this.ansPort = ansPort;
    }

    @Transactional
    public Sale registerSale(SaleRequest request) {
        Client client = clientService.findOrCreateByCpf(request.getCpf());

        Sale sale = Sale.builder()
                .client(client)
                .items(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;
        boolean hasControlledProduct = false;

        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productService.findById(itemReq.getProductId());

            SaleItem item = SaleItem.builder()
                    .sale(sale)
                    .product(product)
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

        SefazPort.SefazResult sefazResult = sefazPort.generateInvoice(total.doubleValue());
        sale.setInvoiceNumber(sefazResult.invoiceNumber());
        sale.setInvoiceStatus(sefazResult.status());

        if (hasControlledProduct && request.getPrescriptionId() != null) {
            AnsPort.AnsResult ansResult = ansPort.validatePrescription(request.getPrescriptionId());
            sale.setPrescriptionId(request.getPrescriptionId());
            sale.setPrescriptionStatus(ansResult.status());
        } else if (hasControlledProduct) {
            sale.setPrescriptionStatus("MISSING");
        }

        return saleRepository.save(sale);
    }

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    public Sale findById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada com id: " + id));
    }
}
