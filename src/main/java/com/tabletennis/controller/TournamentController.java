package com.tabletennis.controller;

import com.tabletennis.dto.TournamentRequest;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.service.RegistrationService;
import com.tabletennis.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for handling tournament management operations
 */
@Controller
@RequestMapping("/tournaments")
public class TournamentController {

    private final RegistrationService registrationService;
    private final TournamentService tournamentService;

    public TournamentController(RegistrationService registrationService, TournamentService tournamentService) {
        this.registrationService = registrationService;
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public String showTournaments(Model model) {
        List<Tournament> tournaments = tournamentService.findAllOrderByDate();
        List<TournamentRegistration> allRegistrations = registrationService.findAll();

        // Create a map of tournament ID to list of registrations
        Map<Long, List<TournamentRegistration>> tournamentRegistrations = allRegistrations.stream()
            .collect(Collectors.groupingBy(r -> r.getTournament().getId()));

        model.addAttribute("tournaments", tournaments);
        model.addAttribute("tournamentRegistrations", tournamentRegistrations);
        return "tournaments";
    }

    @GetMapping("/new")
    public String showCreateTournamentForm(Model model) {
        model.addAttribute("tournament", new Tournament());
        return "create-tournament";
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTournament(@Valid @RequestBody TournamentRequest tournamentRequest) {
        try {
            Tournament tournament = new Tournament();
            tournament.setName(tournamentRequest.getName());
            tournament.setDescription(tournamentRequest.getDescription());
            tournament.setDate(tournamentRequest.getDate());
            tournament.setTime(tournamentRequest.getTime());
            tournament.setLocation(tournamentRequest.getLocation());
            tournament.setMaxEntrants(tournamentRequest.getMaxEntrants());

            tournamentService.save(tournament);
            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Tournament created successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditTournamentForm(@PathVariable Long id, Model model) {
        Tournament tournament = tournamentService.findById(id).orElse(null);
        if (tournament == null) {
            return "redirect:/tournaments";
        }
        model.addAttribute("tournament", tournament);
        return "edit-tournament";
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<?> updateTournament(@PathVariable Long id,
                                            @Valid @RequestBody TournamentRequest tournamentRequest) {
        try {
            Tournament tournament = tournamentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

            tournament.setName(tournamentRequest.getName());
            tournament.setDescription(tournamentRequest.getDescription());
            tournament.setDate(tournamentRequest.getDate());
            tournament.setTime(tournamentRequest.getTime());
            tournament.setLocation(tournamentRequest.getLocation());
            tournament.setMaxEntrants(tournamentRequest.getMaxEntrants());

            tournamentService.save(tournament);
            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Tournament updated successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteTournament(@PathVariable Long id) {
        try {
            if (tournamentService.findById(id).isPresent()) {
                tournamentService.deleteById(id);
                return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Tournament deleted successfully\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Tournament not found\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}
