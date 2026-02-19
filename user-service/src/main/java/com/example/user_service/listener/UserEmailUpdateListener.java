package com.example.user_service.listener;

import com.example.common_dto.command.ConfirmEmailUpdateCommand;
import com.example.common_dto.command.DiscardEmailUpdateCommand;
import com.example.common_dto.constant.UpdateEmailConstants;
import com.example.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEmailUpdateListener {

    private final UserService userService;

    @RabbitListener(queues = UpdateEmailConstants.QUEUE_USER_EMAIL_CONFIRM)
    public void handleConfirmEmailUpdate(ConfirmEmailUpdateCommand command) {
        log.info("Received ConfirmEmailUpdateCommand for user: {}", command.getUserId());
        userService.confirmEmailUpdate(command.getUserId(), command.getEmail());
    }

    @RabbitListener(queues = UpdateEmailConstants.QUEUE_USER_EMAIL_DISCARD)
    public void handleDiscardEmailUpdate(DiscardEmailUpdateCommand command) {
        log.info("Received DiscardEmailUpdateCommand for user: {}", command.getUserId());
        userService.discardEmailUpdate(command.getUserId());
    }
}
