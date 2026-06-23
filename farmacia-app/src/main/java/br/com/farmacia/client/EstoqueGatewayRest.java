package br.com.farmacia.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * V1 — baixa de estoque síncrona via REST (um microsserviço chamando o outro).
 * Ativa por padrão (estoque.mode=rest).
 */
@Component
@ConditionalOnProperty(name = "estoque.mode", havingValue = "rest", matchIfMissing = true)
public class EstoqueGatewayRest implements EstoqueGateway {

    private static final Logger log = LoggerFactory.getLogger(EstoqueGatewayRest.class);

    private final RestTemplate restTemplate;
    private final String produtosUrl;

    public EstoqueGatewayRest(RestTemplate restTemplate,
                              @Value("${produtos-service.url}") String produtosUrl) {
        this.restTemplate = restTemplate;
        this.produtosUrl = produtosUrl;
    }

    @Override
    public void baixarEstoque(Long productId, Integer quantity) {
        String url = produtosUrl + "/api/products/" + productId + "/baixa?quantity=" + quantity;
        log.info("[ESTOQUE-REST] Baixando {} unidade(s) do produto {}", quantity, productId);
        try {
            restTemplate.postForObject(url, null, Void.class);
        } catch (Exception e) {
            log.error("[ESTOQUE-REST] Falha na baixa do produto {}: {}", productId, e.getMessage());
        }
    }
}
