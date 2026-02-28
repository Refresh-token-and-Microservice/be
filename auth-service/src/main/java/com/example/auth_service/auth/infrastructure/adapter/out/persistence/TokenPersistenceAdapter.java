package com.example.auth_service.auth.infrastructure.adapter.out.persistence;

import com.example.auth_service.auth.application.port.out.TokenPersistencePort;
import com.example.auth_service.auth.domain.RefreshToken;
import com.example.auth_service.auth.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenPersistenceAdapter implements TokenPersistencePort {

    private final SpringDataRefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken saveToken(RefreshToken token) {
        return refreshTokenRepository.save(token);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public Optional<RefreshToken> findByUser(User user) {
        return refreshTokenRepository.findByUser(user);
    }

    @Override
    public void deleteToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }
}
