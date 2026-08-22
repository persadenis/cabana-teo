
package com.cabanateo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReservationRepository
        extends JpaRepository<Reservation, Integer> {
            boolean existsByCheckInBeforeAndCheckOutAfter(
                    LocalDateTime checkOut,
                    LocalDateTime checkIn
            );
        boolean existsByCheckInBeforeAndCheckOutAfterAndIdNot(
                LocalDateTime checkOut,
                LocalDateTime checkIn,
                int id
        );
}
