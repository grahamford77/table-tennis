package com.tabletennis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a game between two players in a tournament
 */
@Entity
@Table(name = "games")
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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "played_at")
    private LocalDateTime playedAt;

    public enum GameStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    // Constructors
    public Game() {
        this.createdAt = LocalDateTime.now();
    }

    public Game(Tournament tournament, Player player1, Player player2, Integer gameOrder) {
        this();
        this.tournament = tournament;
        this.player1 = player1;
        this.player2 = player2;
        this.gameOrder = gameOrder;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Integer getGameOrder() {
        return gameOrder;
    }

    public void setGameOrder(Integer gameOrder) {
        this.gameOrder = gameOrder;
    }

    public Integer getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(Integer player1Score) {
        this.player1Score = player1Score;
    }

    public Integer getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(Integer player2Score) {
        this.player2Score = player2Score;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    // Convenience methods for backward compatibility with templates
    public String getPlayer1Name() {
        return player1 != null ? player1.getFullName() : null;
    }

    public String getPlayer1Email() {
        return player1 != null ? player1.getEmail() : null;
    }

    public String getPlayer2Name() {
        return player2 != null ? player2.getFullName() : null;
    }

    public String getPlayer2Email() {
        return player2 != null ? player2.getEmail() : null;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", player1Name='" + getPlayer1Name() + '\'' +
                ", player2Name='" + getPlayer2Name() + '\'' +
                ", gameOrder=" + gameOrder +
                ", status=" + status +
                '}';
    }
}
