package com.tabletennis.service;

import com.tabletennis.entity.Player;
import com.tabletennis.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for managing players
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;

    /**
     * Find player by email
     */
    public Optional<Player> findByEmail(String email) {
        return playerRepository.findByEmail(email);
    }

    /**
     * Save or update a player
     */
    public Player save(Player player) {
        return playerRepository.save(player);
    }

    /**
     * Find or create a player by email, firstName, and surname
     * If player exists with same email, return existing player
     * Otherwise create new player
     */
    public Player findOrCreatePlayer(String firstName, String surname, String email) {
        return findByEmail(email)
                .orElseGet(() -> save(new Player(firstName, surname, email)));
    }

}
