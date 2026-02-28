package com.example.user_service.user.infrastructure.adapter.in.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.common_dto.constant.DisableUserConstants;
import com.example.common_dto.event.UserDisabledEvent;
import com.example.user_service.user.application.port.out.UserPersistencePort;
import com.example.user_service.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDeletionListener {

    private final UserPersistencePort userPersistencePort;

    @RabbitListener(queues = DisableUserConstants.QUEUE_USER_DISABLE_USER_CONFIRM)
    public void handleUserDisableConfirmEvent(UserDisabledEvent event) {
        log.info("Received disable user confirm command for userId: {}", event.getUserId());
        try {
            User user = userPersistencePort.findByUserId(event.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setDisabled(true);
            user.setDisableAt(LocalDateTime.now());
            userPersistencePort.save(user);

            log.info("Successfully disabled user profile for userId: {}", event.getUserId());
        } catch (Exception e) {
            log.error("Failed to disable user profile for userId: {}. Error: {}", event.getUserId(), e.getMessage(), e);
        }
    }
}
