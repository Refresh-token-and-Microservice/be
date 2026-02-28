package com.example.auth_service.auth.infrastructure.security;

import com.example.auth_service.auth.domain.Role;
import com.example.auth_service.auth.application.port.out.AuthPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthPersistencePort authPersistencePort;

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("EMPLOYEE");
    }

    private Role createRoleIfNotFound(String name) {
        return authPersistencePort.findRoleByName(name)
                .orElseGet(() -> authPersistencePort.saveRole(Role.builder().roleName(name).build()));
    }
}
