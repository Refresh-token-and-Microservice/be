package com.example.event_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "member-service")
public interface MemberClient {

    @GetMapping("/events/{eventId}/members/{userId}/check-role")
    Boolean checkMemberRole(@PathVariable("eventId") String eventId, @PathVariable("userId") Integer userId,
            @RequestParam("role") String role);

    @GetMapping("/events/{eventId}/members/{userId}/exists")
    Boolean isMember(@PathVariable("eventId") String eventId, @PathVariable("userId") Integer userId);

    @GetMapping("/users/{userId}/events")
    java.util.List<String> getUserEvents(@PathVariable("userId") Integer userId);
}
