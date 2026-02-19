package com.example.user_service.listener;

import com.example.common_dto.command.DisableUserCommand;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDeletionListener {

    private final UserRepository userRepository;

    @RabbitListener(queues = RabbitMQConstants.AUTH_USER_DISABLED_QUEUE)
    @Transactional
    public void handleCreateProfile(DisableUserCommand command) {
        log.info("Received UserDeletedEvent: {}", command);

        userRepository.deleteById(Long.valueOf(command.getUserId()));
    }
}
