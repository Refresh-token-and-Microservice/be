package com.example.auth_service.auth.infrastructure.adapter.in.messaging;

import com.example.auth_service.auth.application.port.in.AuthUseCase;
import com.example.common_dto.command.UpdateAuthEmailCommand;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.constant.UpdateEmailConstants;
import com.example.common_dto.event.AuthEmailUpdateFailedEvent;
import com.example.common_dto.event.AuthEmailUpdatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEmailUpdateListener {

    private final AuthUseCase authUseCase;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = UpdateEmailConstants.QUEUE_AUTH_EMAIL_UPDATE)
    public void handleUpdateAuthEmail(UpdateAuthEmailCommand command) {
        log.info("Received UpdateAuthEmailCommand for user: {}", command.getUserId());
        try {
            Integer userId = Integer.valueOf(command.getUserId());
            authUseCase.updateEmail(userId, command.getNewEmail());

            AuthEmailUpdatedEvent event = AuthEmailUpdatedEvent.builder()
                    .userId(command.getUserId())
                    .email(command.getNewEmail())
                    .build();
            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE,
                    UpdateEmailConstants.EVENT_AUTH_EMAIL_UPDATED,
                    event);
            log.info("Published AuthEmailUpdatedEvent for user: {}", command.getUserId());

        } catch (Exception e) {
            log.error("Failed to update email for user: {}", command.getUserId(), e);
            AuthEmailUpdateFailedEvent event = AuthEmailUpdateFailedEvent.builder()
                    .userId(command.getUserId())
                    .email(command.getNewEmail())
                    .reason(e.getMessage())
                    .build();
            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE, UpdateEmailConstants.EVENT_AUTH_EMAIL_FAILED,
                    event);
        }
    }
}
