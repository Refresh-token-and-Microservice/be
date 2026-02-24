package com.example.event_service.entity;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.event_service.enums.EventRole;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private Integer userId;

    @Enumerated(EnumType.STRING)
    private EventRole role;

    private Instant joinedAt;
}