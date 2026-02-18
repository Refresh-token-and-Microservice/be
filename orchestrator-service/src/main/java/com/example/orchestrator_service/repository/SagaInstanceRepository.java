package com.example.orchestrator_service.repository;

import com.example.orchestrator_service.entity.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {
    Optional<SagaInstance> findByUserIdAndStatus(String userId, String status);
}
