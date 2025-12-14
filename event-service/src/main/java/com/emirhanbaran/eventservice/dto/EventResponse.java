package com.emirhanbaran.eventservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        String category,
        String city,
        String location,
        LocalDateTime eventDate,
        Integer capacity,
        BigDecimal price
) {}