package com.example.member_service.controller;

import com.example.member_service.dto.request.MemberInviteRequest;
import com.example.member_service.dto.response.MemberResponse;
import com.example.member_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/{eventId}")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/invite")
    public ResponseEntity<MemberResponse> inviteMember(
            @PathVariable String eventId,
            @RequestBody MemberInviteRequest request) {
        return ResponseEntity.ok(memberService.inviteMember(eventId, request, getUserId()));
    }

    @PostMapping("/accept")
    public ResponseEntity<MemberResponse> acceptInvitation(@PathVariable String eventId) {
        return ResponseEntity.ok(memberService.acceptInvitation(eventId, getUserId()));
    }

    @PostMapping("/decline")
    public ResponseEntity<Void> declineInvitation(@PathVariable String eventId) {
        memberService.declineInvitation(eventId, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/rsvp")
    public ResponseEntity<MemberResponse> rsvpEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(memberService.rsvpEvent(eventId, getUserId()));
    }

    @GetMapping
    public ResponseEntity<java.util.List<MemberResponse>> getEventMembers(@PathVariable String eventId) {
        return ResponseEntity.ok(memberService.getEventMembers(eventId, getUserId()));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @PathVariable String eventId,
            @PathVariable Integer userId,
            @RequestBody com.example.member_service.dto.request.MemberRoleUpdateRequest request) {
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

    // Internal APIs for event-service
    @GetMapping("/{userId}/check-role")
    public ResponseEntity<Boolean> checkRole(
            @PathVariable String eventId,
            @PathVariable Integer userId,
            @RequestParam String role) {
        return ResponseEntity.ok(memberService.checkMemberRole(eventId, userId, role));
    }

    @GetMapping("/{userId}/exists")
    public ResponseEntity<Boolean> isMember(
            @PathVariable String eventId,
            @PathVariable Integer userId) {
        return ResponseEntity.ok(memberService.isMember(eventId, userId));
    }

    private Integer getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Integer.parseInt(principal.toString());
    }
}
