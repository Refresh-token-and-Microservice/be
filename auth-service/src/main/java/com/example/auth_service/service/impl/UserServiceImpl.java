package com.example.auth_service.service.impl;

import com.example.auth_service.dto.request.UserRequest;
import com.example.auth_service.dto.response.UserResponse;
import com.example.auth_service.entity.User;
import com.example.auth_service.mapper.UserMapper;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.UserService;
import com.example.common_dto.constant.RabbitMQConstants;
import com.example.common_dto.constant.RegisterConstants;
import com.example.common_dto.event.AuthRegisteredEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.example.auth_service.repository.RoleRepository roleRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public UserResponse register(UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus("PENDING"); // Set initial status as PENDING

        // Handle Role
        String roleName = userRequest.getRole();
        if (roleName == null || roleName.isEmpty()) {
            roleName = "EMPLOYEE"; // Default role
        } else {
            roleName = roleName.toUpperCase();
            if (!roleName.equals("ADMIN") && !roleName.equals("EMPLOYEE")) {
                throw new RuntimeException("Invalid role! Role must be ADMIN or EMPLOYEE");
            }
        }

        String finalRoleName = roleName;
        com.example.auth_service.entity.Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository
                        .save(com.example.auth_service.entity.Role.builder().roleName(finalRoleName).build()));

        user.setRoles(java.util.Collections.singleton(role));

        // Save user first (within transaction)
        User savedUser = userRepository.save(user);

        // Then publish event (still within transaction)
        AuthRegisteredEvent event = AuthRegisteredEvent.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConstants.SAGA_EXCHANGE, RegisterConstants.EVENT_AUTH_USER_REGISTERED,
                event);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public Optional<UserResponse> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            if (!userOpt.get().isEnabled()) {
                return Optional.empty();
            }
            return Optional.of(userMapper.toResponse(userOpt.get()));
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public UserResponse updateEmail(Integer userId, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        user.setEmail(newEmail);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
