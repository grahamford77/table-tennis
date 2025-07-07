package com.tabletennis.mapping;

import com.tabletennis.dto.GameDto;
import com.tabletennis.dto.PlayerDto;
import com.tabletennis.dto.RegistrationDto;
import com.tabletennis.dto.TournamentDto;
import com.tabletennis.entity.Game;
import com.tabletennis.entity.Player;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for mapping between entities and DTOs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EntityToDtoMapper {

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
        dto.setCurrentRegistrations(registrations.size());
        dto.setIsStarted(isStarted);
        dto.setIsFull(registrations.size() >= tournament.getMaxEntrants());
        return dto;
    }

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

    /**
     * Convert TournamentRegistration entity to DTO
     */
    public RegistrationDto convertToDto(TournamentRegistration registration) {
        var dto = new RegistrationDto();
        dto.setId(registration.getId());

        // Convert player to DTO
        dto.setPlayer(convertToDto(registration.getPlayer()));
        dto.setTournament(convertToDto(registration.getTournament()));

        // Set flattened fields for easy access
        dto.setTournamentName(registration.getTournament().getName());
        dto.setPlayerName(registration.getPlayer().getFullName());
        dto.setPlayerEmail(registration.getPlayer().getEmail());

        return dto;
    }

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

        // Convert players to DTOs
        dto.setPlayer1(convertToDto(game.getPlayer1()));
        dto.setPlayer2(convertToDto(game.getPlayer2()));

        // Set flattened fields for easy access
        dto.setPlayer1Name(game.getPlayer1Name());
        dto.setPlayer2Name(game.getPlayer2Name());

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
