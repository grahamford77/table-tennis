package com.tabletennis.mapping;

import com.tabletennis.dto.TournamentDto;
import com.tabletennis.dto.RegistrationDto;
import com.tabletennis.entity.Tournament;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for mapping Tournament entities to DTOs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentMapper {

    /**
     * Convert Tournament entity to DTO
     */
    public TournamentDto convertToDto(Tournament tournament) {
        var dto = new TournamentDto();
        dto.setId(tournament.getId());
        dto.setName(tournament.getName());
        dto.setDescription(tournament.getDescription());
        dto.setDate(tournament.getDate());
        dto.setTime(tournament.getTime());
        dto.setLocation(tournament.getLocation());
        dto.setMaxEntrants(tournament.getMaxEntrants());
        dto.setDisplayName(tournament.getDisplayName());
        return dto;
    }

    /**
     * Convert Tournament entity to DTO with registrations
     */
    public TournamentDto convertToDto(Tournament tournament, List<RegistrationDto> registrations, boolean isStarted) {
        var dto = convertToDto(tournament);
        dto.setRegistrations(registrations);
        dto.setCurrentRegistrations(registrations != null ? registrations.size() : 0);
        dto.setStarted(isStarted);
        dto.setFull(registrations != null && registrations.size() >= tournament.getMaxEntrants());
        return dto;
    }

    /**
     * Convert TournamentDto to Tournament entity (for internal operations)
     */
    public Tournament convertToEntity(TournamentDto dto) {
        var tournament = new Tournament();
        tournament.setId(dto.getId());
        tournament.setName(dto.getName());
        tournament.setDescription(dto.getDescription());
        tournament.setDate(dto.getDate());
        tournament.setTime(dto.getTime());
        tournament.setLocation(dto.getLocation());
        tournament.setMaxEntrants(dto.getMaxEntrants());
        return tournament;
    }
}
