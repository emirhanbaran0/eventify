package com.emirhanbaran.paymentservice.kafka;

import com.emirhanbaran.paymentservice.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSagaConsumer {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "event-service.events", groupId = "payment-saga-group")
    public void consumeEventEvents(String message) {
        try {

            JsonNode node = objectMapper.readTree(message);

            if (node.has("bookingId") && node.has("userId")) {
                Long bookingId = node.get("bookingId").asLong();
                Long userId = node.get("userId").asLong();
                Long price = node.get("price").asLong();

                log.info("Seats reserved for booking {}. Processing payment...", bookingId);

                paymentService.processPayment(bookingId, userId, BigDecimal.valueOf(price));
            }

        } catch (Exception e) {
            log.error("Error processing event", e);
        }
    }
}