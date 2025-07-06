package com.tabletennis.service;

import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.repository.TournamentRegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final TournamentRegistrationRepository registrationRepository;

    public RegistrationServiceImpl(TournamentRegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

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
}
