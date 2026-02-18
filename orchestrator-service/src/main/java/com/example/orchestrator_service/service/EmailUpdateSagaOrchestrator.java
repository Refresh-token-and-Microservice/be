package com.example.orchestrator_service.service;

import com.example.common_dto.command.ConfirmEmailUpdateCommand;
import com.example.common_dto.command.DiscardEmailUpdateCommand;
import com.example.common_dto.command.UpdateAuthEmailCommand;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.event.AuthEmailUpdateFailedEvent;
import com.example.common_dto.event.AuthEmailUpdatedEvent;
import com.example.common_dto.event.EmailUpdateRequestedEvent;
import com.example.orchestrator_service.entity.SagaInstance;
import com.example.orchestrator_service.repository.SagaInstanceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailUpdateSagaOrchestrator {

    private final SagaInstanceRepository sagaInstanceRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConstants.EMAIL_UPDATE_REQUESTED_QUEUE)
    @Transactional
    public void handleEmailUpdateRequested(EmailUpdateRequestedEvent event) {
        log.info("Received EmailUpdateRequestedEvent for user: {}", event.getUserId());

        try {
            // Persist State: STARTED
            SagaInstance sagaInstance = SagaInstance.builder()
                    .userId(event.getUserId())
                    .transactionId(event.getUserId()) // Using userId as transactionId for simplicity in this flow
                                                      // basically
                    .payload(objectMapper.writeValueAsString(event))
                    .status("STARTED")
                    .step("EMAIL_UPDATE_REQUESTED")
                    .build();
            sagaInstanceRepository.save(sagaInstance);

            // Create command
            UpdateAuthEmailCommand command = UpdateAuthEmailCommand.builder()
                    .userId(event.getUserId())
                    .newEmail(event.getNewEmail())
                    .build();

            // Send to Auth Service
            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE,
                    RabbitMQConstants.ORCHESTRATOR_AUTH_EMAIL_UPDATE, command);

        } catch (JsonProcessingException e) {
            log.error("Error serializing event payload", e);
            // Handle error, maybe retry or discard
        }
    }

    @RabbitListener(queues = RabbitMQConstants.AUTH_EMAIL_UPDATED_QUEUE)
    @Transactional
    public void handleAuthEmailUpdated(AuthEmailUpdatedEvent event) {
        log.info("Received AuthEmailUpdatedEvent for user: {}", event.getUserId());

        SagaInstance sagaInstance = sagaInstanceRepository.findByUserIdAndStatus(event.getUserId(), "STARTED")
                .orElse(null); // Should find by transactionId if possible, but userId works if 1 active saga
                               // per user

        if (sagaInstance != null) {
            sagaInstance.setStatus("AUTH_UPDATED");
            sagaInstance.setStep("AUTH_EMAIL_UPDATED");
            sagaInstanceRepository.save(sagaInstance);

            // Create command
            ConfirmEmailUpdateCommand command = ConfirmEmailUpdateCommand.builder()
                    .userId(event.getUserId())
                    .email(event.getEmail())
                    .build();

            // Send to User Service
            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE,
                    RabbitMQConstants.ORCHESTRATOR_USER_CONFIRM_EMAIL, command);

            // Mark as COMPLETED after sending command (optimistic)
            sagaInstance.setStatus("COMPLETED");
            sagaInstance.setStep("COMPLETED");
            sagaInstanceRepository.save(sagaInstance);
        } else {
            log.warn("Saga instance not found for userId: {}", event.getUserId());
        }
    }

    @RabbitListener(queues = RabbitMQConstants.AUTH_EMAIL_UPDATE_FAILED_QUEUE)
    @Transactional
    public void handleAuthEmailUpdateFailed(AuthEmailUpdateFailedEvent event) {
        log.info("Received AuthEmailUpdateFailedEvent for user: {}", event.getUserId());

        SagaInstance sagaInstance = sagaInstanceRepository.findByUserIdAndStatus(event.getUserId(), "STARTED")
                .orElse(null);

        if (sagaInstance != null) {
            sagaInstance.setStatus("FAILED");
            sagaInstance.setStep("AUTH_EMAIL_UPDATE_FAILED");
            sagaInstanceRepository.save(sagaInstance);

            // Create command
            DiscardEmailUpdateCommand command = DiscardEmailUpdateCommand.builder()
                    .userId(event.getUserId())
                    .build();

            // Send to User Service
            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE,
                    RabbitMQConstants.ORCHESTRATOR_USER_DISCARD_EMAIL, command);
        } else {
            log.warn("Saga instance not found for userId: {}", event.getUserId());
        }
    }
}
