package com.tabletennis.service;

import com.tabletennis.entity.Tournament;
import com.tabletennis.repository.TournamentRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TournamentServiceImpl.
 * Tests all service methods using mocked repository.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tournament Service Tests")
class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    private Tournament tournament1;
    private Tournament tournament2;

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
    }

    @Test
    @DisplayName("Should find all tournaments ordered by date")
    void shouldFindAllTournamentsOrderedByDate() {
        // Given
        List<Tournament> expectedTournaments = Arrays.asList(tournament1, tournament2);
        when(tournamentRepository.findAllByOrderByDateAsc()).thenReturn(expectedTournaments);

        // When
        List<Tournament> actualTournaments = tournamentService.findAllOrderByDate();

        // Then
        assertEquals(expectedTournaments, actualTournaments);
        assertEquals(2, actualTournaments.size());
        verify(tournamentRepository, times(1)).findAllByOrderByDateAsc();
    }

    @Test
    @DisplayName("Should return empty list when no tournaments exist")
    void shouldReturnEmptyListWhenNoTournaments() {
        // Given
        when(tournamentRepository.findAllByOrderByDateAsc()).thenReturn(List.of());

        // When
        List<Tournament> actualTournaments = tournamentService.findAllOrderByDate();

        // Then
        assertTrue(actualTournaments.isEmpty());
        verify(tournamentRepository, times(1)).findAllByOrderByDateAsc();
    }

    @Test
    @DisplayName("Should find tournament by ID when exists")
    void shouldFindTournamentByIdWhenExists() {
        // Given
        Long tournamentId = 1L;
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament1));

        // When
        Optional<Tournament> actualTournament = tournamentService.findById(tournamentId);

        // Then
        assertTrue(actualTournament.isPresent());
        assertEquals(tournament1, actualTournament.get());
        assertEquals("Summer Championship", actualTournament.get().getName());
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    @DisplayName("Should return empty optional when tournament not found by ID")
    void shouldReturnEmptyOptionalWhenTournamentNotFound() {
        // Given
        Long nonExistentId = 999L;
        when(tournamentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<Tournament> actualTournament = tournamentService.findById(nonExistentId);

        // Then
        assertTrue(actualTournament.isEmpty());
        verify(tournamentRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should save tournament successfully")
    void shouldSaveTournamentSuccessfully() {
        // Given
        Tournament tournamentToSave = new Tournament("New Tournament", "Description",
                LocalDate.of(2025, 9, 1), LocalTime.of(16, 0),
                "New Location", 24);

        Tournament savedTournament = new Tournament("New Tournament", "Description",
                LocalDate.of(2025, 9, 1), LocalTime.of(16, 0),
                "New Location", 24);
        savedTournament.setId(3L);

        when(tournamentRepository.save(tournamentToSave)).thenReturn(savedTournament);

        // When
        Tournament actualTournament = tournamentService.save(tournamentToSave);

        // Then
        assertEquals(savedTournament, actualTournament);
        assertEquals(3L, actualTournament.getId());
        assertEquals("New Tournament", actualTournament.getName());
        verify(tournamentRepository, times(1)).save(tournamentToSave);
    }

    @Test
    @DisplayName("Should delete tournament by ID")
    void shouldDeleteTournamentById() {
        // Given
        Long tournamentId = 1L;
        doNothing().when(tournamentRepository).deleteById(tournamentId);

        // When
        tournamentService.deleteById(tournamentId);

        // Then
        verify(tournamentRepository, times(1)).deleteById(tournamentId);
    }

    @Test
    @DisplayName("Should handle invalid tournament ID in delete method")
    void shouldHandleInvalidTournamentIdInDelete() {
        // Given
        Long invalidId = 999L;
        doNothing().when(tournamentRepository).deleteById(invalidId);

        // When
        tournamentService.deleteById(invalidId);

        // Then
        verify(tournamentRepository, times(1)).deleteById(invalidId);
    }
}
