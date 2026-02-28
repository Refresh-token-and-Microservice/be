package com.example.event_service.service.impl;

import com.example.event_service.dto.request.EventCreateRequest;
import com.example.event_service.dto.request.EventUpdateRequest;
import com.example.event_service.dto.response.EventResponse;
import com.example.event_service.entity.Event;
import com.example.event_service.enums.EventStatus;
import com.example.event_service.client.MemberClient;
import com.example.event_service.repository.EventRepository;
import com.example.event_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final MemberClient memberClient;

    @Override
    @Transactional
    public EventResponse createEvent(EventCreateRequest request, Integer userId) {
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .location(request.getLocation())
                .category(request.getCategory())
                .isPrivate(request.getIsPrivate())
                .status(EventStatus.SCHEDULED)
                .ownerId(userId)
                .createdAt(Instant.now())
                .build();

        event = eventRepository.save(event);

        return mapToResponse(event);
    }

    @Override
    public List<EventResponse> getMyEvents(Integer userId) {
        List<Event> events = eventRepository.findMyEvents(userId);
        return events.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public EventResponse getEvent(String eventId, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOwnerId().equals(userId)) {
            boolean isMember = memberClient.isMember(eventId, userId);
            if (!isMember) {
                throw new RuntimeException("Access denied: Not a member of this event");
            }
        }

        return mapToResponse(event);
    }

    @Override
    public EventResponse getEventInternal(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return mapToResponse(event);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(String eventId, EventUpdateRequest request, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        checkHasEditorOrOwnerPermission(eventId, userId, event);

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setThumbnailUrl(request.getThumbnailUrl());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setLocation(request.getLocation());
        event.setCategory(request.getCategory());
        event.setIsPrivate(request.getIsPrivate());

        event = eventRepository.save(event);
        return mapToResponse(event);
    }

    @Override
    @Transactional
    public void deleteEvent(String eventId, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOwnerId().equals(userId)) {
            throw new RuntimeException("Access denied: Only owner can delete the event");
        }

        eventRepository.delete(event);
    }

    private void checkHasEditorOrOwnerPermission(String eventId, Integer userId, Event event) {
        if (!event.getOwnerId().equals(userId)) {
            boolean isEditor = memberClient.checkMemberRole(eventId, userId, "EDITOR");
            if (!isEditor) {
                throw new RuntimeException("Access denied: Requires EDITOR or OWNER role");
            }
        }
    }

    private EventResponse mapToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .thumbnailUrl(event.getThumbnailUrl())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .category(event.getCategory())
                .isPrivate(event.getIsPrivate())
                .status(event.getStatus())
                .ownerId(event.getOwnerId())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
