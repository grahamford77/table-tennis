package com.tabletennis.service;

import com.tabletennis.entity.Player;
import com.tabletennis.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
     * Find all players
     */
    public List<Player> findAll() {
        return playerRepository.findAll();
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

}
