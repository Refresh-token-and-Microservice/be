package com.example.api_gateway.service.impl;

import com.example.api_gateway.dto.request.UserRequest;
import com.example.api_gateway.dto.response.UserResponse;
import com.example.api_gateway.entity.User;
import com.example.api_gateway.mapper.UserMapper;
import com.example.api_gateway.repository.UserRepository;
import com.example.api_gateway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserResponse register(UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public Optional<UserResponse> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return Optional.of(userMapper.toResponse(userOpt.get()));
        }

        return Optional.empty();
    }
}
