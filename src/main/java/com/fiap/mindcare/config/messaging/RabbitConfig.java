package com.fiap.mindcare.config.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${mindcheck.rabbitmq.queue}")
    private String triagemQueue;

    @Value("${mindcheck.rabbitmq.exchange}")
    private String triagemExchange;

    @Value("${mindcheck.rabbitmq.routing-key}")
    private String triagemRoutingKey;

    @Bean
    public Queue triagemQueue() {
        return new Queue(triagemQueue, true);
    }

    @Bean
    public TopicExchange triagemExchange() {
        return new TopicExchange(triagemExchange, true, false);
    }

    @Bean
    public Binding triagemBinding(Queue triagemQueue, TopicExchange triagemExchange) {
        return BindingBuilder
                .bind(triagemQueue)
                .to(triagemExchange)
                .with(triagemRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
