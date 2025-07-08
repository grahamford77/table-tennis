package com.tabletennis.mapping;

import com.tabletennis.dto.RegistrationDto;
import com.tabletennis.entity.TournamentRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for mapping TournamentRegistration entities to DTOs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationMapper {

    private final PlayerMapper playerMapper;
    private final TournamentMapper tournamentMapper;

    /**
     * Convert TournamentRegistration entity to DTO
     */
    public RegistrationDto convertToDto(TournamentRegistration registration) {
        var dto = new RegistrationDto();
        dto.setId(registration.getId());

        // Convert player to DTO using PlayerMapper
        dto.setPlayer(playerMapper.convertToDto(registration.getPlayer()));

        // Convert tournament to DTO using TournamentMapper
        dto.setTournament(tournamentMapper.convertToDto(registration.getTournament()));

        // Set flattened fields for easy access
        dto.setTournamentName(registration.getTournament().getName());
        dto.setPlayerName(registration.getPlayer().getFullName());
        dto.setPlayerEmail(registration.getPlayer().getEmail());

        return dto;
    }
}
