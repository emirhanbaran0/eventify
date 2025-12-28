package com.emirhanbaran.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "outbox_payments")
public class OutboxPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;
    private Long aggregateId;
    private String type;

    @Column(length = 4000)
    private String payload;

    @CreationTimestamp
    private LocalDateTime createdAt;
}