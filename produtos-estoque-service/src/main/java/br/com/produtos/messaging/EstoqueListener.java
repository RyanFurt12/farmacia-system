package br.com.produtos.messaging;

import br.com.farmacia.dto.EstoqueBaixaEvent;
import br.com.produtos.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(name = "estoque.mode", havingValue = "messaging")
public class EstoqueListener {

    private static final Logger log = LoggerFactory.getLogger(EstoqueListener.class);

    private final ProductService productService;

    public EstoqueListener(ProductService productService) {
        this.productService = productService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onEstoqueBaixa(EstoqueBaixaEvent event) {
        log.info("[MENSAGERIA] Evento de baixa recebido: produto={} qtd={}",
                 event.getProductId(), event.getQuantity());
        try {
            productService.decreaseStock(event.getProductId(), event.getQuantity());
        } catch (Exception e) {
            log.error("[MENSAGERIA] Falha ao processar baixa de estoque: {}", e.getMessage());
        }
    }
}
