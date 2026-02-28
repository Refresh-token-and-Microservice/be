package com.example.member_service.dto.response;

import lombok.Data;

@Data
public class EventResponse {
    private String id;
    private Integer ownerId;
    private Boolean isPrivate;
}
