package com.example.common_dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailUpdateRequestedEvent {
    private String userId;
    private String oldEmail;
    private String newEmail;
}
