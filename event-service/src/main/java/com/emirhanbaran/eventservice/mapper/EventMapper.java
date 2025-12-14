package com.emirhanbaran.eventservice.mapper;

import com.emirhanbaran.eventservice.dto.EventRequest;
import com.emirhanbaran.eventservice.dto.EventResponse;
import com.emirhanbaran.eventservice.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event toEntity(EventRequest request) {
        return Event.builder()
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .city(request.city())
                .location(request.location())
                .eventDate(request.eventDate())
                .capacity(request.capacity())
                .price(request.price())
                .build();
    }

    public EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getCity(),
                event.getLocation(),
                event.getEventDate(),
                event.getCapacity(),
                event.getPrice()
        );
    }

    public void updateEntity(Event event, EventRequest request) {
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setCategory(request.category());
        event.setCity(request.city());
        event.setLocation(request.location());
        event.setEventDate(request.eventDate());
        event.setCapacity(request.capacity());
        event.setPrice(request.price());
    }
}