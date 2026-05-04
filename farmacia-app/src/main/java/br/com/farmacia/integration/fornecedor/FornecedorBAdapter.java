package br.com.farmacia.integration.fornecedor;

import br.com.farmacia.dto.OrderRequest;
import br.com.farmacia.dto.OrderResponse;
import br.com.farmacia.model.SupplierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FornecedorBAdapter implements SupplierPort {

    private static final Logger log = LoggerFactory.getLogger(FornecedorBAdapter.class);

    private final RestTemplate restTemplate;
    private final String supplierUrl;

    public FornecedorBAdapter(RestTemplate restTemplate, 
                              @Value("${fornecedor-b.url}") String supplierUrl) {
        this.restTemplate = restTemplate;
        this.supplierUrl = supplierUrl;
    }

    @Override
    public boolean supports(SupplierType type) {
        return SupplierType.FORNECEDOR_B.equals(type);
    }

    @Override
    public OrderResponse sendOrder(OrderRequest order) {
        String url = supplierUrl + "/api/pedidos";
        log.info("[FORNECEDOR-B] Enviando pedido via SOAP/Rest adapter de {} (Qtd: {}) para {}", 
                 order.getProductName(), order.getQuantity(), url);

        try {
            ResponseEntity<OrderResponse> response = restTemplate.postForEntity(url, order, OrderResponse.class);
            
            log.info("[FORNECEDOR-B] Resposta recebida: {} - {}", 
                     response.getBody().getProtocol(), response.getBody().getStatus());
            
            return response.getBody();
        } catch (Exception e) {
            log.error("[FORNECEDOR-B] Erro ao comunicar com fornecedor: {}", e.getMessage());
            return new OrderResponse(null, "ERRO", "Falha de comunicação: " + e.getMessage());
        }
    }
}
