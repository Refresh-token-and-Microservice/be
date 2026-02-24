package com.example.event_service.controller;

import com.example.event_service.dto.request.EventCreateRequest;
import com.example.event_service.dto.request.EventUpdateRequest;
import com.example.event_service.dto.response.EventResponse;
import com.example.event_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventCreateRequest request) {
        return ResponseEntity.ok(eventService.createEvent(request, getUserId()));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getMyEvents() {
        return ResponseEntity.ok(eventService.getMyEvents(getUserId()));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(eventService.getEvent(eventId, getUserId()));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable String eventId,
            @RequestBody EventUpdateRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, request, getUserId()));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String eventId) {
        eventService.deleteEvent(eventId, getUserId());
        return ResponseEntity.noContent().build();
    }

    private Integer getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Integer.parseInt(principal.toString());
    }
}
