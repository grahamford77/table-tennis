package com.tabletennis.service;

import com.tabletennis.entity.Tournament;
import com.tabletennis.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of TournamentService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;

    @Override
    public List<Tournament> findAllOrderByDate() {
        return tournamentRepository.findAllByOrderByDateAsc();
    }

    @Override
    public Optional<Tournament> findById(Long id) {
        return tournamentRepository.findById(id);
    }

    @Override
    public Tournament save(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    /**
     * Count tournaments starting within the next 2 weeks
     * This is the new logic for "Active Tournaments"
     */
    @Override
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

    @Override
    public void deleteById(Long id) {
        tournamentRepository.deleteById(id);
    }
}
