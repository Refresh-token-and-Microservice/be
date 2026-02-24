package com.example.event_service.dto.response;

import com.example.event_service.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private Long id;
    private String eventId;
    private String title;
    private String description;
    private TaskStatus status;
    private Integer assigneeId;
    private Instant dueDate;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isLate;
}
