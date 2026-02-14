package com.example.orchestrator_service.service;

import com.example.common.command.ActivateUserCommand;
import com.example.common.command.CreateProfileCommand;
import com.example.common.command.RollbackAuthCommand;
import com.example.common.event.AuthRegisteredEvent;
import com.example.common.event.ProfileCreatedEvent;
import com.example.common.event.ProfileFailedEvent;
import com.example.orchestrator.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestrator {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Step 1: Listen for AuthRegisteredEvent from auth-service
     * Then send CreateProfileCommand to user-service
     */
    @RabbitListener(queues = RabbitMQConfig.AUTH_REGISTERED_QUEUE)
    public void handleAuthRegistered(AuthRegisteredEvent event) {
        log.info("Received AuthRegisteredEvent: {}", event);

        try {
            CreateProfileCommand command = CreateProfileCommand.builder()
                    .userId(event.getUserId())
                    .email(event.getEmail())
                    .build();

            log.info("Sending CreateProfileCommand to user-service: {}", command);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.SAGA_EXCHANGE,
                    RabbitMQConfig.ORCHESTRATOR_PROFILE_CREATE,
                    command);
        } catch (Exception e) {
            log.error("Error handling AuthRegisteredEvent", e);
            // Could trigger rollback here if needed
        }
    }

    /**
     * Step 2a (SUCCESS): Listen for ProfileCreatedEvent from user-service
     * Then send ActivateUserCommand to auth-service
     */
    @RabbitListener(queues = RabbitMQConfig.PROFILE_CREATED_QUEUE)
    public void handleProfileCreated(ProfileCreatedEvent event) {
        log.info("Received ProfileCreatedEvent: {}", event);

        ActivateUserCommand command = ActivateUserCommand.builder()
                .userId(event.getUserId())
                .build();

        log.info("Sending ActivateUserCommand to auth-service: {}", command);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_EXCHANGE,
                RabbitMQConfig.ORCHESTRATOR_USER_ACTIVATE,
                command);

        log.info("Saga completed successfully for userId: {}", event.getUserId());
    }

    /**
     * Step 2b (FAILURE): Listen for ProfileFailedEvent from user-service
     * Then send RollbackAuthCommand to auth-service (COMPENSATION)
     */
    @RabbitListener(queues = RabbitMQConfig.PROFILE_FAILED_QUEUE)
    public void handleProfileFailed(ProfileFailedEvent event) {
        log.error("Received ProfileFailedEvent: {} - Reason: {}", event.getUserId(), event.getReason());

        RollbackAuthCommand command = RollbackAuthCommand.builder()
                .userId(event.getUserId())
                .build();

        log.info("Sending RollbackAuthCommand to auth-service for compensation: {}", command);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SAGA_EXCHANGE,
                RabbitMQConfig.ORCHESTRATOR_AUTH_ROLLBACK,
                command);

        log.info("Saga rolled back for userId: {}", event.getUserId());
    }
}
