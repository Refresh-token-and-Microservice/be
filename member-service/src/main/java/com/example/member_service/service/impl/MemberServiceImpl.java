package com.example.member_service.service.impl;

import com.example.member_service.client.EventClient;
import com.example.member_service.dto.request.MemberInviteRequest;
import com.example.member_service.dto.response.EventResponse;
import com.example.member_service.dto.response.MemberResponse;
import com.example.member_service.entity.EventMember;
import com.example.member_service.enums.EventRole;
import com.example.member_service.enums.MemberStatus;
import com.example.member_service.repository.EventMemberRepository;
import com.example.member_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final EventMemberRepository eventMemberRepository;
    private final EventClient eventClient;

    @Override
    @Transactional
    public MemberResponse inviteMember(String eventId, MemberInviteRequest request, Integer inviterId) {
        EventResponse event = eventClient.getEventInternal(eventId);

        // Only owner or editor can invite
        if (!event.getOwnerId().equals(inviterId)) {
            boolean isEditor = eventMemberRepository.existsByEventIdAndUserIdAndRoleAndStatus(eventId, inviterId,
                    EventRole.EDITOR, MemberStatus.JOINED);
            if (!isEditor) {
                throw new RuntimeException("Access denied: Only OWNER or EDITOR can invite members");
            }
        }

        if (request.getRole() == EventRole.OWNER) {
            throw new RuntimeException("Cannot invite someone as OWNER");
        }

        EventMember newMember = eventMemberRepository.findByEventIdAndUserId(eventId, request.getUserId())
                .orElse(EventMember.builder()
                        .eventId(eventId)
                        .userId(request.getUserId())
                        .build());

        if (newMember.getStatus() == MemberStatus.JOINED) {
            throw new RuntimeException("User is already a member");
        }

        newMember.setRole(request.getRole());
        newMember.setStatus(MemberStatus.INVITED);
        newMember = eventMemberRepository.save(newMember);

        return mapToResponse(newMember);
    }

    @Override
    @Transactional
    public MemberResponse acceptInvitation(String eventId, Integer userId) {
        EventMember member = eventMemberRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        if (member.getStatus() != MemberStatus.INVITED) {
            throw new RuntimeException("No pending invitation");
        }

        member.setStatus(MemberStatus.JOINED);
        member.setJoinedAt(Instant.now());
        member = eventMemberRepository.save(member);

        return mapToResponse(member);
    }

    @Override
    @Transactional
    public void declineInvitation(String eventId, Integer userId) {
        EventMember member = eventMemberRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        if (member.getStatus() != MemberStatus.INVITED) {
            throw new RuntimeException("No pending invitation");
        }

        eventMemberRepository.delete(member);
    }

    @Override
    @Transactional
    public MemberResponse rsvpEvent(String eventId, Integer userId) {
        EventResponse event = eventClient.getEventInternal(eventId);

        if (event.getIsPrivate() != null && event.getIsPrivate()) {
            throw new RuntimeException("Cannot RSVP to a private event, must be invited");
        }

        if (event.getOwnerId().equals(userId)) {
            throw new RuntimeException("Owner cannot RSVP to their own event");
        }

        Optional<EventMember> existing = eventMemberRepository.findByEventIdAndUserId(eventId, userId);
        if (existing.isPresent()) {
            if (existing.get().getStatus() == MemberStatus.JOINED) {
                throw new RuntimeException("Already joined");
            } else {
                // If they are invited, RSVP can just auto-accept as VIEWER or keep their
                // invited role
                EventMember member = existing.get();
                member.setStatus(MemberStatus.JOINED);
                member.setJoinedAt(Instant.now());
                member = eventMemberRepository.save(member);
                return mapToResponse(member);
            }
        }

        EventMember newMember = EventMember.builder()
                .eventId(eventId)
                .userId(userId)
                .role(EventRole.VIEWER)
                .status(MemberStatus.JOINED)
                .joinedAt(Instant.now())
                .build();

        newMember = eventMemberRepository.save(newMember);
        return mapToResponse(newMember);
    }

    @Override
    public Boolean checkMemberRole(String eventId, Integer userId, String roleStr) {
        EventResponse event = eventClient.getEventInternal(eventId);
        if ("OWNER".equals(roleStr) && event.getOwnerId().equals(userId)) {
            return true;
        }
        EventRole role = EventRole.valueOf(roleStr);
        return eventMemberRepository.existsByEventIdAndUserIdAndRoleAndStatus(eventId, userId, role,
                MemberStatus.JOINED);
    }

    @Override
    public Boolean isMember(String eventId, Integer userId) {
        EventResponse event = eventClient.getEventInternal(eventId);
        if (event.getOwnerId().equals(userId)) {
            return true;
        }
        return eventMemberRepository.existsByEventIdAndUserIdAndStatus(eventId, userId, MemberStatus.JOINED);
    }

    @Override
    public java.util.List<MemberResponse> getEventMembers(String eventId, Integer requesterId) {
        EventResponse event = eventClient.getEventInternal(eventId);
        if (!event.getOwnerId().equals(requesterId)) {
            boolean isMember = eventMemberRepository.existsByEventIdAndUserIdAndStatus(eventId, requesterId,
                    MemberStatus.JOINED);
            if (!isMember) {
                throw new RuntimeException("Access denied: Not a member");
            }
        }
        return eventMemberRepository.findByEventIdAndStatus(eventId, MemberStatus.JOINED)
                .stream().map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public MemberResponse updateMemberRole(String eventId, Integer targetUserId,
            com.example.member_service.dto.request.MemberRoleUpdateRequest request, Integer requesterId) {
        EventResponse event = eventClient.getEventInternal(eventId);
        if (!event.getOwnerId().equals(requesterId)) {
            throw new RuntimeException("Access denied: Only OWNER can update roles");
        }
        if (event.getOwnerId().equals(targetUserId)) {
            throw new RuntimeException("Cannot change owner's role");
        }
        if (request.getRole() == EventRole.OWNER) {
            throw new RuntimeException("Cannot set role to OWNER");
        }

        EventMember member = eventMemberRepository.findByEventIdAndUserId(eventId, targetUserId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setRole(request.getRole());
        return mapToResponse(eventMemberRepository.save(member));
    }

    @Override
    @Transactional
    public void removeMember(String eventId, Integer targetUserId, Integer requesterId) {
        EventResponse event = eventClient.getEventInternal(eventId);
        if (!event.getOwnerId().equals(requesterId)) {
            boolean isEditor = eventMemberRepository.existsByEventIdAndUserIdAndRoleAndStatus(eventId, requesterId,
                    EventRole.EDITOR, MemberStatus.JOINED);
            if (!isEditor) {
                throw new RuntimeException("Access denied: Requires EDITOR or OWNER role");
            }
        }
        if (event.getOwnerId().equals(targetUserId)) {
            throw new RuntimeException("Cannot remove owner");
        }

        EventMember member = eventMemberRepository.findByEventIdAndUserId(eventId, targetUserId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        eventMemberRepository.delete(member);
    }

    @Override
    @Transactional
    public void leaveEvent(String eventId, Integer userId) {
        EventResponse event = eventClient.getEventInternal(eventId);
        if (event.getOwnerId().equals(userId)) {
            throw new RuntimeException("Owner cannot leave the event");
        }

        EventMember member = eventMemberRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        eventMemberRepository.delete(member);
    }

    private MemberResponse mapToResponse(EventMember member) {
        return MemberResponse.builder()
                .id(member.getId())
                .eventId(member.getEventId())
                .userId(member.getUserId())
                .role(member.getRole())
                .status(member.getStatus())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
