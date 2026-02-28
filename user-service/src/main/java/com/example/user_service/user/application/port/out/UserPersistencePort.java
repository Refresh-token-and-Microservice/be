package com.example.user_service.user.application.port.out;

import java.util.List;
import java.util.Optional;
import com.example.user_service.user.domain.User;

public interface UserPersistencePort {
    User save(User user);

    Optional<User> findByUserId(Integer userId);

    List<User> findAll();
}
