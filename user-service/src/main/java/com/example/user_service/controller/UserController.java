package com.example.user_service.controller;

import com.example.common.dto.ApiResponse;
import com.example.common.util.ResponseFactory;
import com.example.user_service.dto.UserDto;
import com.example.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@RequestBody UserDto userDto) {
        return ResponseFactory.success("User created successfully", userService.saveUser(userDto));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable String userId) {
        return ResponseFactory.success("User retrieved successfully", userService.getUserById(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        return ResponseFactory.success("All users retrieved successfully", userService.getAllUsers());
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable String userId, @RequestBody UserDto userDto) {
        return ResponseFactory.success("User updated successfully", userService.updateUser(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseFactory.success("User deleted successfully", null);
    }
}
