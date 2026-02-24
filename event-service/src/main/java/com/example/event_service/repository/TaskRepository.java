package com.example.event_service.repository;

import com.example.event_service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByEventId(String eventId);

    Optional<Task> findByIdAndEventId(Long id, String eventId);
}
