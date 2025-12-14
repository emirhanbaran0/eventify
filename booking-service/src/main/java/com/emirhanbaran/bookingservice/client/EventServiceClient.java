package com.emirhanbaran.bookingservice.client;

import com.emirhanbaran.bookingservice.client.dto.ExternalEventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service", url = "${application.config.event-url:http://localhost:8081}")
public interface EventServiceClient {

    @GetMapping("/events/{id}")
    ExternalEventDto getEventById(@PathVariable("id") Long id);
}