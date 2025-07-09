package com.tabletennis.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.tabletennis.TestDataFactory;
import com.tabletennis.dto.RegistrationDto;
import com.tabletennis.dto.TournamentDto;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.mapping.RegistrationMapper;
import com.tabletennis.mapping.TournamentMapper;
import com.tabletennis.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private GameService gameService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private TournamentMapper tournamentMapper;

    @Mock
    private RegistrationMapper registrationMapper;

    private TournamentService tournamentService;

    private Tournament tournament;
    private TournamentDto tournamentDto;
    private List<TournamentRegistration> registrations;

    @BeforeEach
    void setUp() {
        tournamentService = new TournamentService(
                tournamentRepository, gameService, registrationService, tournamentMapper, registrationMapper);

        // Create test data using TestDataFactory
        tournament = TestDataFactory.createTournament();
        tournamentDto = TestDataFactory.createTournamentDtoFromTournament(tournament);
        registrations = TestDataFactory.createTournamentRegistrationsForTournament(tournament, 3);
    }

    @Test
    void findAllOrderByDate_ShouldReturnTournamentDtos() {
        // Given
        var tournaments = List.of(tournament);

        when(tournamentRepository.findAllByOrderByDateAsc()).thenReturn(tournaments);
        when(registrationService.findByTournament(tournament)).thenReturn(registrations);
        when(registrationMapper.convertToDto(any())).thenReturn(new RegistrationDto());
        when(gameService.isTournamentStarted(tournament)).thenReturn(false);
        when(tournamentMapper.convertToDto(eq(tournament), any(), anyBoolean())).thenReturn(tournamentDto);

        // When
        var result = tournamentService.findAllOrderByDate();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tournamentRepository).findAllByOrderByDateAsc();
        verify(registrationService).findByTournament(tournament);
        verify(gameService).isTournamentStarted(tournament);
        verify(tournamentMapper).convertToDto(eq(tournament), any(), eq(false));
    }

    @Test
    void findByIdDto_WhenTournamentExists_ShouldReturnTournamentDto() {
        // Given
        var tournamentId = TestDataFactory.randomId();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(registrationService.findByTournament(tournament)).thenReturn(registrations);
        when(registrationMapper.convertToDto(any())).thenReturn(new RegistrationDto());
        when(gameService.isTournamentStarted(tournament)).thenReturn(false);
        when(tournamentMapper.convertToDto(eq(tournament), any(), anyBoolean())).thenReturn(tournamentDto);

        // When
        var result = tournamentService.findByIdDto(tournamentId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(tournamentDto, result.get());
        verify(tournamentRepository).findById(tournamentId);
    }

    @Test
    void findByIdDto_WhenTournamentDoesNotExist_ShouldReturnEmpty() {
        // Given
        var tournamentId = TestDataFactory.randomId();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // When
        var result = tournamentService.findByIdDto(tournamentId);

        // Then
        assertFalse(result.isPresent());
        verify(tournamentRepository).findById(tournamentId);
    }

    @Test
    void findById_WhenTournamentExists_ShouldReturnTournament() {
        // Given
        var tournamentId = TestDataFactory.randomId();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // When
        var result = tournamentService.findById(tournamentId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(tournament, result.get());
        verify(tournamentRepository).findById(tournamentId);
    }

    @Test
    void deleteById_ShouldDeleteTournament() {
        // Given
        var tournamentId = TestDataFactory.randomId();

        // When
        tournamentService.deleteById(tournamentId);

        // Then
        verify(tournamentRepository).deleteById(tournamentId);
    }

    @Test
    void createTournament_ShouldCreateAndReturnTournamentDto() {
        // Given
        var tournamentRequest = TestDataFactory.createTournamentRequest();
        var newTournament = TestDataFactory.createTournament();
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(newTournament);
        when(tournamentMapper.convertToDto(newTournament)).thenReturn(tournamentDto);

        // When
        var result = tournamentService.createTournament(tournamentRequest);

        // Then
        assertNotNull(result);
        assertEquals(tournamentDto, result);
        verify(tournamentRepository).save(any(Tournament.class));
        verify(tournamentMapper).convertToDto(newTournament);
    }

    @Test
    void updateTournament_ShouldUpdateAndReturnTournamentDto() {
        // Given
        var tournamentId = TestDataFactory.randomId();
        var tournamentRequest = TestDataFactory.createTournamentRequest();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
        when(tournamentMapper.convertToDto(tournament)).thenReturn(tournamentDto);

        // When
        var result = tournamentService.updateTournament(tournamentId, tournamentRequest);

        // Then
        assertNotNull(result);
        assertEquals(tournamentDto, result);
        verify(tournamentRepository).findById(tournamentId);
        verify(tournamentRepository).save(tournament);
        verify(tournamentMapper).convertToDto(tournament);
    }

    @Test
    void countActiveTournaments_ShouldReturnCountOfTournamentsWithinNextTwoWeeks() {
        // Given
        var activeTournament1 = TestDataFactory.createTournament();
        activeTournament1.setDate(LocalDate.now().plusDays(5)); // Within 2 weeks

        var activeTournament2 = TestDataFactory.createTournament();
        activeTournament2.setDate(LocalDate.now().plusWeeks(1)); // Within 2 weeks

        var inactiveTournament = TestDataFactory.createTournament();
        inactiveTournament.setDate(LocalDate.now().plusWeeks(3)); // Beyond 2 weeks

        var allTournaments = List.of(activeTournament1, activeTournament2, inactiveTournament);
        when(tournamentRepository.findAll()).thenReturn(allTournaments);

        // When
        var result = tournamentService.countActiveTournaments();

        // Then
        assertEquals(2L, result);
        verify(tournamentRepository).findAll();
    }

    @Test
    void findAvailableForRegistration_ShouldReturnTournamentsNotStartedAndNotFull() {
        // Given
        var availableTournament = TestDataFactory.createTournament();
        availableTournament.setMaxEntrants(10);

        var fullTournament = TestDataFactory.createTournament();
        fullTournament.setMaxEntrants(2);

        var startedTournament = TestDataFactory.createTournament();

        var allTournaments = List.of(availableTournament, fullTournament, startedTournament);
        when(tournamentRepository.findAllByOrderByDateAsc()).thenReturn(allTournaments);

        // Available tournament: not started, not full
        when(gameService.isTournamentStarted(availableTournament)).thenReturn(false);
        when(registrationService.findByTournament(availableTournament)).thenReturn(List.of());

        // Full tournament: not started but full
        when(gameService.isTournamentStarted(fullTournament)).thenReturn(false);
        when(registrationService.findByTournament(fullTournament)).thenReturn(
                TestDataFactory.createTournamentRegistrationsForTournament(fullTournament, 2)
        );

        // Started tournament: already started
        when(gameService.isTournamentStarted(startedTournament)).thenReturn(true);

        var availableTournamentDto = TestDataFactory.createTournamentDtoFromTournament(availableTournament);
        when(tournamentMapper.convertToDto(availableTournament)).thenReturn(availableTournamentDto);

        // When
        var result = tournamentService.findAvailableForRegistration();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(availableTournamentDto, result.getFirst());
        verify(tournamentRepository).findAllByOrderByDateAsc();
        verify(gameService).isTournamentStarted(availableTournament);
        verify(gameService).isTournamentStarted(fullTournament);
        verify(gameService).isTournamentStarted(startedTournament);
        verify(registrationService).findByTournament(availableTournament);
        verify(registrationService).findByTournament(fullTournament);
        verify(tournamentMapper).convertToDto(availableTournament);
    }
}


