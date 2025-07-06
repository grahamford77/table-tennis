package com.tabletennis.service;

import com.tabletennis.entity.Tournament;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for tournament management
 */
public interface TournamentService {

    /**
     * Find all tournaments ordered by date
     */
    List<Tournament> findAllOrderByDate();

    /**
     * Find tournament by ID
     */
    Optional<Tournament> findById(Long id);

    /**
     * Save tournament
     */
    Tournament save(Tournament tournament);

    /**
     * Count tournaments starting within the next 2 weeks
     */
    long countActiveTournaments();

    /**
     * Delete tournament by ID
     */
    void deleteById(Long id);
}
