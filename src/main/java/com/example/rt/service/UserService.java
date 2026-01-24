package com.example.rt.service;

import com.example.rt.dto.request.UserRequest;
import com.example.rt.dto.response.UserResponse;

import java.util.Optional;

public interface UserService {
    UserResponse register(UserRequest user);
    Optional<UserResponse> login(String email, String password);
}
