package com.example.orchestrator_service.service;

import com.example.common_dto.command.DisableUserCommand;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.event.UserDisableRequestEvent;
import com.example.common_dto.event.UserDisabledEvent;
import com.example.orchestrator_service.entity.SagaInstance;
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

    @RabbitListener(queues = RabbitMQConstants.USER_DISABLE_REQUESTED_QUEUE)
    @Transactional
    public void handleUserDisableRequest(UserDisableRequestEvent event) {
        log.info("Received user disable request: {}", event);

        String transactionId = UUID.randomUUID().toString();

        SagaInstance sagaInstance = SagaInstance.builder()
                .userId(event.getUserId())
                .transactionId(transactionId)
                .payload(event.getUserId())
                .status("STARTED")
                .step("USER_DISABLE_REQUESTED")
                .build();
        sagaInstanceRepository.save(sagaInstance);

        DisableUserCommand command = DisableUserCommand.builder()
                .transactionId(transactionId)
                .userId(event.getUserId())
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE,
                RabbitMQConstants.ORCHESTRATOR_AUTH_USER_DISABLE, command);
    }

    @RabbitListener(queues = RabbitMQConstants.AUTH_USER_DISABLED_QUEUE)
    @Transactional
    public void handleAuthUserDisabled(UserDisabledEvent event) {
        log.info("Received auth user disabled event: {}", event);

        SagaInstance sagaInstance = sagaInstanceRepository.findByTransactionId(event.getTransactionId());

        if (sagaInstance != null) {
            sagaInstance.setStatus("COMPLETED");
            sagaInstance.setStep("AUTH_USER_DISABLED");
            sagaInstanceRepository.save(sagaInstance);
        }
    }
}
