package com.cabanateo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ReservationController {
    private final ReservationService service;
    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @GetMapping("/api/reservations")
    public List<Reservation> getAllReservations() {
        return service.getAllReservations();
    }

    @GetMapping("/api/reservations/{id}")
    public Reservation getReservationById(@PathVariable int id) {
        try {
            return service.findReservationById(id);
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

        service.addReservation(reservation);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservation);
    }

    @DeleteMapping("/api/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable int id) {
        try {
            service.deleteReservation(id);
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
            Reservation existingReservation = service.findReservationById(id);

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
