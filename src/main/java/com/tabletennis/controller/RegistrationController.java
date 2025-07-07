package com.tabletennis.controller;

import com.tabletennis.dto.RegistrationRequest;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
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

    public RegistrationController(RegistrationService registrationService, TournamentService tournamentService) {
        this.registrationService = registrationService;
        this.tournamentService = tournamentService;
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

            // Create registration entity
            TournamentRegistration registration = new TournamentRegistration();
            registration.setFirstName(registrationRequest.getFirstName());
            registration.setSurname(registrationRequest.getSurname());
            registration.setEmail(registrationRequest.getEmail());
            registration.setTournament(tournament);

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
