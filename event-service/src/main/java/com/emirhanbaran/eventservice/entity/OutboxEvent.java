package com.emirhanbaran.eventservice.entity;

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
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType; // e.g., "EVENT"
    private Long aggregateId;     // e.g., 1

    private String type;          // e.g., "EVENT_CREATED"

    @Column(length = 4000)
    private String payload;       // JSON content

    @CreationTimestamp
    private LocalDateTime createdAt;
}