package com.tabletennis.service;

import com.tabletennis.dto.RegistrationDto;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.mapping.EntityToDtoMapper;
import com.tabletennis.repository.TournamentRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing tournament registrations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final TournamentRegistrationRepository registrationRepository;
    private final EntityToDtoMapper mapper;

    /**
     * Find all tournament registrations and return as DTOs
     */
    public List<RegistrationDto> findAllDto() {
        return registrationRepository.findAll().stream()
            .map(mapper::convertToDto)
            .toList();
    }

    /**
     * Find all tournament registrations (for internal use)
     */
    public List<TournamentRegistration> findAll() {
        return registrationRepository.findAll();
    }

    /**
     * Save tournament registration
     */
    public void save(TournamentRegistration registration) {
        registrationRepository.save(registration);
    }

    /**
     * Get tournament registration counts grouped by tournament name
     */
    public Map<String, Long> getTournamentCounts() {
        return findAll().stream()
            .collect(Collectors.groupingBy(
                r -> r.getTournament().getName(),
                Collectors.counting()
            ));
    }

    /**
     * Find registrations by tournament
     */
    public List<TournamentRegistration> findByTournament(Tournament tournament) {
        return registrationRepository.findByTournament(tournament);
    }

    /**
     * Find registrations by tournament and return as DTOs
     */
    public List<RegistrationDto> findByTournamentDto(Tournament tournament) {
        return registrationRepository.findByTournament(tournament).stream()
            .map(mapper::convertToDto)
            .toList();
    }

    /**
     * Find registrations by tournament ID and return as DTOs
     */
    public List<RegistrationDto> findByTournamentIdDto(Long tournamentId) {
        return registrationRepository.findByTournamentId(tournamentId).stream()
            .map(mapper::convertToDto)
            .toList();
    }
}
