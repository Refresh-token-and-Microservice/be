package com.example.user_service.service;

import com.example.user_service.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    UserDto getUserById(Integer userId);

    List<UserDto> getAllUsers();

    UserDto updateUser(Integer userId, UserDto userDto);

    void deleteUser(Integer userId);

    void confirmEmailUpdate(Integer userId, String email);

    void discardEmailUpdate(Integer userId);
}
