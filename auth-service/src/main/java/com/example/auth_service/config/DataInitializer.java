package com.example.auth_service.config;

import com.example.auth_service.entity.Role;
import com.example.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("EMPLOYEE");
    }

    private Role createRoleIfNotFound(String name) {
        return roleRepository.findByRoleName(name)
                .orElseGet(() -> roleRepository.save(Role.builder().roleName(name).build()));
    }
}
