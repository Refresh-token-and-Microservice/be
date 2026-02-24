package com.example.common_dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthEmailUpdatedEvent {
    private String transactionId;
    private Integer userId;
    private String email;
}
