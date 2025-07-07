package com.tabletennis.controller;

import com.tabletennis.dto.RegistrationRequest;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.service.PlayerService;
import com.tabletennis.service.RegistrationService;
import com.tabletennis.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller for handling tournament registrations
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private static final String JSON_SUCCESS_TRUE = "{\"success\": true, \"message\": \"Registration successful\"}";
    private static final String JSON_SUCCESS_FALSE_PREFIX = "{\"success\": false, \"message\": \"";
    private static final String JSON_SUFFIX = "\"}";

    private final RegistrationService registrationService;
    private final TournamentService tournamentService;
    private final PlayerService playerService;

    @GetMapping("/")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registration", new TournamentRegistration());
        model.addAttribute("tournaments", tournamentService.findAvailableForRegistration());
        return "registration";
    }

    @PostMapping("/register")
    public ResponseEntity<String> processRegistration(@Valid @RequestBody RegistrationRequest registrationRequest) {
        try {
            // Find the tournament
            var tournament = tournamentService.findById(registrationRequest.getTournamentId())
                    .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

            // Find or create the player
            var player = playerService.findOrCreatePlayer(
                    registrationRequest.getFirstName(),
                    registrationRequest.getSurname(),
                    registrationRequest.getEmail()
            );

            // Create registration entity
            var registration = new TournamentRegistration(player, tournament);

            registrationService.save(registration);
            return ResponseEntity.ok().body(JSON_SUCCESS_TRUE);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(JSON_SUCCESS_FALSE_PREFIX + e.getMessage() + JSON_SUFFIX);
        }
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success";
    }

    @GetMapping("/registrations")
    public String showRegistrations(Model model) {
        var allRegistrations = registrationService.findAll();
        model.addAttribute("registrations", allRegistrations);
        model.addAttribute("tournamentCounts", registrationService.getTournamentCounts());
        return "registrations";
    }
}
