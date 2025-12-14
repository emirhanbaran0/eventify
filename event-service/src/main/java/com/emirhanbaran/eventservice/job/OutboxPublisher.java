package com.emirhanbaran.eventservice.job;

import com.emirhanbaran.eventservice.entity.OutboxEvent;
import com.emirhanbaran.eventservice.repository.OutboxEventRepository;
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
public class OutboxPublisher {

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000) // Run every 5 seconds
    @Transactional
    public void publishEvents() {
        List<OutboxEvent> events = outboxRepository.findAll();

        if (events.isEmpty()) return;

        log.info("Found {} events in outbox. Publishing...", events.size());

        for (OutboxEvent event : events) {
            // Topic: event-service.events
            // Key: String.valueOf(event.getAggregateId()) -> ensures ordering for same ID
            String topic = "event-service.events";
            String key = String.valueOf(event.getAggregateId());
            String payload = event.getPayload();

            kafkaTemplate.send(topic, key, payload);

            log.info("Published event: ID={}, Type={}", event.getId(), event.getType());

            // Delete after processing (Simple Outbox Pattern)
            outboxRepository.delete(event);
        }
    }
}