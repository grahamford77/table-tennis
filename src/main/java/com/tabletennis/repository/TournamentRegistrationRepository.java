package com.tabletennis.repository;

import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration, Long> {

    // Find all registrations for a specific tournament
    List<TournamentRegistration> findByTournament(Tournament tournament);

    // Find all registrations for a tournament by ID
    List<TournamentRegistration> findByTournamentId(Long tournamentId);
}
