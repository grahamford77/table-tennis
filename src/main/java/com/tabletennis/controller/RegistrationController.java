package com.tabletennis.controller;

import com.tabletennis.dto.RegistrationRequest;
import com.tabletennis.entity.Player;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.service.PlayerService;
import com.tabletennis.service.RegistrationService;
import com.tabletennis.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Controller for handling tournament registrations
 */
@Controller
public class RegistrationController {

    private final RegistrationService registrationService;
    private final TournamentService tournamentService;
    private final PlayerService playerService;

    public RegistrationController(RegistrationService registrationService, TournamentService tournamentService, PlayerService playerService) {
        this.registrationService = registrationService;
        this.tournamentService = tournamentService;
        this.playerService = playerService;
    }

    @GetMapping("/")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registration", new TournamentRegistration());
        model.addAttribute("tournaments", tournamentService.findAllOrderByDate());
        return "registration";
    }

    @PostMapping("/register")
    public ResponseEntity<?> processRegistration(@Valid @RequestBody RegistrationRequest registrationRequest) {
        try {
            // Find the tournament
            Tournament tournament = tournamentService.findById(registrationRequest.getTournamentId())
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

            // Find or create the player
            Player player = playerService.findOrCreatePlayer(
                registrationRequest.getFirstName(),
                registrationRequest.getSurname(),
                registrationRequest.getEmail()
            );

            // Create registration entity
            TournamentRegistration registration = new TournamentRegistration(player, tournament);

            registrationService.save(registration);
            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Registration successful\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success";
    }

    @GetMapping("/registrations")
    public String showRegistrations(Model model) {
        List<TournamentRegistration> allRegistrations = registrationService.findAll();
        model.addAttribute("registrations", allRegistrations);
        model.addAttribute("tournamentCounts", registrationService.getTournamentCounts());
        return "registrations";
    }
}
