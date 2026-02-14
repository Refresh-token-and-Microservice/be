package com.example.auth_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SAGA_EXCHANGE = "saga-exchange";
    public static final String USER_ACTIVATE_QUEUE = "user.activate.queue";
    public static final String AUTH_ROLLBACK_QUEUE = "auth.rollback.queue";

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE);
    }

    @Bean
    public Queue userActivateQueue() {
        return new Queue(USER_ACTIVATE_QUEUE, true);
    }

    @Bean
    public Queue authRollbackQueue() {
        return new Queue(AUTH_ROLLBACK_QUEUE, true);
    }

    @Bean
    public Binding userActivateBinding() {
        return BindingBuilder.bind(userActivateQueue())
                .to(sagaExchange())
                .with("orchestrator.user.activate");
    }

    @Bean
    public Binding authRollbackBinding() {
        return BindingBuilder.bind(authRollbackQueue())
                .to(sagaExchange())
                .with("orchestrator.auth.rollback");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
