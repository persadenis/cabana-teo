package com.cabanateo;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {
    private List<Reservation> reservations;

    public ReservationService() {
        reservations = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        if (reservation == null)
            throw new IllegalArgumentException("The reservation can't be null");
        reservations.add(reservation);
    }

    public Reservation findReservationById(int id) {
        if (id <= 0)
            throw new IllegalArgumentException("The id should be a non-zero positive integer");

        for (Reservation r : this.reservations) {
            if (r.getId() == id)
                return r;
        }
        throw new IllegalArgumentException("The id doesn't exist");
    }

    public void updateStatus(int id, ReservationStatus newStatus) {
        Reservation reservation = findReservationById(id);
        reservation.setStatus(newStatus);
    }

    public void updateDates(int id, LocalDateTime checkIn, LocalDateTime checkOut) {
        Reservation reservation = findReservationById(id);
        reservation.setCheckIn(checkIn);
        reservation.setCheckOut(checkOut);
    }

    public void deleteReservation(int id)
    {
        Reservation reservation = findReservationById(id);
        reservations.remove(reservation);
    }

    public List<Reservation> getAllReservations()
    {
        return reservations;
    }




}
