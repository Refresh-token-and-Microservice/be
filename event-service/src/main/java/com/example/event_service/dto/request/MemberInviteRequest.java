package com.example.event_service.dto.request;

import com.example.event_service.enums.EventRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberInviteRequest {
    private Integer userId;
    private EventRole role;
}
