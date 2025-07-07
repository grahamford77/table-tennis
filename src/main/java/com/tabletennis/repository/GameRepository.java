package com.tabletennis.repository;

import com.tabletennis.entity.Game;
import com.tabletennis.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Game entities
 */
@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    /**
     * Find all games for a specific tournament, ordered by game order
     */
    List<Game> findByTournamentOrderByGameOrderAsc(Tournament tournament);

    /**
     * Find all games for a specific tournament ID, ordered by game order
     */
    List<Game> findByTournamentIdOrderByGameOrderAsc(Long tournamentId);

    /**
     * Check if games exist for a tournament
     */
    boolean existsByTournament(Tournament tournament);

    /**
     * Count games for a tournament
     */
    long countByTournament(Tournament tournament);
}
