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

        ExternalEventDto event;
        try {
            event = eventServiceClient.getEventById(request.eventId());
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("Event not found with id: " + request.eventId());
        }


        Booking booking = Booking.builder()
                .userId(request.userId())
                .eventId(request.eventId())
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        OutboxBooking outbox = OutboxBooking.builder()
                .aggregateType("BOOKING")
                .aggregateId(booking.getId())
                .type("BOOKING_CREATED")
                .payload(objectMapper.writeValueAsString(booking))
                .build();

        outboxBookingRepository.save(outbox);

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

    public void confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId));
        booking.setStatus(BookingStatus.CONFIRMED);

        OutboxBooking outbox = OutboxBooking.builder()
                .aggregateType("BOOKING")
                .aggregateId(booking.getId())
                .type("BOOKING_CONFIRMED")
                .payload(objectMapper.writeValueAsString(booking))
                .build();

        outboxBookingRepository.save(outbox);

        bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId));
        booking.setStatus(BookingStatus.CANCELLED);

        OutboxBooking outbox = OutboxBooking.builder()
                .aggregateType("BOOKING")
                .aggregateId(booking.getId())
                .type("BOOKING_FAILED")
                .payload(objectMapper.writeValueAsString(booking))
                .build();

        outboxBookingRepository.save(outbox);

        bookingRepository.save(booking);
    }


}