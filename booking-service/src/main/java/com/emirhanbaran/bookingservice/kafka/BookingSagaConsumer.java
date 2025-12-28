package com.emirhanbaran.bookingservice.kafka;


import com.emirhanbaran.bookingservice.service.BookingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingSagaConsumer {

    private final BookingService bookingService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-service.payments", groupId = "booking-saga-group")
    public void consumeEventEvents(String message) {
        try {

            JsonNode outerNode = objectMapper.readTree(message);
            JsonNode node = objectMapper.readTree(outerNode.asText());

            if (node.has("bookingId") && node.has("status")) {
                Long bookingId = node.get("bookingId").asLong();
                String status = node.get("status").asText();

                if("SUCCESS".equals(status)) {
                    log.info("Payment succeeded for booking {}. Confirming booking...", bookingId);
                    bookingService.confirmBooking(bookingId);
                } else if ("FAILED".equals(status)) {
                    log.info("Payment failed for booking {}. Cancelling booking...", bookingId);
                    bookingService.cancelBooking(bookingId);
                };
            }

        } catch (Exception e) {
            log.error("Error processing event", e);
        }
    }
}