package br.com.farmacia.client;

import br.com.farmacia.dto.ProdutoDTO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProdutoClientRest implements ProdutoClient {

    private static final Logger log = LoggerFactory.getLogger(ProdutoClientRest.class);

    private final RestTemplate restTemplate;
    private final String produtosUrl;

    public ProdutoClientRest(RestTemplate restTemplate,
                             @Value("${produtos-service.url}") String produtosUrl) {
        this.restTemplate = restTemplate;
        this.produtosUrl = produtosUrl;
    }

    @Override
    public ProdutoDTO buscarProduto(Long id) {
        String url = produtosUrl + "/api/products/" + id;
        try {
            return restTemplate.getForObject(url, ProdutoDTO.class);
        } catch (Exception e) {
            log.error("[PRODUTOS] Falha ao buscar produto {}: {}", id, e.getMessage());
            throw new EntityNotFoundException("Produto não encontrado ou serviço indisponível: " + id);
        }
    }
}
