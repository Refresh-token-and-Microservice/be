package com.example.api_gateway.service;

import com.example.api_gateway.dto.request.UserRequest;
import com.example.api_gateway.dto.response.UserResponse;

import java.util.Optional;

public interface UserService {
    UserResponse register(UserRequest user);

    Optional<UserResponse> login(String email, String password);
}
