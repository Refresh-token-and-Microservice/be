package com.example.user_service.user.infrastructure.adapter.in.web;

import com.example.common.dto.ApiResponse;
import com.example.common.util.ResponseFactory;
import com.example.user_service.dto.UserDto;
import com.example.user_service.user.application.port.in.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@RequestBody UserDto userDto) {
        return ResponseFactory.success("User created successfully", userUseCase.saveUser(userDto));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable Integer userId) {
        return ResponseFactory.success("User retrieved successfully", userUseCase.getUserById(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        return ResponseFactory.success("All users retrieved successfully", userUseCase.getAllUsers());
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Integer userId, @RequestBody UserDto userDto) {
        return ResponseFactory.success("User updated successfully", userUseCase.updateUser(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Integer userId) {
        userUseCase.deleteUser(userId);
        return ResponseFactory.success("User deleted successfully", null);
    }
}
