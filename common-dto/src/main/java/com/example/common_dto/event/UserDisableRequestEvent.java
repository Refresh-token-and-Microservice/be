package com.example.common_dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDisableRequestEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer userId;
}
