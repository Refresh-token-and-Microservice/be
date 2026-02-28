package com.example.event_service.mapper;

import com.example.event_service.dto.response.EventResponse;
import com.example.event_service.entity.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventResponse toResponse(Event event);
}
