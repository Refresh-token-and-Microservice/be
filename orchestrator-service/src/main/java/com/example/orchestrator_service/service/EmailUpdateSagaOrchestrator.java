package com.example.orchestrator_service.service;

import com.example.common_dto.command.ConfirmEmailUpdateCommand;
import com.example.common_dto.command.DiscardEmailUpdateCommand;
import com.example.common_dto.command.UpdateAuthEmailCommand;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.constant.UpdateEmailConstants;
import com.example.common_dto.event.AuthEmailUpdateFailedEvent;
import com.example.common_dto.event.AuthEmailUpdatedEvent;
import com.example.common_dto.event.EmailUpdateRequestedEvent;
import com.example.orchestrator_service.entity.SagaInstance;
import com.example.orchestrator_service.enums.Status;
import com.example.orchestrator_service.enums.UpdateEmailSteps;
import com.example.orchestrator_service.repository.SagaInstanceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

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

    @RabbitListener(queues = UpdateEmailConstants.QUEUE_ORCHESTRATOR_EMAIL_UPDATE_REQUESTED)
    @Transactional
    public void handleEmailUpdateRequested(EmailUpdateRequestedEvent event) {
        log.info("Received EmailUpdateRequestedEvent for user: {}", event.getUserId());

        String transactionId = UUID.randomUUID().toString();

        try {
            // Persist State: STARTED
            SagaInstance sagaInstance = SagaInstance.builder()
                    .userId(event.getUserId())
                    .transactionId(transactionId)
                    .payload(objectMapper.writeValueAsString(event))
                    .status(Status.STARTED)
                    .step(UpdateEmailSteps.EMAIL_UPDATE_REQUESTED.name())
                    .build();
            sagaInstanceRepository.save(sagaInstance);

            UpdateAuthEmailCommand command = UpdateAuthEmailCommand.builder()
                    .transactionId(transactionId)
                    .userId(event.getUserId())
                    .newEmail(event.getNewEmail())
                    .build();

            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE,
                    UpdateEmailConstants.COMMAND_AUTH_EMAIL_UPDATE, command);

        } catch (JsonProcessingException e) {
            log.error("Error serializing event payload", e);
            // Handle error, maybe retry or discard
        }
    }

    @RabbitListener(queues = UpdateEmailConstants.QUEUE_ORCHESTRATOR_AUTH_EMAIL_UPDATED)
    @Transactional
    public void handleAuthEmailUpdated(AuthEmailUpdatedEvent event) {
        log.info("Received AuthEmailUpdatedEvent for user: {}", event.getUserId());

        SagaInstance sagaInstance = sagaInstanceRepository.findByTransactionId(event.getTransactionId()).orElse(null);

        if (sagaInstance != null) {
            sagaInstance.setStatus(Status.PROCESSING);
            sagaInstance.setStep(UpdateEmailSteps.AUTH_EMAIL_UPDATED.name());
            sagaInstanceRepository.save(sagaInstance);

            ConfirmEmailUpdateCommand command = ConfirmEmailUpdateCommand.builder()
                    .userId(event.getUserId())
                    .email(event.getEmail())
                    .build();

            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE,
                    UpdateEmailConstants.COMMAND_USER_EMAIL_CONFIRM, command);

            sagaInstance.setStatus(Status.COMPLETED);
            sagaInstance.setStep(UpdateEmailSteps.CONFIRM_EMAIL_UPDATE.name());
            sagaInstanceRepository.save(sagaInstance);
        } else {
            log.warn("Saga instance not found for userId: {}", event.getUserId());
        }
    }

    @RabbitListener(queues = UpdateEmailConstants.QUEUE_ORCHESTRATOR_AUTH_EMAIL_FAILED)
    @Transactional
    public void handleAuthEmailUpdateFailed(AuthEmailUpdateFailedEvent event) {
        log.info("Received AuthEmailUpdateFailedEvent for user: {}", event.getUserId());

        SagaInstance sagaInstance = sagaInstanceRepository.findByTransactionId(event.getTransactionId()).orElse(null);

        if (sagaInstance != null) {
            sagaInstance.setStatus(Status.FAILED);
            sagaInstance.setStep(UpdateEmailSteps.AUTH_EMAIL_UPDATE_FAILED.name());
            sagaInstanceRepository.save(sagaInstance);

            DiscardEmailUpdateCommand command = DiscardEmailUpdateCommand.builder()
                    .userId(event.getUserId())
                    .build();

            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE,
                    UpdateEmailConstants.COMMAND_USER_EMAIL_DISCARD, command);
        } else {
            log.warn("Saga instance not found for userId: {}", event.getUserId());
        }
    }
}
