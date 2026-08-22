package com.cabanateo;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation addReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("The reservation can't be null");
        }

        boolean overlaps =
                repository.existsByCheckInBeforeAndCheckOutAfter(
                        reservation.getCheckOut(),
                        reservation.getCheckIn()
                );

        if (overlaps) {
            throw new IllegalStateException(
                    "The selected period is already booked"
            );
        }


        return repository.save(reservation);
    }

    public Reservation findReservationById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "The id should be a non-zero positive integer"
            );
        }

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("The id doesn't exist")
                );
    }

    public void updateStatus(int id, ReservationStatus newStatus) {
        Reservation reservation = findReservationById(id);
        reservation.setStatus(newStatus);
        repository.save(reservation);
    }

    public void updateDates(int id, LocalDateTime checkIn, LocalDateTime checkOut) {
        Reservation reservation = findReservationById(id);
        reservation.setCheckIn(checkIn);
        reservation.setCheckOut(checkOut);
        repository.save(reservation);
    }

    public void deleteReservation(int id) {
        Reservation reservation = findReservationById(id);
        repository.delete(reservation);
    }

    public Reservation updateReservation(
            int id,
            CreateReservationRequest request) {

        Reservation reservation = findReservationById(id);

        reservation.setGuestName(request.getGuestName());
        reservation.setEmail(request.getEmail());
        reservation.setPhone(request.getPhone());
        reservation.setCheckIn(request.getCheckIn());
        reservation.setCheckOut(request.getCheckOut());
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setNotes(request.getNotes());

        boolean overlaps =
                repository.existsByCheckInBeforeAndCheckOutAfterAndIdNot(
                        reservation.getCheckOut(),
                        reservation.getCheckIn(),
                        id
                );

        if(overlaps)
        {
            throw new IllegalStateException(
                    "The selected period is already booked"
            );
        }

        return repository.save(reservation);
    }

    public List<Reservation> getAllReservations()
    {
        return repository.findAll();
    }




}
