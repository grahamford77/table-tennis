package com.tabletennis.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.tabletennis.entity.Game;
import com.tabletennis.entity.Tournament;
import com.tabletennis.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for managing tournament games
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final RegistrationService registrationService;

    /**
     * Create round-robin games for a tournament
     * Each player plays against every other player exactly once
     */
    public List<Game> startTournament(Tournament tournament) {
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
        return gameRepository.saveAll(games);
    }

    /**
     * Get all games for a tournament in order
     */
    public List<Game> getGamesForTournament(Tournament tournament) {
        return gameRepository.findByTournamentOrderByGameOrderAsc(tournament);
    }

    /**
     * Check if tournament has been started (has games)
     */
    public boolean isTournamentStarted(Tournament tournament) {
        return gameRepository.existsByTournament(tournament);
    }

    /**
     * Update game score
     */
    public Game updateGameScore(Long gameId, int player1Score, int player2Score) {
        var game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        game.setPlayer1Score(player1Score);
        game.setPlayer2Score(player2Score);
        game.setStatus(Game.GameStatus.COMPLETED);
        game.setPlayedAt(java.time.LocalDateTime.now());

        return gameRepository.save(game);
    }
}
