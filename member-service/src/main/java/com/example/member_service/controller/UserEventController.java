package com.example.member_service.controller;

import com.example.member_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class UserEventController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<java.util.List<String>> getUserEvents(@PathVariable Integer userId) {
        return ResponseEntity.ok(memberService.getUserEvents(userId));
    }
}
