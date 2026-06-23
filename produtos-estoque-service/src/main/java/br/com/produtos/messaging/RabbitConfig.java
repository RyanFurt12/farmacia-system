package br.com.produtos.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(name = "estoque.mode", havingValue = "messaging")
public class RabbitConfig {

    public static final String EXCHANGE = "farmacia.exchange";
    public static final String QUEUE = "estoque.baixa.queue";
    public static final String ROUTING_KEY = "estoque.baixa";

    @Bean
    public TopicExchange farmaciaExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue estoqueBaixaQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding estoqueBaixaBinding(Queue estoqueBaixaQueue, TopicExchange farmaciaExchange) {
        return BindingBuilder.bind(estoqueBaixaQueue).to(farmaciaExchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
