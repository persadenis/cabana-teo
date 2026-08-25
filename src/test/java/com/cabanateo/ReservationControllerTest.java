package com.cabanateo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateValidReservation() throws Exception {

        String json = """
        {
          "guestName": "Test User",
          "email": "test@email.com",
          "phone": "0712345678",
          "checkIn": "2026-12-10T15:00:00",
          "checkOut": "2026-12-12T12:00:00",
          "numberOfGuests": 4,
          "notes": "Test reservation"
        }
        """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

    }

    @Test
    void shouldRejectInvalidEmail() throws Exception {

        String json = """
        {
          "guestName": "Test User",
          "email": "not-an-email",
          "phone": "0712345678",
          "checkIn": "2026-12-15T15:00:00",
          "checkOut": "2026-12-17T12:00:00",
          "numberOfGuests": 4,
          "notes": ""
        }
        """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectTooManyGuests() throws Exception {

        String json = """
        {
          "guestName": "Test User",
          "email": "test2@email.com",
          "phone": "0712345678",
          "checkIn": "2026-12-20T15:00:00",
          "checkOut": "2026-12-22T12:00:00",
          "numberOfGuests": 14,
          "notes": ""
        }
        """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAcceptReservationWithExactlyThreeHourGap() throws Exception {

        String firstReservation = """
        {
          "guestName": "First Guest",
          "email": "first@email.com",
          "phone": "0711111111",
          "checkIn": "2027-01-10T15:00:00",
          "checkOut": "2027-01-12T12:00:00",
          "numberOfGuests": 4,
          "notes": ""
        }
        """;

        String secondReservation = """
        {
          "guestName": "Second Guest",
          "email": "second@email.com",
          "phone": "0722222222",
          "checkIn": "2027-01-12T15:00:00",
          "checkOut": "2027-01-14T12:00:00",
          "numberOfGuests": 3,
          "notes": ""
        }
        """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstReservation))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondReservation))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRejectReservationWithLessThanThreeHourGap() throws Exception {

        String firstReservation = """
        {
          "guestName": "First Guest",
          "email": "first2@email.com",
          "phone": "0711111111",
          "checkIn": "2027-02-10T15:00:00",
          "checkOut": "2027-02-12T12:00:00",
          "numberOfGuests": 4,
          "notes": ""
        }
        """;

        String secondReservation = """
        {
          "guestName": "Second Guest",
          "email": "second2@email.com",
          "phone": "0722222222",
          "checkIn": "2027-02-12T14:59:59",
          "checkOut": "2027-02-14T12:00:00",
          "numberOfGuests": 3,
          "notes": ""
        }
        """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstReservation))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondReservation))
                .andExpect(status().isConflict());
    }

}