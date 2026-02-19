package com.example.auth_service.listener;

import com.example.auth_service.entity.User;
import com.example.auth_service.repository.UserRepository;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.event.UserDisableRequestEvent;
import com.example.common_dto.event.UserDisabledEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
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

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConstants.USER_DISABLE_REQUESTED_QUEUE)
    @Transactional
    public void handleUserDisable(UserDisableRequestEvent event) {
        log.warn("Received UserDisableRequestEvent for userId: {}", event.getUserId());

        Optional<User> user = userRepository.findById(Integer.valueOf(event.getUserId()));

        if (user != null) {
            user.get().setIsDisabled(true);
            user.get().setDisableAt(LocalDateTime.now());

            userRepository.save(user.get());

            UserDisabledEvent disabledEvent = UserDisabledEvent.builder()
                    .userId(event.getUserId())
                    .build();
            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE, RabbitMQConstants.AUTH_USER_DISABLED,
                    disabledEvent);
        }

        log.info("User {} disabled successfully (compensation)", event.getUserId());
    }
}
