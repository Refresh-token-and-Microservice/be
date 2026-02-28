package com.example.auth_service.auth.infrastructure.adapter.out.persistence;

import com.example.auth_service.auth.domain.RefreshToken;
import com.example.auth_service.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);
}
