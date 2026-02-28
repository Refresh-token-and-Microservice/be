package com.example.auth_service.auth.application.service;

import com.example.auth_service.auth.application.port.out.AuthPersistencePort;
import com.example.auth_service.auth.application.port.out.TokenPersistencePort;
import com.example.auth_service.auth.domain.RefreshToken;
import com.example.auth_service.auth.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

@Service
public class TokenApplicationService {

    @Value("${REFRESH_TOKEN_TIME}")
    private long REFRESH_TOKEN_TIME;

    @Autowired
    private TokenPersistencePort tokenPersistencePort;

    @Autowired
    private AuthPersistencePort authPersistencePort;

    public RefreshToken createRefreshToken(Long userId) {
        User user = authPersistencePort.findUserById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken refreshToken = tokenPersistencePort.findByUser(user)
                .orElseGet(() -> {
                    return RefreshToken.builder()
                            .user(user)
                            .build();
                });

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(Instant.now().plusMillis(REFRESH_TOKEN_TIME));
        refreshToken.setRevoked(false);

        return tokenPersistencePort.saveToken(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return tokenPersistencePort.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().compareTo(Instant.now()) < 0) {
            tokenPersistencePort.deleteToken(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    public void revokeToken(String token) {
        var tokenOpt = tokenPersistencePort.findByToken(token);
        if (tokenOpt.isPresent()) {
            RefreshToken rt = tokenOpt.get();
            rt.setRevoked(true);
            tokenPersistencePort.saveToken(rt);
        }
    }
}
