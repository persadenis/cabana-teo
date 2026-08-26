package com.cabanateo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

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
    void shouldReturnAllReservations() throws Exception {

        String json = """
        {
          "guestName": "Test Guest",
          "email": "guest@email.com",
          "phone": "0712345678",
          "checkIn": "2027-03-10T15:00:00",
          "checkOut": "2027-03-12T12:00:00",
          "numberOfGuests": 4,
          "notes": "Testing GET"
        }
        """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].guestName").value("Test Guest"))
                .andExpect(jsonPath("$[0].email").value("guest@email.com"))
                .andExpect(jsonPath("$[0].numberOfGuests").value(4));
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

    @Test
    void shouldReturnReservationById() throws Exception {

        String json = """
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
        MvcResult result = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isCreated())
                        .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        Reservation createdReservation = objectMapper.readValue(responseBody, Reservation.class);

        int id = createdReservation.getId();

        mockMvc.perform(get("/api/reservations/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guestName").value("First Guest"))
                .andExpect(jsonPath("$.email").value("first@email.com"))
                .andExpect(jsonPath("$.id").value(id));

    }

    @Test
    void shouldReturnNotFoundForNonExistentId() throws Exception {
        mockMvc.perform(get("/api/reservations/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteReservation() throws Exception {
        String json = """
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

        MvcResult result = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Reservation createdReservation = objectMapper.readValue(responseBody, Reservation.class);
        int id = createdReservation.getId();
        mockMvc.perform(delete("/api/reservations/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/reservations/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteNonExistentReservation() throws Exception {
        mockMvc.perform(delete("/api/reservations/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateReservation() throws Exception {
        String json = """
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
        MvcResult result = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Reservation createdReservation = objectMapper.readValue(responseBody, Reservation.class);
        int id = createdReservation.getId();

        String updateJson = """
                {
                  "guestName": "Updated Guest",
                  "email": "updated@email.com",
                  "phone": "0799999999",
                  "checkIn": "2027-03-10T15:00:00",
                  "checkOut": "2027-03-12T12:00:00",
                  "numberOfGuests": 6,
                  "notes": "Updated reservation"
                }
                """;

        mockMvc.perform(put("/api/reservations/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reservations/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.guestName").value("Updated Guest"))
                .andExpect(jsonPath("$.email").value("updated@email.com"))
                .andExpect(jsonPath("$.phone").value("0799999999"))
                .andExpect(jsonPath("$.checkIn").value("2027-03-10T15:00:00"))
                .andExpect(jsonPath("$.checkOut").value("2027-03-12T12:00:00"))
                .andExpect(jsonPath("$.numberOfGuests").value(6))
                .andExpect(jsonPath("$.notes").value("Updated reservation"));
    }

    @Test
    void shouldNotUpdateReservationWithNonExistentId() throws Exception {

        String updateJson = """
            {
              "guestName": "Updated Guest",
              "email": "updated@email.com",
              "phone": "0799999999",
              "checkIn": "2027-03-10T15:00:00",
              "checkOut": "2027-03-12T12:00:00",
              "numberOfGuests": 6,
              "notes": "Updated reservation"
            }
            """;

        mockMvc.perform(put("/api/reservations/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());
    }
}