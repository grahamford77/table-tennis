package com.tabletennis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Game data transfer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
    private Long id;
    private Long tournamentId;
    private String tournamentName;
    private PlayerDto player1;
    private PlayerDto player2;
    private String player1Name;
    private String player2Name;
    private Integer gameOrder;
    private Integer player1Score;
    private Integer player2Score;
    private String status;
    private LocalDateTime playedAt;
}
