package com.emirhanbaran.bookingservice.job;

import com.emirhanbaran.bookingservice.entity.OutboxBooking;
import com.emirhanbaran.bookingservice.repository.OutboxBookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingOutboxPublisher {

    private final OutboxBookingRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000) // Every 5 seconds
    @Transactional
    public void publishBookings() {
        List<OutboxBooking> events = outboxRepository.findAll();

        if (events.isEmpty()) return;

        log.info("Found {} unsent bookings. Publishing...", events.size());

        for (OutboxBooking event : events) {
            String topic = "booking-service.bookings";
            String key = String.valueOf(event.getAggregateId());

            kafkaTemplate.send(topic, key, event.getPayload());

            log.info("Published booking event: ID={}, Type={}", event.getId(), event.getType());

            outboxRepository.delete(event);
        }
    }
}