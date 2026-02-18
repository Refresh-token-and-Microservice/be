package com.example.user_service.service;

import com.example.user_service.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    UserDto getUserById(String userId);

    List<UserDto> getAllUsers();

    UserDto updateUser(String userId, UserDto userDto);

    void deleteUser(String userId);

    void confirmEmailUpdate(String userId, String email);

    void discardEmailUpdate(String userId);
}
