package com.example.event_service.controller;

import com.example.event_service.dto.request.MemberInviteRequest;
import com.example.event_service.dto.request.MemberRoleUpdateRequest;
import com.example.event_service.dto.response.MemberResponse;
import com.example.event_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getEventMembers(@PathVariable String eventId) {
        return ResponseEntity.ok(memberService.getEventMembers(eventId, getUserId()));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(
            @PathVariable String eventId,
            @RequestBody MemberInviteRequest request) {
        return ResponseEntity.ok(memberService.inviteMember(eventId, request, getUserId()));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @PathVariable String eventId,
            @PathVariable Integer userId,
            @RequestBody MemberRoleUpdateRequest request) {
        return ResponseEntity.ok(memberService.updateMemberRole(eventId, userId, request, getUserId()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable String eventId,
            @PathVariable Integer userId) {
        memberService.removeMember(eventId, userId, getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/leave")
    public ResponseEntity<Void> leaveEvent(@PathVariable String eventId) {
        memberService.leaveEvent(eventId, getUserId());
        return ResponseEntity.noContent().build();
    }

    private Integer getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Integer.parseInt(principal.toString());
    }
}
