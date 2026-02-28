package com.example.user_service.user.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import com.example.user_service.user.domain.User;
import com.example.user_service.user.application.port.out.UserPersistencePort;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public User save(User user) {
        return springDataUserRepository.save(user);
    }

    @Override
    public Optional<User> findByUserId(Integer userId) {
        return springDataUserRepository.findByUserId(userId);
    }

    @Override
    public List<User> findAll() {
        return springDataUserRepository.findAll();
    }
}
