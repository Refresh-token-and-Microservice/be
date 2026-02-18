package com.example.user_service.config;

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
    public Queue profileCreateQueue() {
        return new Queue(RabbitMQConstants.PROFILE_CREATE_QUEUE, true);
    }

    @Bean
    public Binding profileCreateBinding() {
        return BindingBuilder.bind(profileCreateQueue())
                .to(sagaExchange())
                .with(RabbitMQConstants.ORCHESTRATOR_PROFILE_CREATE);
    }

    @Bean
    public Queue confirmEmailUpdateQueue() {
        return new Queue(RabbitMQConstants.CONFIRM_EMAIL_UPDATE_QUEUE, true);
    }

    @Bean
    public Queue discardEmailUpdateQueue() {
        return new Queue(RabbitMQConstants.DISCARD_EMAIL_UPDATE_QUEUE, true);
    }

    @Bean
    public Binding confirmEmailUpdateBinding() {
        return BindingBuilder.bind(confirmEmailUpdateQueue())
                .to(sagaExchange())
                .with(RabbitMQConstants.ORCHESTRATOR_USER_CONFIRM_EMAIL);
    }

    @Bean
    public Binding discardEmailUpdateBinding() {
        return BindingBuilder.bind(discardEmailUpdateQueue())
                .to(sagaExchange())
                .with(RabbitMQConstants.ORCHESTRATOR_USER_DISCARD_EMAIL);
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
