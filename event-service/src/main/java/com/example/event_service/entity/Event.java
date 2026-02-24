package com.example.event_service.entity;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.event_service.enums.EventStatus;

@Entity
@Table(name = "events")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;
    private String description;
    private String thumbnailUrl;
    private Instant startTime;
    private Instant endTime;
    private String location;
    private String category;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private Integer ownerId;

    @Column(updatable = false)
    private Instant createdAt;
}