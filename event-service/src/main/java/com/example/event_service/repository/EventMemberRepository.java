package com.example.event_service.repository;

import com.example.event_service.entity.EventMember;
import com.example.event_service.enums.EventRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventMemberRepository extends JpaRepository<EventMember, Long> {

    List<EventMember> findByEventId(String eventId);

    Optional<EventMember> findByEventIdAndUserId(String eventId, Integer userId);

    boolean existsByEventIdAndUserId(String eventId, Integer userId);

    boolean existsByEventIdAndUserIdAndRole(String eventId, Integer userId, EventRole role);

    List<EventMember> findByUserId(Integer userId);
}
