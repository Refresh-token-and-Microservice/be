package com.example.event_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateRequest {
    private String title;
    private String description;
    private String thumbnailUrl;
    private Instant startTime;
    private Instant endTime;
    private String location;
    private String category;
    private Boolean isPrivate;
}
