package com.tabletennis.service;

import java.util.List;
import java.util.Optional;

import com.tabletennis.TestDataFactory;
import com.tabletennis.dto.GameDto;
import com.tabletennis.entity.Game;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.mapping.GameMapper;
import com.tabletennis.mapping.TournamentMapper;
import com.tabletennis.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private GameMapper gameMapper;

    @Mock
    private TournamentMapper tournamentMapper;

    @Captor
    private ArgumentCaptor<List<Game>> gamesCaptor;

    private GameService gameService;

    private Tournament tournament;
    private List<TournamentRegistration> registrations;

    @BeforeEach
    void setUp() {
        gameService = new GameService(gameRepository, registrationService, gameMapper, tournamentMapper);

        // Create test data using TestDataFactory
        tournament = TestDataFactory.createTournament();
        registrations = TestDataFactory.createTournamentRegistrationsForTournament(tournament, 4);
    }

    @Test
    void startTournament_WithValidTournament_ShouldCreateGames() {
        // Given
        when(gameRepository.existsByTournament(tournament)).thenReturn(false);
        when(registrationService.findByTournament(tournament)).thenReturn(registrations);
        when(gameRepository.saveAll(anyList())).thenReturn(List.of(new Game(), new Game()));
        when(gameMapper.convertToDto(any(Game.class))).thenReturn(new GameDto());

        // When
        List<GameDto> result = gameService.startTournament(tournament);

        // Then
        assertNotNull(result);
        verify(gameRepository).existsByTournament(tournament);
        verify(registrationService).findByTournament(tournament);

        // Verify saveAll was called with a list of 6 games
        verify(gameRepository).saveAll(gamesCaptor.capture());
        List<Game> savedGames = gamesCaptor.getValue();
        assertEquals(6, savedGames.size());
    }

    @Test
    void startTournament_WhenTournamentAlreadyStarted_ShouldThrowException() {
        // Given
        when(gameRepository.existsByTournament(tournament)).thenReturn(true);

        // When & Then
        assertThrows(IllegalStateException.class, () -> gameService.startTournament(tournament));
        verify(gameRepository).existsByTournament(tournament);
        verify(registrationService, never()).findByTournament(tournament);
        verify(gameRepository, never()).saveAll(any());
    }

    @Test
    void startTournament_WithInsufficientPlayers_ShouldThrowException() {
        // Given
        List<TournamentRegistration> singleRegistration = List.of(registrations.getFirst());
        when(gameRepository.existsByTournament(tournament)).thenReturn(false);
        when(registrationService.findByTournament(tournament)).thenReturn(singleRegistration);

        // When & Then
        assertThrows(IllegalStateException.class, () -> gameService.startTournament(tournament));
        verify(gameRepository).existsByTournament(tournament);
        verify(registrationService).findByTournament(tournament);
        verify(gameRepository, never()).saveAll(any());
    }

    @Test
    void isTournamentStarted_WhenGamesExist_ShouldReturnTrue() {
        // Given
        when(gameRepository.existsByTournament(tournament)).thenReturn(true);

        // When
        boolean result = gameService.isTournamentStarted(tournament);

        // Then
        assertTrue(result);
        verify(gameRepository).existsByTournament(tournament);
    }

    @Test
    void isTournamentStarted_WhenNoGames_ShouldReturnFalse() {
        // Given
        when(gameRepository.existsByTournament(tournament)).thenReturn(false);

        // When
        boolean result = gameService.isTournamentStarted(tournament);

        // Then
        assertFalse(result);
        verify(gameRepository).existsByTournament(tournament);
    }

    @Test
    void isTournamentStarted_WithTournamentDto_WhenGamesExist_ShouldReturnTrue() {
        // Given
        var tournamentDto = TestDataFactory.createTournamentDtoFromTournament(tournament);
        when(tournamentMapper.convertToEntity(tournamentDto)).thenReturn(tournament);
        when(gameRepository.existsByTournament(tournament)).thenReturn(true);

        // When
        boolean result = gameService.isTournamentStarted(tournamentDto);

        // Then
        assertTrue(result);
        verify(tournamentMapper).convertToEntity(tournamentDto);
        verify(gameRepository).existsByTournament(tournament);
    }

    @Test
    void isTournamentStarted_WithTournamentDto_WhenNoGames_ShouldReturnFalse() {
        // Given
        var tournamentDto = TestDataFactory.createTournamentDtoFromTournament(tournament);
        when(tournamentMapper.convertToEntity(tournamentDto)).thenReturn(tournament);
        when(gameRepository.existsByTournament(tournament)).thenReturn(false);

        // When
        boolean result = gameService.isTournamentStarted(tournamentDto);

        // Then
        assertFalse(result);
        verify(tournamentMapper).convertToEntity(tournamentDto);
        verify(gameRepository).existsByTournament(tournament);
    }

    @Test
    void getGamesForTournamentDto_ShouldReturnGamesInOrder() {
        // Given
        List<Game> games = TestDataFactory.createGamesForTournament(tournament, 3);
        List<GameDto> gameDtos = List.of(
                TestDataFactory.createGameDto(),
                TestDataFactory.createGameDto(),
                TestDataFactory.createGameDto()
        );

        when(gameRepository.findByTournamentOrderByGameOrderAsc(tournament)).thenReturn(games);
        when(gameMapper.convertToDto(any(Game.class))).thenReturn(gameDtos.getFirst(), gameDtos.get(1), gameDtos.get(2));

        // When
        List<GameDto> result = gameService.getGamesForTournamentDto(tournament);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(gameRepository).findByTournamentOrderByGameOrderAsc(tournament);
        verify(gameMapper, times(3)).convertToDto(any(Game.class));
    }

    @Test
    void updateGameScore_ShouldUpdateScoreAndReturnDto() {
        // Given
        Long gameId = TestDataFactory.randomId();
        int player1Score = TestDataFactory.randomScore();
        int player2Score = TestDataFactory.randomScore();

        Game game = TestDataFactory.createGame();
        GameDto gameDto = TestDataFactory.createGameDto();

        when(gameRepository.findById(gameId)).thenReturn(java.util.Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenReturn(game);
        when(gameMapper.convertToDto(game)).thenReturn(gameDto);

        // When
        GameDto result = gameService.updateGameScore(gameId, player1Score, player2Score);

        // Then
        assertNotNull(result);
        assertEquals(gameDto, result);
        assertEquals(player1Score, game.getPlayer1Score());
        assertEquals(player2Score, game.getPlayer2Score());
        assertEquals(Game.GameStatus.COMPLETED, game.getStatus());
        assertNotNull(game.getPlayedAt());
        verify(gameRepository).findById(gameId);
        verify(gameRepository).save(game);
        verify(gameMapper).convertToDto(game);
    }

    @Test
    void updateGameScore_WithNonExistentGame_ShouldThrowException() {
        // Given
        Long gameId = TestDataFactory.randomId();
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        // When & Then
        int player1Score = TestDataFactory.randomScore();
        int player2Score = TestDataFactory.randomScore();

        assertThrows(IllegalArgumentException.class, () -> gameService.updateGameScore(gameId, player1Score, player2Score));
        verify(gameRepository).findById(gameId);
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameMapper, never()).convertToDto(any(Game.class));
    }
}
