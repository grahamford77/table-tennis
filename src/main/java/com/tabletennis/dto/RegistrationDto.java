package com.tabletennis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for Tournament Registration data transfer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {
    private Long id;
    private PlayerDto player;
    private TournamentDto tournament;
    private String tournamentName;
    private String playerName;
    private String playerEmail;
}
