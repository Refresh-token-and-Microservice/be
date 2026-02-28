package com.example.auth_service.auth.infrastructure.adapter.out.persistence;

import com.example.auth_service.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
