package com.emirhanbaran.bookingservice.repository;

import com.emirhanbaran.bookingservice.entity.Booking;
import com.emirhanbaran.bookingservice.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    Integer countByEventIdAndStatus(Long eventId, BookingStatus status);
}