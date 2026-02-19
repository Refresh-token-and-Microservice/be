package com.example.orchestrator_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common_dto.constant.DisableUserConstants;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.constant.RegisterConstants;
import com.example.common_dto.constant.UpdateEmailConstants;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(RabbitMQConstants.SAGA_EXCHANGE);
    }

    @Bean
    public Queue authUserRegisteredQueue() {
        return new Queue(RegisterConstants.QUEUE_ORCHESTRATOR_USER_REGISTERED, true);
    }

    @Bean
    public Queue profileCreatedQueue() {
        return new Queue(RegisterConstants.QUEUE_ORCHESTRATOR_PROFILE_CREATED, true);
    }

    @Bean
    public Queue profileFailedQueue() {
        return new Queue(RegisterConstants.QUEUE_ORCHESTRATOR_PROFILE_FAILED, true);
    }

    @Bean
    public Binding authUserRegisteredBinding() {
        return BindingBuilder.bind(authUserRegisteredQueue())
                .to(sagaExchange())
                .with(RegisterConstants.EVENT_AUTH_USER_REGISTERED);
    }

    @Bean
    public Binding profileCreatedBinding() {
        return BindingBuilder.bind(profileCreatedQueue())
                .to(sagaExchange())
                .with(RegisterConstants.EVENT_PROFILE_CREATED);
    }

    @Bean
    public Binding profileFailedBinding() {
        return BindingBuilder.bind(profileFailedQueue())
                .to(sagaExchange())
                .with(RegisterConstants.EVENT_PROFILE_FAILED);
    }

    @SuppressWarnings("removal")
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Email Update Saga Queues
    @Bean
    public Queue emailUpdateRequestedQueue() {
        return new Queue(UpdateEmailConstants.QUEUE_ORCHESTRATOR_EMAIL_UPDATE_REQUESTED, true);
    }

    @Bean
    public Queue authEmailUpdatedQueue() {
        return new Queue(UpdateEmailConstants.QUEUE_ORCHESTRATOR_AUTH_EMAIL_UPDATED, true);
    }

    @Bean
    public Queue authEmailUpdateFailedQueue() {
        return new Queue(UpdateEmailConstants.QUEUE_ORCHESTRATOR_AUTH_EMAIL_FAILED, true);
    }

    @Bean
    public Binding emailUpdateRequestedBinding() {
        return BindingBuilder.bind(emailUpdateRequestedQueue())
                .to(sagaExchange())
                .with(UpdateEmailConstants.EVENT_EMAIL_UPDATE_REQUESTED);
    }

    @Bean
    public Binding authEmailUpdatedBinding() {
        return BindingBuilder.bind(authEmailUpdatedQueue())
                .to(sagaExchange())
                .with(UpdateEmailConstants.EVENT_AUTH_EMAIL_UPDATED);
    }

    @Bean
    public Binding authEmailUpdateFailedBinding() {
        return BindingBuilder.bind(authEmailUpdateFailedQueue())
                .to(sagaExchange())
                .with(UpdateEmailConstants.EVENT_AUTH_EMAIL_FAILED);
    }

    @Bean
    public Queue disableUserRequestedQueue() {
        return new Queue(DisableUserConstants.QUEUE_ORCHESTRATOR_DISABLE_USER_REQUESTED, true);
    }

    @Bean
    public Binding disableUserRequestedBinding() {
        return BindingBuilder.bind(disableUserRequestedQueue())
                .to(sagaExchange())
                .with(DisableUserConstants.EVENT_DISABLE_USER_REQUESTED);
    }

    @Bean
    public Queue disableUserFailedQueue() {
        return new Queue(DisableUserConstants.QUEUE_ORCHESTRATOR_DISABLE_USER, true);
    }

    @Bean
    public Binding disableUserFailedBinding() {
        return BindingBuilder.bind(disableUserFailedQueue())
                .to(sagaExchange())
                .with(DisableUserConstants.EVENT_DISABLE_USER);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
