package com.example.event_service.dto.response;

import com.example.event_service.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private String id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Instant startTime;
    private Instant endTime;
    private String location;
    private String category;
    private EventStatus status;
    private Integer ownerId;
    private Instant createdAt;
}
