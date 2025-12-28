package com.emirhanbaran.paymentservice.repository;

import com.emirhanbaran.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}