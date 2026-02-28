package com.example.member_service.dto.request;

import com.example.member_service.enums.EventRole;
import lombok.Data;

@Data
public class MemberRoleUpdateRequest {
    private EventRole role;
}
