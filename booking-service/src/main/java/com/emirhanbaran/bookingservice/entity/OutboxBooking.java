package com.emirhanbaran.bookingservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "outbox_bookings")
public class OutboxBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType; // e.g. "BOOKING"
    private Long aggregateId;     // e.g. Booking ID

    private String type;          // e.g. "BOOKING_CONFIRMED"

    @Column(length = 4000)
    private String payload;

    @CreationTimestamp
    private LocalDateTime createdAt;
}