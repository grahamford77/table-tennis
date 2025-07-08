package com.tabletennis.service;

import com.tabletennis.dto.GameDto;
import com.tabletennis.dto.TournamentDto;
import com.tabletennis.entity.Game;
import com.tabletennis.entity.Tournament;
import com.tabletennis.mapping.GameMapper;
import com.tabletennis.mapping.TournamentMapper;
import com.tabletennis.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Service for managing tournament games
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final RegistrationService registrationService;
    private final GameMapper gameMapper;
    private final TournamentMapper tournamentMapper;

    /**
     * Create round-robin games for a tournament
     * Each player plays against every other player exactly once
     */
    public List<GameDto> startTournament(Tournament tournament) {
        // Check if tournament already has games
        if (gameRepository.existsByTournament(tournament)) {
            throw new IllegalStateException("Tournament has already been started");
        }

        // Get all registered players for the tournament
        var registrations = registrationService.findByTournament(tournament);

        if (registrations.size() < 2) {
            throw new IllegalStateException("Need at least 2 players to start tournament");
        }

        // Create round-robin games using Stream API
        var gameOrderCounter = new AtomicInteger(1);

        var games = IntStream.range(0, registrations.size())
                .boxed()
                .flatMap(i -> IntStream.range(i + 1, registrations.size())
                        .mapToObj(j -> {
                            var player1 = registrations.get(i).getPlayer();
                            var player2 = registrations.get(j).getPlayer();
                            return new Game(tournament, player1, player2, gameOrderCounter.getAndIncrement());
                        }))
                .toList();

        // Save all games
        var savedGames = gameRepository.saveAll(games);
        return savedGames.stream()
                .map(gameMapper::convertToDto)
                .toList();
    }

    /**
     * Get all games for a tournament in order as DTOs
     */
    public List<GameDto> getGamesForTournamentDto(Tournament tournament) {
        return gameRepository.findByTournamentOrderByGameOrderAsc(tournament).stream()
            .map(gameMapper::convertToDto)
            .toList();
    }

    /**
     * Check if tournament has been started (has games) - takes TournamentDto
     */
    public boolean isTournamentStarted(TournamentDto tournamentDto) {
        var tournament = tournamentMapper.convertToEntity(tournamentDto);
        return gameRepository.existsByTournament(tournament);
    }

    /**
     * Check if tournament has been started (has games) - for internal use with entities
     */
    public boolean isTournamentStarted(Tournament tournament) {
        return gameRepository.existsByTournament(tournament);
    }

    /**
     * Update game score and return as DTO
     */
    public GameDto updateGameScore(Long gameId, int player1Score, int player2Score) {
        var game = gameRepository.findById(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        game.setPlayer1Score(player1Score);
        game.setPlayer2Score(player2Score);
        game.setStatus(Game.GameStatus.COMPLETED);
        game.setPlayedAt(java.time.LocalDateTime.now());

        var savedGame = gameRepository.save(game);
        return gameMapper.convertToDto(savedGame);
    }
}
