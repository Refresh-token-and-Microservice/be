package com.example.common.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivateUserCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer userId;
}
