package com.tabletennis.repository;

import java.util.List;

import com.tabletennis.TestDataFactory;
import com.tabletennis.entity.Game;
import com.tabletennis.entity.Tournament;
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
        Tournament tournament = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament);

        Game game1 = TestDataFactory.createGameWithTournament(tournament);
        game1.setGameOrder(3);
        // Persist players first before persisting game
        entityManager.persistAndFlush(game1.getPlayer1());
        entityManager.persistAndFlush(game1.getPlayer2());

        Game game2 = TestDataFactory.createGameWithTournament(tournament);
        game2.setGameOrder(1);
        entityManager.persistAndFlush(game2.getPlayer1());
        entityManager.persistAndFlush(game2.getPlayer2());

        Game game3 = TestDataFactory.createGameWithTournament(tournament);
        game3.setGameOrder(2);
        entityManager.persistAndFlush(game3.getPlayer1());
        entityManager.persistAndFlush(game3.getPlayer2());

        entityManager.persistAndFlush(game1);
        entityManager.persistAndFlush(game2);
        entityManager.persistAndFlush(game3);

        // When
        List<Game> result = gameRepository.findByTournamentOrderByGameOrderAsc(tournament);

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
    void findByTournamentOrderByGameOrderAsc_WithDifferentTournament_ShouldReturnEmptyList() {
        // Given
        Tournament tournament1 = TestDataFactory.createTournament();
        Tournament tournament2 = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament1);
        entityManager.persistAndFlush(tournament2);

        Game game = TestDataFactory.createGameWithTournament(tournament1);
        // Persist players first before persisting game
        entityManager.persistAndFlush(game.getPlayer1());
        entityManager.persistAndFlush(game.getPlayer2());
        entityManager.persistAndFlush(game);

        // When
        List<Game> result = gameRepository.findByTournamentOrderByGameOrderAsc(tournament2);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void existsByTournament_WhenGamesExist_ShouldReturnTrue() {
        // Given
        Tournament tournament = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament);

        Game game = TestDataFactory.createGameWithTournament(tournament);
        // Persist players first before persisting game
        entityManager.persistAndFlush(game.getPlayer1());
        entityManager.persistAndFlush(game.getPlayer2());
        entityManager.persistAndFlush(game);

        // When
        boolean result = gameRepository.existsByTournament(tournament);

        // Then
        assertTrue(result);
    }

    @Test
    void existsByTournament_WhenNoGamesExist_ShouldReturnFalse() {
        // Given
        Tournament tournament = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament);

        // When
        boolean result = gameRepository.existsByTournament(tournament);

        // Then
        assertFalse(result);
    }

    @Test
    void existsByTournament_WithDifferentTournament_ShouldReturnFalse() {
        // Given
        Tournament tournament1 = TestDataFactory.createTournament();
        Tournament tournament2 = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament1);
        entityManager.persistAndFlush(tournament2);

        Game game = TestDataFactory.createGameWithTournament(tournament1);
        // Persist players first before persisting game
        entityManager.persistAndFlush(game.getPlayer1());
        entityManager.persistAndFlush(game.getPlayer2());
        entityManager.persistAndFlush(game);

        // When
        boolean result = gameRepository.existsByTournament(tournament2);

        // Then
        assertFalse(result);
    }
}
