package com.tabletennis.service;

import com.tabletennis.dto.TournamentDto;
import com.tabletennis.dto.TournamentRequest;
import com.tabletennis.entity.Tournament;
import com.tabletennis.mapping.TournamentMapper;
import com.tabletennis.mapping.RegistrationMapper;
import com.tabletennis.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for tournament management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final GameService gameService;
    private final RegistrationService registrationService;
    private final TournamentMapper tournamentMapper;
    private final RegistrationMapper registrationMapper;

    /**
     * Find all tournaments ordered by date and return as DTOs
     */
    public List<TournamentDto> findAllOrderByDate() {
        return tournamentRepository.findAllByOrderByDateAsc().stream()
            .map(tournament -> {
                var registrations = registrationService.findByTournament(tournament).stream()
                    .map(registrationMapper::convertToDto)
                    .toList();
                var isStarted = gameService.isTournamentStarted(tournament);
                return tournamentMapper.convertToDto(tournament, registrations, isStarted);
            })
            .toList();
    }

    /**
     * Find tournament by ID and return as DTO
     */
    public Optional<TournamentDto> findByIdDto(Long id) {
        return tournamentRepository.findById(id)
            .map(tournament -> {
                var registrations = registrationService.findByTournament(tournament).stream()
                    .map(registrationMapper::convertToDto)
                    .toList();
                var isStarted = gameService.isTournamentStarted(tournament);
                return tournamentMapper.convertToDto(tournament, registrations, isStarted);
            });
    }

    /**
     * Find tournament entity by ID (for internal use)
     */
    public Optional<Tournament> findById(Long id) {
        return tournamentRepository.findById(id);
    }

    /**
     * Create new tournament from request DTO
     */
    public TournamentDto createTournament(TournamentRequest tournamentRequest) {
        var tournament = new Tournament();
        setTournamentFields(tournament, tournamentRequest);
        var savedTournament = tournamentRepository.save(tournament);
        return tournamentMapper.convertToDto(savedTournament);
    }

    /**
     * Update tournament from request DTO
     */
    public TournamentDto updateTournament(Long id, TournamentRequest tournamentRequest) {
        var tournament = tournamentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        setTournamentFields(tournament, tournamentRequest);
        var savedTournament = tournamentRepository.save(tournament);
        return tournamentMapper.convertToDto(savedTournament);
    }

    /**
     * Count tournaments starting within the next 2 weeks
     * This is the new logic for "Active Tournaments"
     */
    public long countActiveTournaments() {
        var today = LocalDate.now();
        var twoWeeksFromNow = today.plusWeeks(2);

        return tournamentRepository.findAll().stream()
            .filter(tournament -> {
                var tournamentDate = tournament.getDate();
                return tournamentDate != null &&
                       !tournamentDate.isBefore(today) &&
                       !tournamentDate.isAfter(twoWeeksFromNow);
            })
            .count();
    }

    /**
     * Delete tournament by ID
     */
    public void deleteById(Long id) {
        tournamentRepository.deleteById(id);
    }

    /**
     * Find tournaments that are available for registration and return as DTOs
     */
    public List<TournamentDto> findAvailableForRegistration() {
        return tournamentRepository.findAllByOrderByDateAsc().stream()
            .filter(tournament -> {
                // Check if tournament has started (has games generated)
                var hasStarted = gameService.isTournamentStarted(tournament);
                if (hasStarted) {
                    return false;
                }

                // Check if tournament is full
                var registrationCount = registrationService.findByTournament(tournament).size();
                var isFull = registrationCount >= tournament.getMaxEntrants();

                return !isFull;
            })
            .map(tournamentMapper::convertToDto)
            .toList();
    }

    /**
     * Set tournament fields from request DTO
     */
    private void setTournamentFields(Tournament tournament, com.tabletennis.dto.TournamentRequest tournamentRequest) {
        tournament.setName(tournamentRequest.getName());
        tournament.setDescription(tournamentRequest.getDescription());
        tournament.setDate(tournamentRequest.getDate());
        tournament.setTime(tournamentRequest.getTime());
        tournament.setLocation(tournamentRequest.getLocation());
        tournament.setMaxEntrants(tournamentRequest.getMaxEntrants());
    }
}
