package com.example.user_service.listener;

import com.example.common_dto.command.CreateProfileCommand;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.event.ProfileCreatedEvent;
import com.example.common_dto.event.ProfileFailedEvent;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileCreationListener {

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConstants.PROFILE_CREATE_QUEUE)
    @Transactional
    public void handleCreateProfile(CreateProfileCommand command) {
        log.info("Received CreateProfileCommand: {}", command);

        try {
            // Create user profile with userId from auth service
            User user = User.builder()
                    .userId(String.valueOf(command.getUserId()))
                    .email(command.getEmail())
                    // firstName, lastName, phone are null as mentioned
                    .build();

            User savedUser = userRepository.save(user);
            log.info("User profile created successfully: {}", savedUser);

            // Publish success event
            ProfileCreatedEvent event = ProfileCreatedEvent.builder()
                    .userId(command.getUserId())
                    .build();

            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE, RabbitMQConstants.USER_PROFILE_CREATED,
                    event);
            log.info("Published ProfileCreatedEvent: {}", event);

        } catch (Exception e) {
            log.error("Failed to create user profile for userId: {}", command.getUserId(), e);

            // Publish failure event
            ProfileFailedEvent event = ProfileFailedEvent.builder()
                    .userId(command.getUserId())
                    .reason(e.getMessage())
                    .build();

            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE, RabbitMQConstants.USER_PROFILE_FAILED,
                    event);
            log.info("Published ProfileFailedEvent: {}", event);
        }
    }
}
