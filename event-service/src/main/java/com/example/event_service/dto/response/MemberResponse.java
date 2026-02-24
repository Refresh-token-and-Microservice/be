package com.example.event_service.dto.response;

import com.example.event_service.enums.EventRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private Long id;
    private String eventId;
    private Integer userId;
    private EventRole role;
    private Instant joinedAt;
}
