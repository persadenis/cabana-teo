package com.cabanateo;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
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
        return service.findReservationById(id);
    }

    @PostMapping("/api/reservations")
    public ResponseEntity<Reservation> createReservation(
            @RequestBody @Valid CreateReservationRequest request) {

        Reservation reservation = new Reservation(
                request.getGuestName(),
                request.getEmail(),
                request.getPhone(),
                request.getCheckIn(),
                request.getCheckOut(),
                request.getNumberOfGuests(),
                request.getNotes()
        );

        Reservation savedReservation = service.addReservation(reservation);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedReservation);
    }

    @DeleteMapping("/api/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable int id) {
        service.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/reservations/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable int id,
            @RequestBody @Valid CreateReservationRequest request) {

        Reservation updatedReservation =
                service.updateReservation(id, request);

        return ResponseEntity.ok(updatedReservation);
    }

}
