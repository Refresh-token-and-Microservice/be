package com.example.auth_service.controller;

import com.example.auth_service.dto.request.UserRequest;
import com.example.auth_service.dto.response.LoginResponse;
import com.example.auth_service.dto.response.UserResponse;
import com.example.auth_service.entity.RefreshToken;
import com.example.auth_service.entity.User;
import com.example.auth_service.service.JwtService;
import com.example.auth_service.service.RefreshTokenService;
import com.example.auth_service.service.UserService;
import com.example.auth_service.util.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Value("${REFRESH_TOKEN_TIME}")
    private long REFRESH_TOKEN_TIME;

    @Value("${ACCESS_TOKEN_EXPIRATION}")
    private long ACCESS_TOKEN_EXPIRATION;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest user) {
        try {
            UserResponse newUser = userService.register(user);
            return ResponseFactory.success("User registered successfully", newUser);
        } catch (RuntimeException e) {
            return ResponseFactory.error(400, "USER_REGISTRATION_FAILED", e.getMessage(), "/auth/register");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        Optional<UserResponse> userOpt = userService.login(email, password);

        if (userOpt.isPresent()) {
            UserResponse userResponse = userOpt.get();

            // 1. Tạo Access Token (JWT) - Now includes Roles and ID as claims
            String accessToken = jwtService.generateAccessToken(userResponse);

            // 2. Tạo Refresh Token (UUID lưu DB)
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userResponse.getId());

            // 3. Đóng gói vào Cookie (Lưu ý chia thời gian cho 1000)
            ResponseCookie refreshCookie = createCookie("refresh_token", refreshToken.getToken(), REFRESH_TOKEN_TIME);
            ResponseCookie accessCookie = createCookie("access_token", accessToken, ACCESS_TOKEN_EXPIRATION);

            // 4. Create LoginResponse without roles
            LoginResponse loginResponse = LoginResponse.builder()
                    .id(userResponse.getId())
                    .email(userResponse.getEmail())
                    .build();

            return ResponseEntity.ok()
                    // Add cả 2 cookie vào header an toàn
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                    .body(ResponseFactory.payload("Login successful", loginResponse));
        } else {
            return ResponseFactory.error(401, "INVALID_CREDENTIALS", "Invalid email or password", "/auth/login");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token", required = false) String requestRefreshToken) {
        if (requestRefreshToken != null) {
            refreshTokenService.revokeToken(requestRefreshToken);
        }

        ResponseCookie cleanRefreshCookie = createCookie("refresh_token", "", 0);
        ResponseCookie cleanAccessCookie = createCookie("access_token", "", 0);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanAccessCookie.toString(), cleanRefreshCookie.toString())
                .body(ResponseFactory.payload("Logged out successfully", null));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String requestRefreshToken) {
        if (requestRefreshToken == null) {
            return ResponseFactory.error(401, "REFRESH_TOKEN_MISSING", "No refresh token provided",
                    "/auth/refresh-token");
        }

        try {
            RefreshToken verifiedToken = refreshTokenService.verifyExpiration(
                    refreshTokenService.findByToken(requestRefreshToken)
                            .orElseThrow(() -> new RuntimeException("Refresh token is not in database!")));

            User user = verifiedToken.getUser();

            // Handle rotation with new claims-based token
            // Map User entity to UserResponse for JwtService
            UserResponse userResponse = UserResponse.builder()
                    .id(Long.valueOf(user.getId()))
                    .email(user.getEmail())
                    .roles(user.getRoles().stream().map(r -> r.getRoleName())
                            .collect(java.util.stream.Collectors.toSet()))
                    .build();

            String newAccessToken = jwtService.generateAccessToken(userResponse);

            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userResponse.getId());

            ResponseCookie newAccessCookie = createCookie("access_token", newAccessToken, ACCESS_TOKEN_EXPIRATION);
            ResponseCookie newRefreshCookie = createCookie("refresh_token", newRefreshToken.getToken(),
                    REFRESH_TOKEN_TIME);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString(), newRefreshCookie.toString())
                    .body(ResponseFactory.payload("Token refreshed and rotated successfully", null));

        } catch (Exception e) {
            ResponseCookie cleanRefreshCookie = createCookie("refresh_token", "", 0);
            ResponseCookie cleanAccessCookie = createCookie("access_token", "", 0);

            return ResponseEntity.status(403)
                    .header(HttpHeaders.SET_COOKIE, cleanAccessCookie.toString(), cleanRefreshCookie.toString())
                    .body(ResponseFactory.error(403, "INVALID_REFRESH_TOKEN", e.getMessage(), "/auth/refresh-token"));
        }
    }

    private ResponseCookie createCookie(String name, String value, long maxAgeInMillis) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAgeInMillis / 1000)
                .sameSite("Strict")
                .build();
    }
}
