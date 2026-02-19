package com.example.auth_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.constant.RegisterConstants;
import com.example.common_dto.constant.UpdateEmailConstants;;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(RabbitMQConstants.SAGA_EXCHANGE);
    }

    @Bean
    public Queue userActivateQueue() {
        return new Queue(RegisterConstants.QUEUE_USER_ACTIVATE, true);
    }

    @Bean
    public Queue authRollbackQueue() {
        return new Queue(RegisterConstants.QUEUE_AUTH_ROLLBACK, true);
    }

    @Bean
    public Binding userActivateBinding() {
        return BindingBuilder.bind(userActivateQueue())
                .to(sagaExchange())
                .with(RegisterConstants.COMMAND_USER_ACTIVATE);
    }

    @Bean
    public Binding authRollbackBinding() {
        return BindingBuilder.bind(authRollbackQueue())
                .to(sagaExchange())
                .with(RegisterConstants.COMMAND_AUTH_ROLLBACK);
    }

    @Bean
    public Queue updateAuthEmailQueue() {
        return new Queue(UpdateEmailConstants.QUEUE_AUTH_EMAIL_UPDATE, true);
    }

    @Bean
    public Binding updateAuthEmailBinding() {
        return BindingBuilder.bind(updateAuthEmailQueue())
                .to(sagaExchange())
                .with(UpdateEmailConstants.COMMAND_AUTH_EMAIL_UPDATE);
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
