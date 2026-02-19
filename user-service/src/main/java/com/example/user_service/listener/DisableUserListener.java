package com.example.user_service.listener;

import com.example.common_dto.command.DisableUserCommand;
import com.example.common_dto.constant.DisableUserConstants;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DisableUserListener {

    private final UserRepository userRepository;

    @RabbitListener(queues = DisableUserConstants.QUEUE_USER_DISABLE_USER_CONFIRM)
    @Transactional
    public void handleCreateProfile(DisableUserCommand command) {
        log.info("Received UserDeletedEvent: {}", command);

        Optional<User> user = userRepository.findByUserId(command.getUserId());

        if (user != null) {
            user.get().setDisabled(true);
            user.get().setDisableAt(LocalDateTime.now());
            userRepository.save(user.get());
        }
    }
}
