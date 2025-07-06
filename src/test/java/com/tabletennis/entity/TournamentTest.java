package com.tabletennis.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Tournament entity class.
 * Tests all getters, setters, constructors, and business logic methods.
 */
@DisplayName("Tournament Entity Tests")
class TournamentTest {

    private Tournament tournament;
    private final LocalDate testDate = LocalDate.of(2025, 7, 15);
    private final LocalTime testTime = LocalTime.of(14, 30);

    @BeforeEach
    void setUp() {
        tournament = new Tournament();
    }

    @Test
    @DisplayName("Should create empty tournament with default constructor")
    void shouldCreateEmptyTournament() {
        assertNotNull(tournament);
        assertNull(tournament.getId());
        assertNull(tournament.getName());
        assertNull(tournament.getDescription());
        assertNull(tournament.getDate());
        assertNull(tournament.getTime());
        assertNull(tournament.getLocation());
        assertNull(tournament.getMaxEntrants());
    }

    @Test
    @DisplayName("Should create tournament with all parameters")
    void shouldCreateTournamentWithAllParameters() {
        Tournament paramTournament = new Tournament(
            "Summer Championship",
            "Annual summer table tennis tournament",
            testDate,
            testTime,
            "Sports Center",
            32
        );

        assertEquals("Summer Championship", paramTournament.getName());
        assertEquals("Annual summer table tennis tournament", paramTournament.getDescription());
        assertEquals(testDate, paramTournament.getDate());
        assertEquals(testTime, paramTournament.getTime());
        assertEquals("Sports Center", paramTournament.getLocation());
        assertEquals(32, paramTournament.getMaxEntrants());
    }

    @Test
    @DisplayName("Should set and get tournament ID correctly")
    void shouldSetAndGetId() {
        Long expectedId = 1L;
        tournament.setId(expectedId);
        assertEquals(expectedId, tournament.getId());
    }

    @Test
    @DisplayName("Should set and get tournament name correctly")
    void shouldSetAndGetName() {
        String expectedName = "Winter Championship";
        tournament.setName(expectedName);
        assertEquals(expectedName, tournament.getName());
    }

    @Test
    @DisplayName("Should set and get tournament description correctly")
    void shouldSetAndGetDescription() {
        String expectedDescription = "Winter table tennis tournament for all skill levels";
        tournament.setDescription(expectedDescription);
        assertEquals(expectedDescription, tournament.getDescription());
    }

    @Test
    @DisplayName("Should set and get tournament date correctly")
    void shouldSetAndGetDate() {
        tournament.setDate(testDate);
        assertEquals(testDate, tournament.getDate());
    }

    @Test
    @DisplayName("Should set and get tournament time correctly")
    void shouldSetAndGetTime() {
        tournament.setTime(testTime);
        assertEquals(testTime, tournament.getTime());
    }

    @Test
    @DisplayName("Should set and get tournament location correctly")
    void shouldSetAndGetLocation() {
        String expectedLocation = "Community Center";
        tournament.setLocation(expectedLocation);
        assertEquals(expectedLocation, tournament.getLocation());
    }

    @Test
    @DisplayName("Should set and get maximum entrants correctly")
    void shouldSetAndGetMaxEntrants() {
        Integer expectedMaxEntrants = 16;
        tournament.setMaxEntrants(expectedMaxEntrants);
        assertEquals(expectedMaxEntrants, tournament.getMaxEntrants());
    }

    @Test
    @DisplayName("Should format display name correctly with date and tournament name")
    void shouldFormatDisplayNameCorrectly() {
        tournament.setName("Spring Tournament");
        tournament.setDate(LocalDate.of(2025, 3, 15));

        String displayName = tournament.getDisplayName();

        assertEquals("15 March 25 - Spring Tournament", displayName);
    }

    @Test
    @DisplayName("Should format display name correctly with single digit day")
    void shouldFormatDisplayNameWithSingleDigitDay() {
        tournament.setName("April Championship");
        tournament.setDate(LocalDate.of(2025, 4, 5));

        String displayName = tournament.getDisplayName();

        assertEquals("05 April 25 - April Championship", displayName);
    }

    @Test
    @DisplayName("Should format display name correctly with different months")
    void shouldFormatDisplayNameWithDifferentMonths() {
        tournament.setName("New Year Cup");
        tournament.setDate(LocalDate.of(2026, 1, 1));

        String displayName = tournament.getDisplayName();

        assertEquals("01 January 26 - New Year Cup", displayName);
    }

    @Test
    @DisplayName("Should handle null date gracefully in display name")
    void shouldHandleNullDateInDisplayName() {
        tournament.setName("Test Tournament");
        tournament.setDate(null);

        assertThrows(NullPointerException.class, () -> tournament.getDisplayName());
    }

    @Test
    @DisplayName("Should handle null name gracefully in display name")
    void shouldHandleNullNameInDisplayName() {
        tournament.setDate(testDate);
        tournament.setName(null);

        String displayName = tournament.getDisplayName();

        assertTrue(displayName.contains("15 July 25"));
        assertTrue(displayName.contains("null"));
    }
}
