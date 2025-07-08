package com.tabletennis.repository;

import com.tabletennis.TestDataFactory;
import com.tabletennis.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void findByEmail_WhenPlayerExists_ShouldReturnPlayer() {
        // Given
        Player player = TestDataFactory.createPlayer();
        String email = player.getEmail();
        entityManager.persistAndFlush(player);

        // When
        Optional<Player> result = playerRepository.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(player.getId(), result.get().getId());
        assertEquals(email, result.get().getEmail());
        assertEquals(player.getFirstName(), result.get().getFirstName());
        assertEquals(player.getSurname(), result.get().getSurname());
    }

    @Test
    void findByEmail_WhenPlayerDoesNotExist_ShouldReturnEmpty() {
        // Given
        String nonExistentEmail = TestDataFactory.randomEmail();

        // When
        Optional<Player> result = playerRepository.findByEmail(nonExistentEmail);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_WhenMultiplePlayersExist_ShouldReturnCorrectPlayer() {
        // Given
        Player player1 = TestDataFactory.createPlayer();
        Player player2 = TestDataFactory.createPlayer();
        entityManager.persistAndFlush(player1);
        entityManager.persistAndFlush(player2);

        // When
        Optional<Player> result = playerRepository.findByEmail(player1.getEmail());

        // Then
        assertTrue(result.isPresent());
        assertEquals(player1.getId(), result.get().getId());
        assertEquals(player1.getEmail(), result.get().getEmail());
    }

    @Test
    void findByEmail_WithCaseVariation_ShouldBeExact() {
        // Given
        Player player = TestDataFactory.createPlayer();
        player.setEmail("test@example.com");
        entityManager.persistAndFlush(player);

        // When
        Optional<Player> resultUpperCase = playerRepository.findByEmail("TEST@EXAMPLE.COM");
        Optional<Player> resultLowerCase = playerRepository.findByEmail("test@example.com");

        // Then
        assertFalse(resultUpperCase.isPresent()); // Email search should be exact
        assertTrue(resultLowerCase.isPresent());
    }
}
