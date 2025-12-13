// java
package com.emirhanbaran.eventservice.repository;

import com.emirhanbaran.eventservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE " +
            "(e.city = COALESCE(:city, e.city)) AND " +
            "(e.eventDate >= COALESCE(:startDate, e.eventDate)) AND " +
            "(e.eventDate <= COALESCE(:endDate, e.eventDate))")
    List<Event> findEvents(
            @Param("city") String city,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}