package com.example.user_service.service.impl;

import org.springframework.stereotype.Service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

        private final UserRepository userRepository;
        private final com.example.user_service.mapper.UserMapper userMapper;

        @Override
        public UserDto saveUser(UserDto userDto) {
                User user = userMapper.toEntity(userDto);
                User savedUser = userRepository.save(user);
                return userMapper.toDto(savedUser);
        }

        @Override
        public UserDto getUserById(String userId) {
                User user = userRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return userMapper.toDto(user);
        }
}
