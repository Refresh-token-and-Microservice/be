package com.example.orchestrator_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common_dto.constant.RabbitMQConstants;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(RabbitMQConstants.SAGA_EXCHANGE);
    }

    @Bean
    public Queue authRegisteredQueue() {
        return new Queue(RabbitMQConstants.AUTH_REGISTERED_QUEUE, true);
    }

    @Bean
    public Queue profileCreatedQueue() {
        return new Queue(RabbitMQConstants.PROFILE_CREATED_QUEUE, true);
    }

    @Bean
    public Queue profileFailedQueue() {
        return new Queue(RabbitMQConstants.PROFILE_FAILED_QUEUE, true);
    }

    @Bean
    public Binding authRegisteredBinding() {
        return BindingBuilder.bind(authRegisteredQueue())
                .to(sagaExchange())
                .with(RabbitMQConstants.AUTH_USER_REGISTERED);
    }

    @Bean
    public Binding profileCreatedBinding() {
        return BindingBuilder.bind(profileCreatedQueue())
                .to(sagaExchange())
                .with(RabbitMQConstants.USER_PROFILE_CREATED);
    }

    @Bean
    public Binding profileFailedBinding() {
        return BindingBuilder.bind(profileFailedQueue())
                .to(sagaExchange())
                .with(RabbitMQConstants.USER_PROFILE_FAILED);
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
