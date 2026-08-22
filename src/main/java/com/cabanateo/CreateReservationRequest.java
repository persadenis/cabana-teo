package com.cabanateo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public class CreateReservationRequest {

    @NotBlank
    private String guestName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String phone;

    @NotNull
    private LocalDateTime checkIn;

    @NotNull
    private LocalDateTime checkOut;

    @NotNull
    @Min(1)
    @Max(13)
    private int numberOfGuests;
    private String notes;


    public String getGuestName() {
        return guestName;
    }


    public String getEmail() {
        return email;
    }


    public String getPhone() {
        return phone;
    }


    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public String getNotes() {
        return notes;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}