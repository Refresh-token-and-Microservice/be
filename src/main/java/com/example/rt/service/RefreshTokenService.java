package com.example.rt.service;

import com.example.rt.entity.RefreshToken;
import com.example.rt.entity.User;
import com.example.rt.repository.RefreshTokenRepository;
import com.example.rt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Value("${REFRESH_TOKEN_TIME}")
    private long REFRESH_TOKEN_TIME;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(() -> {
                    return RefreshToken.builder()
                            .user(user)
                            .build();
                });

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(Instant.now().plusMillis(REFRESH_TOKEN_TIME));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    public void revokeToken(String token) {
        var tokenOpt = refreshTokenRepository.findByToken(token);
        if(tokenOpt.isPresent()){
            RefreshToken rt = tokenOpt.get();
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        }
    }
}