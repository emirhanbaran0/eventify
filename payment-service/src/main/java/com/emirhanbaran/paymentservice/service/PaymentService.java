package com.emirhanbaran.paymentservice.service;

import com.emirhanbaran.paymentservice.entity.*;
import com.emirhanbaran.paymentservice.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxPaymentRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @SneakyThrows
    public void processPayment(Long bookingId, Long userId, BigDecimal price) {

        boolean success = price.compareTo(BigDecimal.valueOf(1000)) < 0;
        PaymentStatus status = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        Payment payment = Payment.builder()
                .bookingId(bookingId)
                .userId(userId)
                .amount(price)
                .status(status)
                .build();

        paymentRepository.save(payment);

        String eventType = success ? "PAYMENT_SUCCEEDED" : "PAYMENT_FAILED";

        OutboxPayment outbox = OutboxPayment.builder()
                .aggregateType("PAYMENT")
                .aggregateId(payment.getId())
                .type(eventType)
                .payload(objectMapper.writeValueAsString(payment))
                .build();

        outboxRepository.save(outbox);
    }
}