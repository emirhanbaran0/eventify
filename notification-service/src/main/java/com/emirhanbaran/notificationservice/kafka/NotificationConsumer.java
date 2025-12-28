package com.emirhanbaran.notificationservice.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "booking-service.bookings", groupId = "notification-group")
    public void consumeBookingEvents(String message) {
        try {

            JsonNode node = objectMapper.readTree(message);

            if (node.has("status") && node.has("userId")) {
                String status = node.get("status").asText();
                Long userId = node.get("userId").asLong();
                Long bookingId = node.get("id").asLong();

                if ("BOOKING_CONFIRMED".equals(status)) {
                    sendEmail(userId, "Booking " + bookingId + " Confirmed!",
                            "Your payment was successful and seats are reserved.");
                }
                else if ("BOOKING_CANCELLED".equals(status)) {
                    sendEmail(userId, "Booking " + bookingId + " Cancelled",
                            "We could not process your booking (Payment failed or No seats).");
                }
            }
        } catch (Exception e) {
            log.error("Failed to process notification", e);
        }
    }

    private void sendEmail(Long userId, String subject, String body) {
        log.info("==================================================");
        log.info("📧 SENDING EMAIL TO USER ID: {}", userId);
        log.info("Subject: {}", subject);
        log.info("Body: {}", body);
        log.info("==================================================");
    }
}
