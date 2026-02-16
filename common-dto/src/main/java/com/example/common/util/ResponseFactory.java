package com.example.common.util;

import com.example.common.dto.ApiResponse;
import com.example.common.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

public class ResponseFactory {

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T result) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .message(message)
                        .result(result)
                        .build());
    }

    public static ResponseEntity<ErrorResponse> error(int status, String code, String message, String path) {
        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .status(status)
                        .code(code)
                        .message(message)
                        .path(path)
                        .timestamp(Instant.now().toEpochMilli())
                        .build());
    }

    public static <T> ApiResponse<T> payload(String message, T result) {
        return ApiResponse.<T>builder()
                .message(message)
                .result(result)
                .build();
    }
}
