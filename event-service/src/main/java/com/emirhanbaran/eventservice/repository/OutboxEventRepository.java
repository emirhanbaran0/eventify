package com.emirhanbaran.eventservice.repository;

import com.emirhanbaran.eventservice.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

}