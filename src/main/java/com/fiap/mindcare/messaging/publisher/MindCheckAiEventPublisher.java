package com.fiap.mindcare.messaging.publisher;

import com.fiap.mindcare.messaging.event.TriagemAvaliacaoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MindCheckAiEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MindCheckAiEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public MindCheckAiEventPublisher(RabbitTemplate rabbitTemplate,
                                     @Value("${mindcheck.rabbitmq.exchange}") String exchange,
                                     @Value("${mindcheck.rabbitmq.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publicarTriagem(TriagemAvaliacaoEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        LOGGER.info("Evento TriagemAvaliacao publicado: {}", event.getTriagemId());
    }
}
