package com.tabletennis.service;

import com.tabletennis.entity.Player;
import com.tabletennis.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing players
 */
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Find all players
     */
    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    /**
     * Find player by ID
     */
    public Optional<Player> findById(Long id) {
        return playerRepository.findById(id);
    }

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
        Optional<Player> existingPlayer = findByEmail(email);

        if (existingPlayer.isPresent()) {
            // Update existing player's name if different
            Player player = existingPlayer.get();
            if (!player.getFirstName().equals(firstName) || !player.getSurname().equals(surname)) {
                player.setFirstName(firstName);
                player.setSurname(surname);
                return save(player);
            }
            return player;
        } else {
            // Create new player
            Player newPlayer = new Player(firstName, surname, email);
            return save(newPlayer);
        }
    }

    /**
     * Check if player exists by email
     */
    public boolean existsByEmail(String email) {
        return playerRepository.existsByEmail(email);
    }

    /**
     * Delete player by ID
     */
    public void deleteById(Long id) {
        playerRepository.deleteById(id);
    }
}
