package com.tabletennis.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing a player's registration for a tournament
 * This is a join table between Player and Tournament
 */
@Entity
@Table(name = "tournament_registrations")
public class TournamentRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Player is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @NotNull(message = "Tournament selection is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    // Constructors
    public TournamentRegistration() {}

    public TournamentRegistration(Player player, Tournament tournament) {
        this.player = player;
        this.tournament = tournament;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    // Convenience methods for backward compatibility
    public String getFirstName() {
        return player != null ? player.getFirstName() : null;
    }

    public String getSurname() {
        return player != null ? player.getSurname() : null;
    }

    public String getEmail() {
        return player != null ? player.getEmail() : null;
    }

    public String getFullName() {
        return player != null ? player.getFullName() : null;
    }

    @Override
    public String toString() {
        return "TournamentRegistration{" +
                "id=" + id +
                ", player=" + (player != null ? player.getFullName() : "null") +
                ", tournament=" + (tournament != null ? tournament.getName() : "null") +
                '}';
    }
}
