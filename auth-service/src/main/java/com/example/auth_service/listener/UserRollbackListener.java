package com.example.auth_service.listener;

import com.example.auth_service.repository.UserRepository;
import com.example.common_dto.command.RollbackAuthCommand;
import com.example.common_dto.constant.RegisterConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRollbackListener {

    private final UserRepository userRepository;

    @RabbitListener(queues = RegisterConstants.QUEUE_AUTH_ROLLBACK)
    @Transactional
    public void handleRollbackAuth(RollbackAuthCommand command) {
        log.warn("Received RollbackAuthCommand for userId: {}", command.getUserId());

        userRepository.deleteById(command.getUserId());

        log.info("User {} deleted successfully (compensation)", command.getUserId());
    }
}
