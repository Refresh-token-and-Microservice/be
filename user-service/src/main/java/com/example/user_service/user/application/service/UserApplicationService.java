package com.example.user_service.user.application.service;

import org.springframework.stereotype.Service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.user.domain.User;
import com.example.user_service.user.application.port.in.UserUseCase;
import com.example.user_service.user.application.port.out.UserPersistencePort;
import com.example.common_dto.constant.DisableUserConstants;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.constant.UpdateEmailConstants;
import com.example.common_dto.event.EmailUpdateRequestedEvent;
import com.example.common_dto.event.UserDisableRequestEvent;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserApplicationService implements UserUseCase {

    private final UserPersistencePort userPersistencePort;
    private final com.example.user_service.user.infrastructure.mapper.UserMapper userMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User savedUser = userPersistencePort.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto getUserById(Integer userId) {
        User user = userPersistencePort.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userPersistencePort.findAll().stream()
                .map(userMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        User user = userPersistencePort.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String oldEmail = user.getEmail();
        String newEmail = userDto.getEmail();
        String emailStatus = null;

        if (newEmail != null && !newEmail.equals(oldEmail)) {
            user.setPendingEmail(newEmail);
            emailStatus = "PENDING_UPDATE";

            EmailUpdateRequestedEvent event = EmailUpdateRequestedEvent.builder()
                    .userId(userId)
                    .oldEmail(oldEmail)
                    .newEmail(newEmail)
                    .build();
            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE,
                    UpdateEmailConstants.EVENT_EMAIL_UPDATE_REQUESTED, event);

            userDto.setEmail(null);
        }

        userMapper.updateEntityFromDto(userDto, user);
        User updatedUser = userPersistencePort.save(user);

        UserDto responseDto = userMapper.toDto(updatedUser);
        if (emailStatus != null) {
            responseDto.setEmailStatus(emailStatus);
        }
        return responseDto;
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = userPersistencePort.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user != null) {
            UserDisableRequestEvent event = UserDisableRequestEvent.builder()
                    .userId(userId)
                    .build();
            rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE,
                    DisableUserConstants.EVENT_DISABLE_USER_REQUESTED, event);
        }
    }

    @Override
    public void confirmEmailUpdate(Integer userId, String email) {
        User user = userPersistencePort.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (email.equals(user.getPendingEmail())) {
            user.setEmail(email);
            user.setPendingEmail(null);
            userPersistencePort.save(user);
        }
    }

    @Override
    public void discardEmailUpdate(Integer userId) {
        User user = userPersistencePort.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPendingEmail(null);
        userPersistencePort.save(user);
    }
}
