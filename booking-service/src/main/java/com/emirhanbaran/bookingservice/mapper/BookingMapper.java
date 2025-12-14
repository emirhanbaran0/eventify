package com.emirhanbaran.bookingservice.mapper;

import com.emirhanbaran.bookingservice.dto.BookingResponse;
import com.emirhanbaran.bookingservice.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getEventId(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}