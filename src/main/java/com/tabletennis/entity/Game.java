package com.tabletennis.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a game between two players in a tournament
 */
@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id", nullable = false)
    private Player player1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id", nullable = false)
    private Player player2;

    @Column(name = "game_order", nullable = false)
    private Integer gameOrder;

    @Column(name = "player1_score")
    private Integer player1Score;

    @Column(name = "player2_score")
    private Integer player2Score;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameStatus status = GameStatus.SCHEDULED;

    @Column(name = "played_at")
    private LocalDateTime playedAt;

    public Game(Tournament tournament, Player player1, Player player2, Integer gameOrder) {
        this.tournament = tournament;
        this.player1 = player1;
        this.player2 = player2;
        this.gameOrder = gameOrder;
    }

    // Convenience methods for getting player names
    public String getPlayer1Name() {
        return player1 != null ? player1.getFullName() : "Unknown";
    }

    public String getPlayer2Name() {
        return player2 != null ? player2.getFullName() : "Unknown";
    }

    public enum GameStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
