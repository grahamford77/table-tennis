package com.tabletennis.service;

import com.tabletennis.TestDataFactory;
import com.tabletennis.entity.Player;
import com.tabletennis.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    private PlayerService playerService;
    private Player player;

    @BeforeEach
    void setUp() {
        playerService = new PlayerService(playerRepository);

        // Create test data using TestDataFactory
        player = TestDataFactory.createPlayer();
    }

    @Test
    void save_ShouldSaveAndReturnPlayer() {
        // Given
        when(playerRepository.save(player)).thenReturn(player);

        // When
        Player result = playerService.save(player);

        // Then
        assertEquals(player, result);
        verify(playerRepository).save(player);
    }

    @Test
    void findByEmail_WhenPlayerExists_ShouldReturnPlayer() {
        // Given
        String email = player.getEmail();
        when(playerRepository.findByEmail(email)).thenReturn(Optional.of(player));

        // When
        Optional<Player> result = playerService.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(player, result.get());
        verify(playerRepository).findByEmail(email);
    }

    @Test
    void findByEmail_WhenPlayerDoesNotExist_ShouldReturnEmpty() {
        // Given
        String email = TestDataFactory.randomEmail();
        when(playerRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<Player> result = playerService.findByEmail(email);

        // Then
        assertFalse(result.isPresent());
        verify(playerRepository).findByEmail(email);
    }

    @Test
    void findOrCreatePlayer_WhenPlayerExists_ShouldReturnExistingPlayer() {
        // Given
        String firstName = player.getFirstName();
        String surname = player.getSurname();
        String email = player.getEmail();

        when(playerRepository.findByEmail(email)).thenReturn(Optional.of(player));

        // When
        Player result = playerService.findOrCreatePlayer(firstName, surname, email);

        // Then
        assertEquals(player, result);
        verify(playerRepository).findByEmail(email);
    }

    @Test
    void findOrCreatePlayer_WhenPlayerDoesNotExist_ShouldCreateNewPlayer() {
        // Given
        String firstName = TestDataFactory.randomName();
        String surname = TestDataFactory.randomSurname();
        String email = TestDataFactory.randomEmail();

        Player newPlayer = TestDataFactory.createPlayer();
        newPlayer.setFirstName(firstName);
        newPlayer.setSurname(surname);
        newPlayer.setEmail(email);

        when(playerRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(playerRepository.save(any(Player.class))).thenReturn(newPlayer);

        // When
        Player result = playerService.findOrCreatePlayer(firstName, surname, email);

        // Then
        assertNotNull(result);
        assertEquals(firstName, result.getFirstName());
        assertEquals(surname, result.getSurname());
        assertEquals(email, result.getEmail());
        verify(playerRepository).findByEmail(email);
        verify(playerRepository).save(any(Player.class));
    }
}
