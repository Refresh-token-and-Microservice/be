package com.example.auth_service.auth.application.port.in;

import com.example.auth_service.dto.request.UserRequest;
import com.example.auth_service.dto.response.UserResponse;

import java.util.Optional;

public interface AuthUseCase {
    UserResponse register(UserRequest user);

    Optional<UserResponse> login(String email, String password);

    UserResponse updateEmail(Integer userId, String newEmail);
}
