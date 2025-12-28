package com.emirhanbaran.paymentservice.repository;

import com.emirhanbaran.paymentservice.entity.OutboxPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxPaymentRepository extends JpaRepository<OutboxPayment, Long> {
}