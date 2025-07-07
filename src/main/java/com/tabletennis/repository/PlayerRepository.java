package com.tabletennis.repository;

import com.tabletennis.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Player entities
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    /**
     * Find a player by email address
     */
    Optional<Player> findByEmail(String email);

}
