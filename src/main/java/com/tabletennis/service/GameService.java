package com.tabletennis.service;

import com.tabletennis.entity.Game;
import com.tabletennis.entity.Player;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing tournament games
 */
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final RegistrationService registrationService;

    public GameService(GameRepository gameRepository, RegistrationService registrationService) {
        this.gameRepository = gameRepository;
        this.registrationService = registrationService;
    }

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
        List<TournamentRegistration> registrations = registrationService.findByTournament(tournament);

        if (registrations.size() < 2) {
            throw new IllegalStateException("Need at least 2 players to start tournament");
        }

        // Create round-robin games
        List<Game> games = new ArrayList<>();
        int gameOrder = 1;

        // Generate all possible combinations of players
        for (int i = 0; i < registrations.size(); i++) {
            for (int j = i + 1; j < registrations.size(); j++) {
                Player player1 = registrations.get(i).getPlayer();
                Player player2 = registrations.get(j).getPlayer();

                Game game = new Game(tournament, player1, player2, gameOrder++);

                games.add(game);
            }
        }

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
     * Get all games for a tournament by ID
     */
    public List<Game> getGamesForTournament(Long tournamentId) {
        return gameRepository.findByTournamentIdOrderByGameOrderAsc(tournamentId);
    }

    /**
     * Check if tournament has been started (has games)
     */
    public boolean isTournamentStarted(Tournament tournament) {
        return gameRepository.existsByTournament(tournament);
    }

    /**
     * Get total number of games for a tournament
     */
    public long getGameCount(Tournament tournament) {
        return gameRepository.countByTournament(tournament);
    }

    /**
     * Update game score
     */
    public Game updateGameScore(Long gameId, int player1Score, int player2Score) {
        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        game.setPlayer1Score(player1Score);
        game.setPlayer2Score(player2Score);
        game.setStatus(Game.GameStatus.COMPLETED);

        return gameRepository.save(game);
    }
}
