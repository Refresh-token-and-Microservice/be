package com.example.auth_service.config;

import com.example.auth_service.entity.Role;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = createRoleIfNotFound("ADMIN");
        Role employeeRole = createRoleIfNotFound("EMPLOYEE");

        if (userRepository.findByEmail("admin@test.com").isEmpty()) {
            User admin = User.builder()
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("123456"))
                    .roles(new HashSet<>(Set.of(adminRole)))
                    .build();
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("employee@test.com").isEmpty()) {
            User employee = User.builder()
                    .email("employee@test.com")
                    .password(passwordEncoder.encode("123456"))
                    .roles(new HashSet<>(Set.of(employeeRole)))
                    .build();
            userRepository.save(employee);
        }
    }

    private Role createRoleIfNotFound(String name) {
        return roleRepository.findByRoleName(name)
                .orElseGet(() -> roleRepository.save(Role.builder().roleName(name).build()));
    }
}
