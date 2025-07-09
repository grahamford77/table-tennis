package com.tabletennis.repository;

import com.tabletennis.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @Test
    void findByTournamentOrderByGameOrderAsc_ShouldReturnGamesOrderedByGameOrder() {
        // Given
        var tournament = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament);

        var game1 = TestDataFactory.createGameWithTournament(tournament);
        game1.setGameOrder(3);
        // Persist players first before persisting game
        entityManager.persistAndFlush(game1.getPlayer1());
        entityManager.persistAndFlush(game1.getPlayer2());

        var game2 = TestDataFactory.createGameWithTournament(tournament);
        game2.setGameOrder(1);
        entityManager.persistAndFlush(game2.getPlayer1());
        entityManager.persistAndFlush(game2.getPlayer2());

        var game3 = TestDataFactory.createGameWithTournament(tournament);
        game3.setGameOrder(2);
        entityManager.persistAndFlush(game3.getPlayer1());
        entityManager.persistAndFlush(game3.getPlayer2());

        entityManager.persistAndFlush(game1);
        entityManager.persistAndFlush(game2);
        entityManager.persistAndFlush(game3);

        // When
        var result = gameRepository.findByTournamentOrderByGameOrderAsc(tournament);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());

        // Verify ordering: game2 (order 1), game3 (order 2), game1 (order 3)
        assertEquals(game2.getId(), result.get(0).getId());
        assertEquals(game3.getId(), result.get(1).getId());
        assertEquals(game1.getId(), result.get(2).getId());

        assertEquals(1, result.get(0).getGameOrder());
        assertEquals(2, result.get(1).getGameOrder());
        assertEquals(3, result.get(2).getGameOrder());
    }

    @Test
    void findByTournament_WithNoGames_ShouldReturnEmptyList() {
        // Given
        var tournament1 = TestDataFactory.createTournament();
        var tournament2 = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament1);
        entityManager.persistAndFlush(tournament2);

        var game = TestDataFactory.createGameWithTournament(tournament1);
        entityManager.persistAndFlush(game.getPlayer1());
        entityManager.persistAndFlush(game.getPlayer2());
        entityManager.persistAndFlush(game);

        // When
        var result = gameRepository.findByTournamentOrderByGameOrderAsc(tournament2);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByTournament_WithMultipleTournaments_ShouldReturnOnlyMatchingGames() {
        // Given
        var tournament1 = TestDataFactory.createTournament();
        var tournament2 = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament1);
        entityManager.persistAndFlush(tournament2);

        // Create games for tournament1
        var game1 = TestDataFactory.createGameWithTournament(tournament1);
        entityManager.persistAndFlush(game1.getPlayer1());
        entityManager.persistAndFlush(game1.getPlayer2());
        entityManager.persistAndFlush(game1);

        var game2 = TestDataFactory.createGameWithTournament(tournament1);
        entityManager.persistAndFlush(game2.getPlayer1());
        entityManager.persistAndFlush(game2.getPlayer2());
        entityManager.persistAndFlush(game2);

        // Create game for tournament2
        var game3 = TestDataFactory.createGameWithTournament(tournament2);
        entityManager.persistAndFlush(game3.getPlayer1());
        entityManager.persistAndFlush(game3.getPlayer2());
        entityManager.persistAndFlush(game3);

        // When
        var result = gameRepository.findByTournamentOrderByGameOrderAsc(tournament1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(game -> game.getTournament().equals(tournament1)));
    }

    @Test
    void existsByTournament_WhenGamesExist_ShouldReturnTrue() {
        // Given
        var tournament = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament);

        var game = TestDataFactory.createGameWithTournament(tournament);
        // Persist players first before persisting game
        entityManager.persistAndFlush(game.getPlayer1());
        entityManager.persistAndFlush(game.getPlayer2());
        entityManager.persistAndFlush(game);

        // When
        var result = gameRepository.existsByTournament(tournament);

        // Then
        assertTrue(result);
    }

    @Test
    void existsByTournament_WhenNoGamesExist_ShouldReturnFalse() {
        // Given
        var tournament = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament);

        // When
        var result = gameRepository.existsByTournament(tournament);

        // Then
        assertFalse(result);
    }

    @Test
    void existsByTournament_WithDifferentTournament_ShouldReturnFalse() {
        // Given
        var tournament1 = TestDataFactory.createTournament();
        var tournament2 = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament1);
        entityManager.persistAndFlush(tournament2);

        var game = TestDataFactory.createGameWithTournament(tournament1);
        // Persist players first before persisting game
        entityManager.persistAndFlush(game.getPlayer1());
        entityManager.persistAndFlush(game.getPlayer2());
        entityManager.persistAndFlush(game);

        // When
        var result = gameRepository.existsByTournament(tournament2);

        // Then
        assertFalse(result);
    }
}
