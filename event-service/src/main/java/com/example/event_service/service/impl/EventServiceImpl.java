package com.example.event_service.service.impl;

import com.example.event_service.dto.request.EventCreateRequest;
import com.example.event_service.dto.request.EventUpdateRequest;
import com.example.event_service.dto.response.EventResponse;
import com.example.event_service.entity.Event;
import com.example.event_service.enums.EventStatus;
import com.example.event_service.mapper.EventMapper;
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
    private final EventMapper eventMapper;

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

        return eventMapper.toResponse(event);
    }

    @Override
    public List<EventResponse> getMyEvents(Integer userId) {
        List<Event> ownedEvents = eventRepository.findByOwnerId(userId);

        List<String> memberEventIds = memberClient.getUserEvents(userId);
        List<Event> memberEvents = eventRepository.findAllById(memberEventIds);

        java.util.Set<Event> allEvents = new java.util.HashSet<>(ownedEvents);
        allEvents.addAll(memberEvents);

        return allEvents.stream().map(eventMapper::toResponse).collect(Collectors.toList());
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

        return eventMapper.toResponse(event);
    }

    @Override
    public EventResponse getEventInternal(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return eventMapper.toResponse(event);
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
        return eventMapper.toResponse(event);
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
}
