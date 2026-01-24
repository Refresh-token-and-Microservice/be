package com.example.rt.controller;

import com.example.rt.dto.request.UserRequest;
import com.example.rt.dto.response.UserResponse;
import com.example.rt.entity.RefreshToken;
import com.example.rt.entity.User;
import com.example.rt.service.JwtService;
import com.example.rt.service.UserService;
import com.example.rt.service.RefreshTokenService;
import com.example.rt.util.ResponseFactory;
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

            // 1. Tạo Access Token (JWT)
            String accessToken = jwtService.generateAccessToken(userResponse.getEmail());

            // 2. Tạo Refresh Token (UUID lưu DB)
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userResponse.getId());

            // 3. Đóng gói vào Cookie (Lưu ý chia thời gian cho 1000)
            ResponseCookie refreshCookie = createCookie("refresh_token", refreshToken.getToken(), REFRESH_TOKEN_TIME);
            ResponseCookie accessCookie = createCookie("access_token", accessToken, ACCESS_TOKEN_EXPIRATION);

            return ResponseEntity.ok()
                    // Add cả 2 cookie vào header an toàn
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                    .body(ResponseFactory.payload("Login successful", userResponse));
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
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = false) String requestRefreshToken) {
        if (requestRefreshToken == null) {
            return ResponseFactory.error(401, "REFRESH_TOKEN_MISSING", "No refresh token provided", "/auth/refresh-token");
        }

        try {
            // 1. Tìm token trong Database và 2. Kiểm tra hết hạn
            RefreshToken verifiedToken = refreshTokenService.verifyExpiration(
                    refreshTokenService.findByToken(requestRefreshToken)
                            .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"))
            );

            // 3. Lấy thông tin User
            User user = verifiedToken.getUser();

            // 4. Tạo Access Token MỚI
            String newAccessToken = jwtService.generateAccessToken(user.getEmail());

            // 5. Tạo Refresh Token MỚI (Cơ chế Rotation)
            // Hàm createRefreshToken của bạn sẽ update bản ghi cũ thành token UUID mới và hạn mới
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(Long.valueOf(user.getId()));

            // 6. Tạo 2 Cookie MỚI (Ghi đè cái cũ trên trình duyệt)
            ResponseCookie newAccessCookie = createCookie("access_token", newAccessToken, ACCESS_TOKEN_EXPIRATION);
            ResponseCookie newRefreshCookie = createCookie("refresh_token", newRefreshToken.getToken(), REFRESH_TOKEN_TIME);

            // 7. Trả về cả 2 Cookie
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
