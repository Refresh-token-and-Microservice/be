package com.example.orchestrator_service.config;

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

    // Queues
    public static final String AUTH_REGISTERED_QUEUE = "auth.registered.queue";
    public static final String PROFILE_CREATED_QUEUE = "profile.created.queue";
    public static final String PROFILE_FAILED_QUEUE = "profile.failed.queue";

    // Routing Keys
    public static final String AUTH_USER_REGISTERED = "auth.user.registered";
    public static final String ORCHESTRATOR_PROFILE_CREATE = "orchestrator.profile.create";
    public static final String USER_PROFILE_CREATED = "user.profile.created";
    public static final String USER_PROFILE_FAILED = "user.profile.failed";
    public static final String ORCHESTRATOR_USER_ACTIVATE = "orchestrator.user.activate";
    public static final String ORCHESTRATOR_AUTH_ROLLBACK = "orchestrator.auth.rollback";

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE);
    }

    @Bean
    public Queue authRegisteredQueue() {
        return new Queue(AUTH_REGISTERED_QUEUE, true);
    }

    @Bean
    public Queue profileCreatedQueue() {
        return new Queue(PROFILE_CREATED_QUEUE, true);
    }

    @Bean
    public Queue profileFailedQueue() {
        return new Queue(PROFILE_FAILED_QUEUE, true);
    }

    @Bean
    public Binding authRegisteredBinding() {
        return BindingBuilder.bind(authRegisteredQueue())
                .to(sagaExchange())
                .with(AUTH_USER_REGISTERED);
    }

    @Bean
    public Binding profileCreatedBinding() {
        return BindingBuilder.bind(profileCreatedQueue())
                .to(sagaExchange())
                .with(USER_PROFILE_CREATED);
    }

    @Bean
    public Binding profileFailedBinding() {
        return BindingBuilder.bind(profileFailedQueue())
                .to(sagaExchange())
                .with(USER_PROFILE_FAILED);
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
