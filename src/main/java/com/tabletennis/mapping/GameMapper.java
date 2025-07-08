package com.tabletennis.mapping;

import com.tabletennis.dto.GameDto;
import com.tabletennis.entity.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for mapping Game entities to DTOs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GameMapper {

    private final PlayerMapper playerMapper;

    /**
     * Convert Game entity to DTO
     */
    public GameDto convertToDto(Game game) {
        var dto = new GameDto();
        dto.setId(game.getId());
        dto.setTournamentId(game.getTournament().getId());
        dto.setTournamentName(game.getTournament().getName());
        dto.setGameOrder(game.getGameOrder());
        dto.setPlayer1Score(game.getPlayer1Score());
        dto.setPlayer2Score(game.getPlayer2Score());
        dto.setStatus(game.getStatus().name());
        dto.setPlayedAt(game.getPlayedAt());

        // Convert players to DTOs using PlayerMapper
        dto.setPlayer1(playerMapper.convertToDto(game.getPlayer1()));
        dto.setPlayer2(playerMapper.convertToDto(game.getPlayer2()));

        // Set flattened fields for easy access
        dto.setPlayer1Name(game.getPlayer1Name());
        dto.setPlayer2Name(game.getPlayer2Name());

        return dto;
    }
}
