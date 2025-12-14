package com.emirhanbaran.eventservice.service.impl;

import com.emirhanbaran.eventservice.dto.EventRequest;
import com.emirhanbaran.eventservice.dto.EventResponse;
import com.emirhanbaran.eventservice.entity.Event;
import com.emirhanbaran.eventservice.entity.OutboxEvent;
import com.emirhanbaran.eventservice.mapper.EventMapper;
import com.emirhanbaran.eventservice.repository.EventRepository;
import com.emirhanbaran.eventservice.repository.OutboxEventRepository;
import com.emirhanbaran.eventservice.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventResponse createEvent(EventRequest request) {
        Event event = eventMapper.toEntity(request);
        Event savedEvent = eventRepository.save(event);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType("EVENT")
                .aggregateId(savedEvent.getId())
                .type("EVENT_CREATED")
                .payload(objectMapper.writeValueAsString(eventMapper.toResponse(savedEvent)))
                .build();

        outboxEventRepository.save(outboxEvent);

        return eventMapper.toResponse(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        return eventRepository.findById(id)
                .map(eventMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents(String city, LocalDateTime startDate, LocalDateTime endDate) {
        return eventRepository.findEvents(city, startDate, endDate)
                .stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));

        eventMapper.updateEntity(existingEvent, request);
        Event updatedEvent = eventRepository.save(existingEvent);

        return eventMapper.toResponse(updatedEvent);
    }
}