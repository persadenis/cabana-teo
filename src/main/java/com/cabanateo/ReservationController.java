package com.cabanateo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ReservationController {
    private final ReservationManager manager;
    public ReservationController() {
        this.manager = new ReservationManager();

        manager.addReservation(
                new Reservation(
                        "Andrei",
                        "andrei@email.com",
                        "0712345678",
                        LocalDateTime.of(2026, 8, 20, 15, 0),
                        LocalDateTime.of(2026, 8, 22, 12, 0),
                        4,
                        "Test reservation"
                )
        );
    }
    @GetMapping("/api/reservations")
    public List<Reservation> getAllReservations() {
        return manager.getAllReservations();
    }

    @GetMapping("/api/reservations/{id}")
    public Reservation getReservationById(@PathVariable int id) {
        try {
            return manager.findReservationById(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
        }
    }

    @PostMapping("/api/reservations")
    public ResponseEntity<Reservation> createReservation(
            @RequestBody Reservation reservation) {

        manager.addReservation(reservation);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservation);
    }

    @DeleteMapping("/api/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable int id) {
        try {
            manager.deleteReservation(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
        }
    }

    @PutMapping("/api/reservations/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable int id,
            @RequestBody Reservation updatedReservation) {

        try {
            Reservation existingReservation = manager.findReservationById(id);

            existingReservation.setGuestName(updatedReservation.getGuestName());
            existingReservation.setEmail(updatedReservation.getEmail());
            existingReservation.setPhone(updatedReservation.getPhone());
            existingReservation.setCheckIn(updatedReservation.getCheckIn());
            existingReservation.setCheckOut(updatedReservation.getCheckOut());
            existingReservation.setNumberOfGuests(updatedReservation.getNumberOfGuests());
            existingReservation.setNotes(updatedReservation.getNotes());

            return ResponseEntity.ok(existingReservation);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
        }
    }


}
