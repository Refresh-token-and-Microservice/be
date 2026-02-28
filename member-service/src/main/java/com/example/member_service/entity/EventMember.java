package com.example.member_service.entity;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.member_service.enums.EventRole;
import com.example.member_service.enums.MemberStatus;

@Entity
@Table(name = "event_members")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    private EventRole role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private Instant joinedAt;
}
