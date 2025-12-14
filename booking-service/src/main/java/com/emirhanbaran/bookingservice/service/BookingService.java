package com.emirhanbaran.bookingservice.service;

import com.emirhanbaran.bookingservice.client.EventServiceClient;
import com.emirhanbaran.bookingservice.client.dto.ExternalEventDto;
import com.emirhanbaran.bookingservice.dto.BookingRequest;
import com.emirhanbaran.bookingservice.dto.BookingResponse;
import com.emirhanbaran.bookingservice.entity.Booking;
import com.emirhanbaran.bookingservice.entity.BookingStatus;
import com.emirhanbaran.bookingservice.entity.OutboxBooking;
import com.emirhanbaran.bookingservice.mapper.BookingMapper;
import com.emirhanbaran.bookingservice.repository.BookingRepository;
import com.emirhanbaran.bookingservice.repository.OutboxBookingRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final EventServiceClient eventServiceClient;
    private final ObjectMapper objectMapper;
    private final OutboxBookingRepository outboxBookingRepository;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // 1. Call Event Service to get event details (Synchronous)
        ExternalEventDto event;
        try {
            event = eventServiceClient.getEventById(request.eventId());
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("Event not found with id: " + request.eventId());
        }

        // 2. Check Capacity
        // We count how many CONFIRMED bookings exist for this event
        Integer currentBookings = bookingRepository.countByEventIdAndStatus(request.eventId(), BookingStatus.CONFIRMED);

        if (currentBookings >= event.capacity()) {
            throw new IllegalStateException("Event is fully booked!");
        }

        // 3. Save Booking as CONFIRMED (Simple version for now)
        Booking booking = Booking.builder()
                .userId(request.userId())
                .eventId(request.eventId())
                .status(BookingStatus.CONFIRMED)
                .build();

        // 4. Save to Outbox (Same Transaction)
        OutboxBooking outbox = OutboxBooking.builder()
                .aggregateType("BOOKING")
                .aggregateId(booking.getId())
                .type("BOOKING_CONFIRMED") // Since we set status to CONFIRMED immediately
                .payload(objectMapper.writeValueAsString(booking))
                .build();

        outboxBookingRepository.save(outbox);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }
}