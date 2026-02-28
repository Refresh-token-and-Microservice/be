package com.example.auth_service.auth.application.port.out;

import com.example.auth_service.auth.domain.RefreshToken;
import com.example.auth_service.auth.domain.User;

import java.util.Optional;

public interface TokenPersistencePort {
    RefreshToken saveToken(RefreshToken token);

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    void deleteToken(RefreshToken token);
}
