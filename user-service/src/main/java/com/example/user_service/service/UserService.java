package com.example.user_service.service;

import com.example.user_service.dto.UserDto;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    UserDto getUserById(String userId);
}
