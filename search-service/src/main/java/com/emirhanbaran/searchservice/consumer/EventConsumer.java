package com.emirhanbaran.searchservice.consumer;

import com.emirhanbaran.searchservice.document.EventDocument;
import com.emirhanbaran.searchservice.repository.EventSearchRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final EventSearchRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "event-service.events", groupId = "search-service-group")
    public void consumeEvent(String message) {
        try {
            log.info("Received event from Kafka: {}", message);

            JsonNode node = objectMapper.readTree(message);

            EventDocument document = EventDocument.builder()
                    .id(node.get("id").asText())
                    .title(node.get("title").asText())
                    .description(node.get("description").asText())
                    .category(node.get("category").asText())
                    .city(node.get("city").asText())
                    .eventDate(LocalDateTime.parse(node.get("eventDate").asText()))
                    .price(node.get("price").decimalValue())
                    .build();

            repository.save(document);
            log.info("Indexed event id: {}", document.getId());

        } catch (Exception e) {
            log.error("Failed to process event", e);
        }
    }
}