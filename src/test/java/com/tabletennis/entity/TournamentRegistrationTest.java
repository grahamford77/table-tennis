package com.tabletennis.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TournamentRegistration entity class.
 * Tests all getters, setters, constructors, and relationships.
 */
@DisplayName("Tournament Registration Entity Tests")
class TournamentRegistrationTest {

    private TournamentRegistration registration;
    private Tournament tournament;

    @BeforeEach
    void setUp() {
        registration = new TournamentRegistration();
        tournament = new Tournament("Test Tournament", "Description",
                                   LocalDate.of(2025, 8, 15),
                                   LocalTime.of(10, 0),
                                   "Test Location", 32);
        tournament.setId(1L);
    }

    @Test
    @DisplayName("Should create empty registration with default constructor")
    void shouldCreateEmptyRegistration() {
        assertNotNull(registration);
        assertNull(registration.getId());
        assertNull(registration.getFirstName());
        assertNull(registration.getSurname());
        assertNull(registration.getEmail());
        assertNull(registration.getTournament());
    }

    @Test
    @DisplayName("Should create registration with all parameters")
    void shouldCreateRegistrationWithAllParameters() {
        TournamentRegistration paramRegistration = new TournamentRegistration(
            "John", "Doe", "john.doe@email.com", tournament
        );

        assertEquals("John", paramRegistration.getFirstName());
        assertEquals("Doe", paramRegistration.getSurname());
        assertEquals("john.doe@email.com", paramRegistration.getEmail());
        assertEquals(tournament, paramRegistration.getTournament());
    }

    @Test
    @DisplayName("Should set and get registration ID correctly")
    void shouldSetAndGetId() {
        Long expectedId = 1L;
        registration.setId(expectedId);
        assertEquals(expectedId, registration.getId());
    }

    @Test
    @DisplayName("Should set and get first name correctly")
    void shouldSetAndGetFirstName() {
        String expectedFirstName = "Alice";
        registration.setFirstName(expectedFirstName);
        assertEquals(expectedFirstName, registration.getFirstName());
    }

    @Test
    @DisplayName("Should set and get surname correctly")
    void shouldSetAndGetSurname() {
        String expectedSurname = "Johnson";
        registration.setSurname(expectedSurname);
        assertEquals(expectedSurname, registration.getSurname());
    }

    @Test
    @DisplayName("Should set and get email correctly")
    void shouldSetAndGetEmail() {
        String expectedEmail = "alice.johnson@email.com";
        registration.setEmail(expectedEmail);
        assertEquals(expectedEmail, registration.getEmail());
    }

    @Test
    @DisplayName("Should set and get tournament correctly")
    void shouldSetAndGetTournament() {
        registration.setTournament(tournament);
        assertEquals(tournament, registration.getTournament());
        assertEquals("Test Tournament", registration.getTournament().getName());
    }

    @Test
    @DisplayName("Should handle null tournament gracefully")
    void shouldHandleNullTournament() {
        registration.setTournament(null);
        assertNull(registration.getTournament());
    }

    @Test
    @DisplayName("Should handle empty strings in name fields")
    void shouldHandleEmptyStrings() {
        registration.setFirstName("");
        registration.setSurname("");
        registration.setEmail("");

        assertEquals("", registration.getFirstName());
        assertEquals("", registration.getSurname());
        assertEquals("", registration.getEmail());
    }

    @Test
    @DisplayName("Should handle whitespace in name fields")
    void shouldHandleWhitespace() {
        registration.setFirstName("  John  ");
        registration.setSurname("  Doe  ");
        registration.setEmail("  john@email.com  ");

        assertEquals("  John  ", registration.getFirstName());
        assertEquals("  Doe  ", registration.getSurname());
        assertEquals("  john@email.com  ", registration.getEmail());
    }
}
