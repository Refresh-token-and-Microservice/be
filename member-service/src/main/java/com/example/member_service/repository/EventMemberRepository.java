package com.example.member_service.repository;

import com.example.member_service.entity.EventMember;
import com.example.member_service.enums.EventRole;
import com.example.member_service.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventMemberRepository extends JpaRepository<EventMember, Long> {

    boolean existsByEventIdAndUserIdAndRoleAndStatus(String eventId, Integer userId, EventRole role,
            MemberStatus status);

    boolean existsByEventIdAndUserIdAndStatus(String eventId, Integer userId, MemberStatus status);

    Optional<EventMember> findByEventIdAndUserId(String eventId, Integer userId);

    List<EventMember> findByEventIdAndStatus(String eventId, MemberStatus status);
}
