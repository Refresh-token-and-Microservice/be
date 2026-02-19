package com.example.common_dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisableAuthUserCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    private String transactionId;

    private String userId;
}
