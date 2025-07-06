package com.tabletennis.service;

import com.tabletennis.entity.TournamentRegistration;

import java.util.List;
import java.util.Map;

public interface RegistrationService {
    List<TournamentRegistration> findAll();
    TournamentRegistration save(TournamentRegistration registration);
    Map<String, Long> getTournamentCounts();
}
