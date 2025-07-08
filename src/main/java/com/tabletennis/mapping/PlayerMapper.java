package com.tabletennis.mapping;

import com.tabletennis.dto.PlayerDto;
import com.tabletennis.entity.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for mapping Player entities to DTOs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerMapper {

    /**
     * Convert Player entity to DTO
     */
    public PlayerDto convertToDto(Player player) {
        var dto = new PlayerDto();
        dto.setId(player.getId());
        dto.setFirstName(player.getFirstName());
        dto.setSurname(player.getSurname());
        dto.setEmail(player.getEmail());
        dto.setFullName(player.getFullName());
        return dto;
    }
}
