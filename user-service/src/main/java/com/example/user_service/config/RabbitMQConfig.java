package com.example.user_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public Queue profileCreateQueue() {
        return new Queue(RegisterConstants.QUEUE_PROFILE_CREATE, true);
    }

    @Bean
    public Binding profileCreateBinding() {
        return BindingBuilder.bind(profileCreateQueue())
                .to(sagaExchange())
                .with(RegisterConstants.COMMAND_PROFILE_CREATE);
    }

    @Bean
    public Queue confirmEmailUpdateQueue() {
        return new Queue(UpdateEmailConstants.QUEUE_USER_EMAIL_CONFIRM, true);
    }

    @Bean
    public Queue discardEmailUpdateQueue() {
        return new Queue(UpdateEmailConstants.QUEUE_USER_EMAIL_DISCARD, true);
    }

    @Bean
    public Binding confirmEmailUpdateBinding() {
        return BindingBuilder.bind(confirmEmailUpdateQueue())
                .to(sagaExchange())
                .with(UpdateEmailConstants.COMMAND_USER_EMAIL_CONFIRM);
    }

    @Bean
    public Binding discardEmailUpdateBinding() {
        return BindingBuilder.bind(discardEmailUpdateQueue())
                .to(sagaExchange())
                .with(UpdateEmailConstants.COMMAND_USER_EMAIL_DISCARD);
    }

    @SuppressWarnings("removal")
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
