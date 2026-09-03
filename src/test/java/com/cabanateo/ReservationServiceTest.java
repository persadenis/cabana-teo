package com.cabanateo;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class ReservationServiceTest {

    @Mock
    private ReservationRepository repository;

    private ReservationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ReservationService(repository);
    }

    @Test
    void shouldFindReservationById() {

        Reservation reservation = new Reservation();
        reservation.setId(5);
        reservation.setGuestName("Test Guest");

        when(repository.findById(5))
                .thenReturn(Optional.of(reservation));

        Reservation result = service.findReservationById(5);

        assertEquals(5, result.getId());
        assertEquals("Test Guest", result.getGuestName());
    }

    @Test
    void shouldThrowWhenCheckOutIsBeforeCheckIn() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(
                        "Test Guest",
                        "test@email.com",
                        "0712345678",
                        LocalDateTime.of(2027, 5, 10, 15, 0),
                        LocalDateTime.of(2027, 5, 10, 12, 0),
                        4,
                        "Test"
                )
        );

        assertEquals(
                "The checkOut should be after the checkIn and should not be null",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowWhenReservationDoesNotExist() {

        when(repository.findById(9999))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.findReservationById(9999)
        );

        assertEquals("The id doesn't exist", exception.getMessage());
    }

    @Test
    void shouldSaveReservationWhenThereIsNoOverlap() {

        Reservation reservation = new Reservation(
                "Test Guest",
                "test@email.com",
                "0712345678",
                LocalDateTime.of(2027, 5, 10, 15, 0),
                LocalDateTime.of(2027, 5, 12, 12, 0),
                4,
                "Test"
        );

        when(repository.existsByCheckInBeforeAndCheckOutAfter(
                any(),
                any()
        )).thenReturn(false);

        when(repository.save(reservation))
                .thenReturn(reservation);

        Reservation result = service.addReservation(reservation);

        assertEquals(reservation, result);

        verify(repository).save(reservation);
    }

    @Test
    void shouldNotSaveReservationWhenThereIsOverlap() {

        Reservation reservation = new Reservation(
                "Test Guest",
                "test@email.com",
                "0712345678",
                LocalDateTime.of(2027, 5, 10, 15, 0),
                LocalDateTime.of(2027, 5, 12, 12, 0),
                4,
                "Test"
        );
        ;

        when(repository.existsByCheckInBeforeAndCheckOutAfter(
                any(),
                any()
        )).thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> service.addReservation(reservation)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeleteReservation() {
        Reservation reservation = new Reservation(
                "Test Guest",
                "test@email.com",
                "0712345678",
                LocalDateTime.of(2027, 5, 10, 15, 0),
                LocalDateTime.of(2027, 5, 12, 12, 0),
                4,
                "Test"
        );
        reservation.setId(5);

        when(repository.findById(5))
                .thenReturn(Optional.of(reservation));

        service.deleteReservation(5);

        verify(repository).delete(reservation);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentReservation() {
        when(repository.findById(9999))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteReservation(9999)
        );

        assertEquals("The id doesn't exist", exception.getMessage());

        verify(repository, never()).delete(any());
    }

    @Test
    void shouldUpdateReservationStatus() {

        Reservation reservation = new Reservation(
                "Old Guest",
                "old@email.com",
                "0711111111",
                LocalDateTime.of(2027, 5, 10, 15, 0),
                LocalDateTime.of(2027, 5, 12, 12, 0),
                4,
                "Old notes"
        );

        reservation.setId(5);

        when(repository.findById(5))
                .thenReturn(Optional.of(reservation));

        service.updateStatus(5, ReservationStatus.CONFIRMED);

        assertEquals(
                ReservationStatus.CONFIRMED,
                reservation.getStatus()
        );

        verify(repository).save(reservation);
    }

    @Test
    void shouldThrowWhenUpdatingCheckInAndCheckOutInvalid() {
        Reservation reservation = new Reservation(
                "Old Guest",
                "old@email.com",
                "0711111111",
                LocalDateTime.of(2027, 5, 10, 15, 0),
                LocalDateTime.of(2027, 5, 12, 12, 0),
                4,
                "Old notes"
        );

        reservation.setId(5);

        when(repository.findById(5))
                .thenReturn(Optional.of(reservation));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateDates(
                        5,
                        LocalDateTime.of(2027, 5, 12, 15, 0),
                        LocalDateTime.of(2027, 5, 10, 12, 0)
                )
        );

        assertEquals("The checkOut should be after the checkIn and should not be null", exception.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowWhenConfirmingReservationThatOverlapsAnotherConfirmedReservation() {
        Reservation reservation = new Reservation(
                "Old Guest",
                "old@email.com",
                "0711111111",
                LocalDateTime.of(2027, 5, 8, 15, 0),
                LocalDateTime.of(2027, 5, 10, 12, 0),
                4,
                "Old notes"
        );

        reservation.setId(5);

        when(repository.findById(5))
                .thenReturn(Optional.of(reservation));

        when(repository.existsByCheckInBeforeAndCheckOutAfterAndIdNotAndStatus(
                any(),
                any(),
                eq(5),
                eq(ReservationStatus.CONFIRMED)
        )).thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateStatus(
                        5,
                        ReservationStatus.CONFIRMED)
        );

        assertEquals("There must be at least 3 hours between confirmed reservations", exception.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    void shouldCancelReservation() {
        Reservation reservation = new Reservation(
                "Old Guest",
                "old@email.com",
                "0711111111",
                LocalDateTime.of(2027, 5, 8, 15, 0),
                LocalDateTime.of(2027, 5, 10, 12, 0),
                4,
                "Old notes"
        );

        reservation.setId(5);

        when(repository.findById(5))
                .thenReturn(Optional.of(reservation));

        service.updateStatus(5, ReservationStatus.CANCELLED);

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());

        verify(repository).save(reservation);

    }

    @Test
    void shouldThrowWhenUpdatingStatusOfCancelledReservation() {
        Reservation reservation = new Reservation(
                "Old Guest",
                "old@email.com",
                "0711111111",
                LocalDateTime.of(2027, 5, 8, 15, 0),
                LocalDateTime.of(2027, 5, 10, 12, 0),
                4,
                "Old notes"
        );

        reservation.setId(5);
        reservation.setStatus(ReservationStatus.CANCELLED);

        when(repository.findById(5))
                .thenReturn(Optional.of(reservation));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateStatus(5, ReservationStatus.CONFIRMED)
        );

        assertEquals("You can't change the status of a cancelled reservation", exception.getMessage());

        verify(repository, never()).save(any());
    }
}