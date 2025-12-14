package com.emirhanbaran.bookingservice.dto;

import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Event ID is required")
        Long eventId
) {}