package com.example.common_dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthEmailUpdateFailedEvent {
    private String transactionId;
    private String userId;
    private String email;
    private String reason;
}
