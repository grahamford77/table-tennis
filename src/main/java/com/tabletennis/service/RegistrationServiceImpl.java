package com.tabletennis.service;

import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.repository.TournamentRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    private final TournamentRegistrationRepository registrationRepository;

    @Override
    public List<TournamentRegistration> findAll() {
        return registrationRepository.findAll();
    }

    @Override
    public TournamentRegistration save(TournamentRegistration registration) {
        return registrationRepository.save(registration);
    }

    @Override
    public Map<String, Long> getTournamentCounts() {
        return findAll().stream()
            .collect(Collectors.groupingBy(
                r -> r.getTournament().getName(),
                Collectors.counting()
            ));
    }

    @Override
    public List<TournamentRegistration> findByTournament(Tournament tournament) {
        return registrationRepository.findByTournament(tournament);
    }

    @Override
    public List<TournamentRegistration> findByTournamentId(Long tournamentId) {
        return registrationRepository.findByTournamentId(tournamentId);
    }
}
