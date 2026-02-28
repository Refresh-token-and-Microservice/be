package com.example.auth_service.auth.application.port.out;

import com.example.auth_service.dto.response.UserResponse;

public interface JwtPort {
    String generateAccessToken(UserResponse user);
}
