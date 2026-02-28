package com.example.member_service.service;

import com.example.member_service.dto.request.MemberInviteRequest;
import com.example.member_service.dto.response.MemberResponse;

public interface MemberService {
    MemberResponse inviteMember(String eventId, MemberInviteRequest request, Integer inviterId);

    MemberResponse acceptInvitation(String eventId, Integer userId);

    void declineInvitation(String eventId, Integer userId);

    MemberResponse rsvpEvent(String eventId, Integer userId);

    Boolean checkMemberRole(String eventId, Integer userId, String role);

    Boolean isMember(String eventId, Integer userId);

    java.util.List<MemberResponse> getEventMembers(String eventId, Integer requesterId);

    MemberResponse updateMemberRole(String eventId, Integer targetUserId,
            com.example.member_service.dto.request.MemberRoleUpdateRequest request, Integer requesterId);

    void removeMember(String eventId, Integer targetUserId, Integer requesterId);

    void leaveEvent(String eventId, Integer userId);
}
