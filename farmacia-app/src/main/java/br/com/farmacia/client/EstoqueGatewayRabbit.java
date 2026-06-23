package br.com.farmacia.client;

import br.com.farmacia.dto.EstoqueBaixaEvent;
import br.com.farmacia.messaging.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * V2 — baixa de estoque assíncrona publicando um evento no RabbitMQ.
 * Ativa com estoque.mode=messaging.
 */
@Component
@ConditionalOnProperty(name = "estoque.mode", havingValue = "messaging")
public class EstoqueGatewayRabbit implements EstoqueGateway {

    private static final Logger log = LoggerFactory.getLogger(EstoqueGatewayRabbit.class);

    private final RabbitTemplate rabbitTemplate;

    public EstoqueGatewayRabbit(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void baixarEstoque(Long productId, Integer quantity) {
        log.info("[ESTOQUE-MENSAGERIA] Publicando evento de baixa: produto={} qtd={}", productId, quantity);
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                new EstoqueBaixaEvent(productId, quantity));
    }
}
