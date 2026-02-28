package com.example.auth_service.auth.infrastructure.adapter.in.messaging;

import com.example.auth_service.auth.domain.User;
import com.example.auth_service.auth.application.port.out.AuthPersistencePort;
import com.example.common_dto.command.DisableAuthUserCommand;
import com.example.common_dto.constant.DisableUserConstants;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.event.UserDisabledEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDisableListener {

    private final AuthPersistencePort authPersistencePort;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = DisableUserConstants.QUEUE_AUTH_DISABLE_USER)
    @Transactional
    public void handleUserDisable(DisableAuthUserCommand command) {
        log.warn("Received UserDisableRequestEvent for userId: {}", command.getUserId());

        Optional<User> user = authPersistencePort.findUserById(Integer.valueOf(command.getUserId()));

        if (user.isPresent()) {
            user.get().setDisabled(true);
            user.get().setDisableAt(LocalDateTime.now());

            authPersistencePort.saveUser(user.get());

            UserDisabledEvent disabledEvent = UserDisabledEvent.builder()
                    .transactionId(command.getTransactionId())
                    .userId(command.getUserId())
                    .build();
            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE, DisableUserConstants.EVENT_DISABLE_USER,
                    disabledEvent);
        }

    }
}
