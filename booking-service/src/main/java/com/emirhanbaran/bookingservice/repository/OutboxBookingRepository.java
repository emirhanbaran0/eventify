package com.emirhanbaran.bookingservice.repository;

import com.emirhanbaran.bookingservice.entity.OutboxBooking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxBookingRepository extends JpaRepository<OutboxBooking, Long> {
}