package com.example.orchestrator_service.service;

import com.example.common_dto.command.DisableAuthUserCommand;
import com.example.common_dto.command.DisableUserCommand;
import com.example.common_dto.constant.DisableUserConstants;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.event.UserDisableRequestEvent;
import com.example.common_dto.event.UserDisabledEvent;
import com.example.orchestrator_service.entity.SagaInstance;
import com.example.orchestrator_service.enums.DisableUserSteps;
import com.example.orchestrator_service.enums.Status;
import com.example.orchestrator_service.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisableUserSagaOrchestrator {
    private final SagaInstanceRepository sagaInstanceRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = DisableUserConstants.QUEUE_ORCHESTRATOR_DISABLE_USER_REQUESTED)
    @Transactional
    public void handleUserDisableRequest(UserDisableRequestEvent event) {
        log.info("Received user disable request: {}", event);

        String transactionId = UUID.randomUUID().toString();

        SagaInstance sagaInstance = SagaInstance.builder()
                .userId(event.getUserId())
                .transactionId(transactionId)
                .payload(String.valueOf(event.getUserId()))
                .status(Status.STARTED)
                .step(DisableUserSteps.DISABLE_USER_REQUESTED.name())
                .build();
        sagaInstanceRepository.save(sagaInstance);

        DisableAuthUserCommand command = DisableAuthUserCommand.builder()
                .transactionId(transactionId)
                .userId(event.getUserId())
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE,
                DisableUserConstants.COMMAND_DISABLE_USER, command);
    }

    @RabbitListener(queues = DisableUserConstants.QUEUE_ORCHESTRATOR_DISABLE_USER)
    @Transactional
    public void handleAuthUserDisabled(UserDisabledEvent event) {
        log.info("Received auth user disabled event: {}", event);

        SagaInstance sagaInstance = sagaInstanceRepository.findByTransactionId(event.getTransactionId())
                .orElse(null);

        if (sagaInstance != null) {
            sagaInstance.setStatus(Status.COMPLETED);
            sagaInstance.setStep(DisableUserSteps.DISABLE_USER.name());
            sagaInstanceRepository.save(sagaInstance);

            DisableUserCommand command = DisableUserCommand.builder()
                    .transactionId(event.getTransactionId())
                    .userId(event.getUserId())
                    .build();

            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE,
                    DisableUserConstants.COMMAND_DISABLE_USER_CONFIRM, command);
        }
    }
}
