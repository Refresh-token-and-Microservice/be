package com.example.event_service.service.impl;

import com.example.event_service.dto.request.MemberInviteRequest;
import com.example.event_service.dto.request.MemberRoleUpdateRequest;
import com.example.event_service.dto.response.MemberResponse;
import com.example.event_service.entity.Event;
import com.example.event_service.entity.EventMember;
import com.example.event_service.enums.EventRole;
import com.example.event_service.repository.EventMemberRepository;
import com.example.event_service.repository.EventRepository;
import com.example.event_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final EventMemberRepository eventMemberRepository;
    private final EventRepository eventRepository;

    @Override
    public List<MemberResponse> getEventMembers(String eventId, Integer requesterId) {
        checkHasMemberPermission(eventId, requesterId);

        List<EventMember> members = eventMemberRepository.findByEventId(eventId);
        return members.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MemberResponse inviteMember(String eventId, MemberInviteRequest request, Integer requesterId) {
        Event event = checkIsOwner(eventId, requesterId);

        if (eventMemberRepository.existsByEventIdAndUserId(eventId, request.getUserId())) {
            throw new RuntimeException("User is already a member of this event");
        }

        EventMember member = EventMember.builder()
                .event(event)
                .userId(request.getUserId())
                .role(request.getRole())
                .joinedAt(Instant.now())
                .build();

        member = eventMemberRepository.save(member);
        return mapToResponse(member);
    }

    @Override
    @Transactional
    public MemberResponse updateMemberRole(String eventId, Integer targetUserId, MemberRoleUpdateRequest request,
            Integer requesterId) {
        checkIsOwner(eventId, requesterId);

        EventMember member = eventMemberRepository.findByEventIdAndUserId(eventId, targetUserId)
                .orElseThrow(() -> new RuntimeException("Member not found in this event"));

        if (member.getRole() == EventRole.OWNER) {
            throw new RuntimeException("Cannot change the role of an OWNER");
        }

        member.setRole(request.getRole());
        member = eventMemberRepository.save(member);
        return mapToResponse(member);
    }

    @Override
    @Transactional
    public void removeMember(String eventId, Integer targetUserId, Integer requesterId) {
        checkIsOwner(eventId, requesterId);

        EventMember member = eventMemberRepository.findByEventIdAndUserId(eventId, targetUserId)
                .orElseThrow(() -> new RuntimeException("Member not found in this event"));

        if (member.getRole() == EventRole.OWNER) {
            throw new RuntimeException("Cannot remove the OWNER from the event");
        }

        eventMemberRepository.delete(member);
    }

    @Override
    @Transactional
    public void leaveEvent(String eventId, Integer requesterId) {
        EventMember member = eventMemberRepository.findByEventIdAndUserId(eventId, requesterId)
                .orElseThrow(() -> new RuntimeException("You are not a member of this event"));

        if (member.getRole() == EventRole.OWNER) {
            throw new RuntimeException("OWNER cannot leave the event. Must transfer ownership or delete event.");
        }

        eventMemberRepository.delete(member);
    }

    private void checkHasMemberPermission(String eventId, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOwnerId().equals(userId)) {
            boolean isMember = eventMemberRepository.existsByEventIdAndUserId(eventId, userId);
            if (!isMember) {
                throw new RuntimeException("Access denied: Not a member of this event");
            }
        }
    }

    private Event checkIsOwner(String eventId, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOwnerId().equals(userId)) {
            throw new RuntimeException("Access denied: Only OWNER can perform this action");
        }

        return event;
    }

    private MemberResponse mapToResponse(EventMember member) {
        return MemberResponse.builder()
                .id(member.getId())
                .eventId(member.getEvent().getId())
                .userId(member.getUserId())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
