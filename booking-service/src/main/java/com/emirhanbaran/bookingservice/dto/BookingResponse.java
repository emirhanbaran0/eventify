package com.emirhanbaran.bookingservice.dto;

import com.emirhanbaran.bookingservice.entity.BookingStatus;
import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Long userId,
        Long eventId,
        BookingStatus status,
        LocalDateTime createdAt
) {}