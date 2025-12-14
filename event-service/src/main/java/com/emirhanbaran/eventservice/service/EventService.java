package com.emirhanbaran.eventservice.service;

import com.emirhanbaran.eventservice.dto.EventRequest;
import com.emirhanbaran.eventservice.dto.EventResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


public interface EventService {
    EventResponse createEvent(EventRequest request);
    EventResponse getEventById(Long id);
    List<EventResponse> getAllEvents(String city, LocalDateTime startDate, LocalDateTime endDate);
    EventResponse updateEvent(Long id, EventRequest request);
}