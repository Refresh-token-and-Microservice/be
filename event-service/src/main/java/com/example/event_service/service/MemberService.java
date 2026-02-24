package com.example.event_service.service;

import com.example.event_service.dto.request.MemberInviteRequest;
import com.example.event_service.dto.request.MemberRoleUpdateRequest;
import com.example.event_service.dto.response.MemberResponse;

import java.util.List;

public interface MemberService {
    List<MemberResponse> getEventMembers(String eventId, Integer requesterId);

    MemberResponse inviteMember(String eventId, MemberInviteRequest request, Integer requesterId);

    MemberResponse updateMemberRole(String eventId, Integer targetUserId, MemberRoleUpdateRequest request,
            Integer requesterId);

    void removeMember(String eventId, Integer targetUserId, Integer requesterId);

    void leaveEvent(String eventId, Integer requesterId);
}
