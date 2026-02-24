package com.example.event_service.service;

import com.example.event_service.dto.request.EventCreateRequest;
import com.example.event_service.dto.request.EventUpdateRequest;
import com.example.event_service.dto.response.EventResponse;

import java.util.List;

public interface EventService {
    EventResponse createEvent(EventCreateRequest request, Integer userId);

    List<EventResponse> getMyEvents(Integer userId);

    EventResponse getEvent(String eventId, Integer userId);

    EventResponse updateEvent(String eventId, EventUpdateRequest request, Integer userId);

    void deleteEvent(String eventId, Integer userId);
}
