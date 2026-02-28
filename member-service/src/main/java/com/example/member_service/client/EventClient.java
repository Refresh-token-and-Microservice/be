package com.example.member_service.client;

import com.example.member_service.dto.response.EventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service")
public interface EventClient {
    @GetMapping("/events/{eventId}/internal")
    EventResponse getEventInternal(@PathVariable("eventId") String eventId);
}
