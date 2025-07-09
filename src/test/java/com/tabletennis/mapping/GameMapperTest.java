package com.tabletennis.mapping;

import com.tabletennis.TestDataFactory;
import com.tabletennis.dto.PlayerDto;
import com.tabletennis.entity.Game;
import com.tabletennis.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameMapperTest {

    @Mock
    private PlayerMapper playerMapper;

    private GameMapper gameMapper;

    private Game game;
    private Player player1;
    private Player player2;
    private PlayerDto player1Dto;
    private PlayerDto player2Dto;

    @BeforeEach
    void setUp() {
        gameMapper = new GameMapper(playerMapper);

        // Create test data using TestDataFactory
        game = TestDataFactory.createGame();
        player1 = game.getPlayer1();
        player2 = game.getPlayer2();
        player1Dto = TestDataFactory.createPlayerDtoFromPlayer(player1);
        player2Dto = TestDataFactory.createPlayerDtoFromPlayer(player2);
    }

    @Test
    void convertToDto_ShouldMapGameToDto() {
        // Given
        when(playerMapper.convertToDto(player1)).thenReturn(player1Dto);
        when(playerMapper.convertToDto(player2)).thenReturn(player2Dto);

        // When
        var result = gameMapper.convertToDto(game);

        // Then
        assertNotNull(result);
        assertEquals(game.getId(), result.getId());
        assertEquals(game.getTournament().getId(), result.getTournamentId());
        assertEquals(game.getTournament().getName(), result.getTournamentName());
        assertEquals(game.getGameOrder(), result.getGameOrder());
        assertEquals(game.getPlayer1Score(), result.getPlayer1Score());
        assertEquals(game.getPlayer2Score(), result.getPlayer2Score());
        assertEquals(game.getPlayedAt(), result.getPlayedAt());
        assertEquals(player1Dto, result.getPlayer1());
        assertEquals(player2Dto, result.getPlayer2());
    }

    @Test
    void convertToDto_WithNullGame_ShouldThrowException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> gameMapper.convertToDto(null));
    }
}
