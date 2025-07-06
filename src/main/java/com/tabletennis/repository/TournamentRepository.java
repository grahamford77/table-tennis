package com.tabletennis.repository;

import com.tabletennis.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Tournament entity
 */
@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    /**
     * Find all tournaments ordered by date in ascending order
     */
    List<Tournament> findAllByOrderByDateAsc();
}
