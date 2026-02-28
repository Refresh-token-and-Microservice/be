package com.example.auth_service.auth.application.port.out;

import com.example.auth_service.auth.domain.Role;
import com.example.auth_service.auth.domain.User;

import java.util.Optional;

public interface AuthPersistencePort {
    User saveUser(User user);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(Integer id);

    Optional<Role> findRoleByName(String roleName);

    Role saveRole(Role role);

    void deleteUserById(Integer id);
}
