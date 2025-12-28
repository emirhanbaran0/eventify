package com.emirhanbaran.eventservice.kafka;

import com.emirhanbaran.eventservice.entity.Event;
import com.emirhanbaran.eventservice.entity.OutboxEvent;
import com.emirhanbaran.eventservice.repository.EventRepository;
import com.emirhanbaran.eventservice.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventSagaConsumer {

    private final EventRepository eventRepository;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "booking-service.bookings", groupId = "event-saga-group")
    @Transactional
    public void consumeBookingEvents(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);

            String status = node.get("status").asText();
            Long eventId = node.get("eventId").asLong();
            Long bookingId = node.get("id").asLong();
            Long userId = node.get("userId").asLong();

            if ("PENDING".equals(status)) {
                log.info("Attempting to reserve seat for booking {}", bookingId);

                Optional<Event> event = eventRepository.findById(eventId);

                if (event.isPresent() && event.get().getCapacity() > 0) {
                    eventRepository.decrementCapacity(eventId);
                    log.info("Seat reserved for event {}. Emitting SEATS_RESERVED.", eventId);

                    // Emit SEATS_RESERVED event
                    // We create a temporary JSON object for the event payload

                    String payload = String.format("{\"bookingId\":%d, \"userId\":%d, \"eventId\":%d, \"price\":%d}", bookingId, userId, eventId, event.get().getPrice().longValue());

                    saveOutboxEvent(bookingId, "SEATS_RESERVED", payload);

                } else {
                    log.error("No capacity for event {}. Emitting SEATS_RESERVATION_FAILED.", eventId);
                    String payload = String.format("{\"bookingId\":%d}", bookingId);
                    saveOutboxEvent(bookingId, "SEATS_RESERVATION_FAILED", payload);
                }
            }
            else if ("CANCELLED".equals(status)) {
                log.info("Booking {} cancelled. Releasing seat.", bookingId);
                eventRepository.incrementCapacity(eventId);
            }

        } catch (Exception e) {
            log.error("Error processing booking event", e);
        }
    }

    private void saveOutboxEvent(Long aggregateId, String type, String payload) {
        OutboxEvent outbox = OutboxEvent.builder()
                .aggregateType("SAGA")
                .aggregateId(aggregateId)
                .type(type)
                .payload(payload)
                .build();
        outboxRepository.save(outbox);
    }
}