package com.example.auth_service.listener;

import com.example.auth_service.entity.User;
import com.example.auth_service.repository.UserRepository;
import com.example.common_dto.command.ActivateUserCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActivationListener {

    private final UserRepository userRepository;

    @RabbitListener(queues = "user.activate.queue")
    @Transactional
    public void handleActivateUser(ActivateUserCommand command) {
        log.info("Received ActivateUserCommand: {}", command);

        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + command.getUserId()));

        user.setStatus("ACTIVE");
        userRepository.save(user);

        log.info("User {} activated successfully", command.getUserId());
    }
}
