package com.emirhanbaran.paymentservice.job;

import com.emirhanbaran.paymentservice.entity.OutboxPayment;
import com.emirhanbaran.paymentservice.repository.OutboxPaymentRepository;
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
public class PaymentOutboxPublisher {

    private final OutboxPaymentRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPayments() {
        List<OutboxPayment> events = outboxRepository.findAll();
        if (events.isEmpty()) return;

        for (OutboxPayment event : events) {
            String topic = "payment-service.payments";
            String key = String.valueOf(event.getAggregateId());
            kafkaTemplate.send(topic, key, event.getPayload());

            log.info("Published payment event: {}", event.getType());
            outboxRepository.delete(event);
        }
    }
}