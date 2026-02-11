package com.example.auth_service.service;

import com.example.auth_service.dto.request.UserRequest;
import com.example.auth_service.dto.response.UserResponse;

import java.util.Optional;

public interface UserService {
    UserResponse register(UserRequest user);

    Optional<UserResponse> login(String email, String password);
}
