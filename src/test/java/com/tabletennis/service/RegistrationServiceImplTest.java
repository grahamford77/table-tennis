package com.tabletennis.service;

import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.repository.TournamentRegistrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RegistrationServiceImpl.
 * Tests all service methods including tournament count calculations.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Registration Service Tests")
class RegistrationServiceImplTest {

    @Mock
    private TournamentRegistrationRepository registrationRepository;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private Tournament tournament1;
    private Tournament tournament2;
    private TournamentRegistration registration1;
    private TournamentRegistration registration2;
    private TournamentRegistration registration3;

    @BeforeEach
    void setUp() {
        tournament1 = new Tournament("Summer Championship", "Summer tournament",
                LocalDate.of(2025, 7, 15), LocalTime.of(14, 0),
                "Sports Center", 32);
        tournament1.setId(1L);

        tournament2 = new Tournament("Winter Cup", "Winter tournament",
                LocalDate.of(2025, 12, 20), LocalTime.of(10, 0),
                "Community Hall", 16);
        tournament2.setId(2L);

        registration1 = new TournamentRegistration("John", "Doe", "john@email.com", tournament1);
        registration1.setId(1L);

        registration2 = new TournamentRegistration("Jane", "Smith", "jane@email.com", tournament1);
        registration2.setId(2L);

        registration3 = new TournamentRegistration("Bob", "Johnson", "bob@email.com", tournament2);
        registration3.setId(3L);
    }

    @Test
    @DisplayName("Should find all registrations")
    void shouldFindAllRegistrations() {
        // Given
        List<TournamentRegistration> expectedRegistrations = Arrays.asList(registration1, registration2, registration3);
        when(registrationRepository.findAll()).thenReturn(expectedRegistrations);

        // When
        List<TournamentRegistration> actualRegistrations = registrationService.findAll();

        // Then
        assertEquals(expectedRegistrations, actualRegistrations);
        assertEquals(3, actualRegistrations.size());
        verify(registrationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no registrations exist")
    void shouldReturnEmptyListWhenNoRegistrations() {
        // Given
        when(registrationRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<TournamentRegistration> actualRegistrations = registrationService.findAll();

        // Then
        assertTrue(actualRegistrations.isEmpty());
        verify(registrationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should save registration successfully")
    void shouldSaveRegistrationSuccessfully() {
        // Given
        TournamentRegistration registrationToSave = new TournamentRegistration("Alice", "Brown", "alice@email.com", tournament1);
        TournamentRegistration savedRegistration = new TournamentRegistration("Alice", "Brown", "alice@email.com", tournament1);
        savedRegistration.setId(4L);

        when(registrationRepository.save(registrationToSave)).thenReturn(savedRegistration);

        // When
        TournamentRegistration actualRegistration = registrationService.save(registrationToSave);

        // Then
        assertEquals(savedRegistration, actualRegistration);
        assertEquals(4L, actualRegistration.getId());
        assertEquals("Alice", actualRegistration.getFirstName());
        verify(registrationRepository, times(1)).save(registrationToSave);
    }

    @Test
    @DisplayName("Should calculate tournament counts correctly")
    void shouldCalculateTournamentCountsCorrectly() {
        // Given
        List<TournamentRegistration> registrations = Arrays.asList(registration1, registration2, registration3);
        when(registrationRepository.findAll()).thenReturn(registrations);

        // When
        Map<String, Long> tournamentCounts = registrationService.getTournamentCounts();

        // Then
        assertEquals(2, tournamentCounts.size());
        assertEquals(2L, tournamentCounts.get("Summer Championship"));
        assertEquals(1L, tournamentCounts.get("Winter Cup"));
        verify(registrationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty map when no registrations for tournament counts")
    void shouldReturnEmptyMapWhenNoRegistrationsForCounts() {
        // Given
        when(registrationRepository.findAll()).thenReturn(Arrays.asList());

        // When
        Map<String, Long> tournamentCounts = registrationService.getTournamentCounts();

        // Then
        assertTrue(tournamentCounts.isEmpty());
        verify(registrationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle single tournament with multiple registrations")
    void shouldHandleSingleTournamentWithMultipleRegistrations() {
        // Given
        List<TournamentRegistration> registrations = Arrays.asList(registration1, registration2);
        when(registrationRepository.findAll()).thenReturn(registrations);

        // When
        Map<String, Long> tournamentCounts = registrationService.getTournamentCounts();

        // Then
        assertEquals(1, tournamentCounts.size());
        assertEquals(2L, tournamentCounts.get("Summer Championship"));
        verify(registrationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle multiple tournaments with single registration each")
    void shouldHandleMultipleTournamentsWithSingleRegistration() {
        // Given
        List<TournamentRegistration> registrations = Arrays.asList(registration1, registration3);
        when(registrationRepository.findAll()).thenReturn(registrations);

        // When
        Map<String, Long> tournamentCounts = registrationService.getTournamentCounts();

        // Then
        assertEquals(2, tournamentCounts.size());
        assertEquals(1L, tournamentCounts.get("Summer Championship"));
        assertEquals(1L, tournamentCounts.get("Winter Cup"));
        verify(registrationRepository, times(1)).findAll();
    }
}
