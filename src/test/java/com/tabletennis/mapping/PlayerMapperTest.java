package com.tabletennis.mapping;

import com.tabletennis.TestDataFactory;
import com.tabletennis.dto.PlayerDto;
import com.tabletennis.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PlayerMapperTest {

    private PlayerMapper playerMapper;
    private Player player;

    @BeforeEach
    void setUp() {
        playerMapper = new PlayerMapper();
        player = TestDataFactory.createPlayer();
    }

    @Test
    void convertToDto_ShouldMapPlayerToDto() {
        // When
        PlayerDto result = playerMapper.convertToDto(player);

        // Then
        assertNotNull(result);
        assertEquals(player.getId(), result.getId());
        assertEquals(player.getFirstName(), result.getFirstName());
        assertEquals(player.getSurname(), result.getSurname());
        assertEquals(player.getEmail(), result.getEmail());
        assertEquals(player.getFullName(), result.getFullName());
    }

    @Test
    void convertToDto_WithNullPlayer_ShouldThrowNullPointerException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> playerMapper.convertToDto(null));
    }

    @Test
    void convertToDto_WithPartialPlayerData_ShouldMapAvailableFields() {
        // Given
        Player partialPlayer = TestDataFactory.createPlayer();

        // When
        PlayerDto result = playerMapper.convertToDto(partialPlayer);

        // Then
        assertNotNull(result);
        assertEquals(partialPlayer.getId(), result.getId());
        assertEquals(partialPlayer.getFirstName(), result.getFirstName());
        assertEquals(partialPlayer.getSurname(), result.getSurname());
        assertEquals(partialPlayer.getEmail(), result.getEmail());
    }

    @Test
    void convertToDto_WithEmptyStringFields_ShouldMapEmptyStrings() {
        // Given
        Player emptyFieldPlayer = TestDataFactory.createPlayer();
        emptyFieldPlayer.setFirstName("");
        emptyFieldPlayer.setSurname("");
        emptyFieldPlayer.setEmail("");

        // When
        PlayerDto result = playerMapper.convertToDto(emptyFieldPlayer);

        // Then
        assertNotNull(result);
        assertEquals(emptyFieldPlayer.getId(), result.getId());
        assertEquals("", result.getFirstName());
        assertEquals("", result.getSurname());
        assertEquals("", result.getEmail());
    }
}
