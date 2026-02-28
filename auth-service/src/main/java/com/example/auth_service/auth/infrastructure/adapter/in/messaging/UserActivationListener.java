package com.example.auth_service.auth.infrastructure.adapter.in.messaging;

import com.example.auth_service.auth.domain.User;
import com.example.auth_service.auth.application.port.out.AuthPersistencePort;
import com.example.common_dto.command.ActivateUserCommand;
import com.example.common_dto.constant.RegisterConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActivationListener {

    private final AuthPersistencePort authPersistencePort;

    @RabbitListener(queues = RegisterConstants.QUEUE_USER_ACTIVATE)
    @Transactional
    public void handleActivateUser(ActivateUserCommand command) {
        log.info("Received ActivateUserCommand: {}", command);

        User user = authPersistencePort.findUserById(command.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + command.getUserId()));

        user.setStatus("ACTIVE");
        authPersistencePort.saveUser(user);

        log.info("User {} activated successfully", command.getUserId());
    }
}
