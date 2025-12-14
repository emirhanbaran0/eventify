package com.emirhanbaran.bookingservice.client.dto;

import java.time.LocalDateTime;

public record ExternalEventDto(
        Long id,
        String title,
        LocalDateTime eventDate,
        Integer capacity
) {}