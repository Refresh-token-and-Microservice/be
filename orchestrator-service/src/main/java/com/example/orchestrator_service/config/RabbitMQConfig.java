package com.example.orchestrator_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.constant.RegisterConstants;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(RabbitMQConstants.SAGA_EXCHANGE);
    }

    @Bean
    public Queue authRegisteredQueue() {
        return new Queue(RegisterConstants.AUTH_REGISTERED_QUEUE, true);
    }

    @Bean
    public Queue profileCreatedQueue() {
        return new Queue(RegisterConstants.PROFILE_CREATED_QUEUE, true);
    }

    @Bean
    public Queue profileFailedQueue() {
        return new Queue(RegisterConstants.PROFILE_FAILED_QUEUE, true);
    }

    @Bean
    public Binding authRegisteredBinding() {
        return BindingBuilder.bind(authRegisteredQueue())
                .to(sagaExchange())
                .with(RegisterConstants.AUTH_USER_REGISTERED);
    }

    @Bean
    public Binding profileCreatedBinding() {
        return BindingBuilder.bind(profileCreatedQueue())
                .to(sagaExchange())
                .with(RegisterConstants.USER_PROFILE_CREATED);
    }

    @Bean
    public Binding profileFailedBinding() {
        return BindingBuilder.bind(profileFailedQueue())
                .to(sagaExchange())
                .with(RegisterConstants.USER_PROFILE_FAILED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Email Update Saga Queues
    @Bean
    public Queue emailUpdateRequestedQueue() {
        return new Queue(RabbitMQConstants.EMAIL_UPDATE_REQUESTED_QUEUE, true);
    }

    @Bean
    public Queue authEmailUpdatedQueue() {
        return new Queue(RabbitMQConstants.AUTH_EMAIL_UPDATED_QUEUE, true);
    }

    @Bean
    public Queue authEmailUpdateFailedQueue() {
        return new Queue(RabbitMQConstants.AUTH_EMAIL_UPDATE_FAILED_QUEUE, true);
    }

    @Bean
    public Binding emailUpdateRequestedBinding() {
        return BindingBuilder.bind(emailUpdateRequestedQueue())
                .to(sagaExchange())
                .with(RabbitMQConstants.EMAIL_UPDATE_REQUESTED);
    }

    @Bean
    public Binding authEmailUpdatedBinding() {
        return BindingBuilder.bind(authEmailUpdatedQueue())
                .to(sagaExchange())
                .with(RabbitMQConstants.AUTH_EMAIL_UPDATED);
    }

    @Bean
    public Binding authEmailUpdateFailedBinding() {
        return BindingBuilder.bind(authEmailUpdateFailedQueue())
                .to(sagaExchange())
                .with(RabbitMQConstants.AUTH_EMAIL_UPDATE_FAILED);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
