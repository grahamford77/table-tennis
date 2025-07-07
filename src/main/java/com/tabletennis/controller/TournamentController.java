package com.tabletennis.controller;

import com.tabletennis.dto.RegistrationRequest;
import com.tabletennis.dto.TournamentRequest;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.service.RegistrationService;
import com.tabletennis.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class TournamentController {

    private final RegistrationService registrationService;
    private final TournamentService tournamentService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    public TournamentController(RegistrationService registrationService, TournamentService tournamentService) {
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

    @GetMapping("/tournaments")
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

    @GetMapping("/tournaments/new")
    public String showCreateTournamentForm(Model model) {
        model.addAttribute("tournament", new Tournament());
        return "create-tournament";
    }

    @PostMapping("/tournaments/create")
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

    @GetMapping("/tournaments/edit/{id}")
    public String showEditTournamentForm(@PathVariable Long id, Model model) {
        Tournament tournament = tournamentService.findById(id).orElse(null);
        if (tournament == null) {
            return "redirect:/tournaments";
        }
        model.addAttribute("tournament", tournament);
        return "edit-tournament";
    }

    @PostMapping("/tournaments/edit/{id}")
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

    @PostMapping("/tournaments/delete/{id}")
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

    private void validateRegistrationFields(TournamentRegistration registration, BindingResult result) {
        if (isBlank(registration.getFirstName())) {
            result.rejectValue("firstName", "error.firstName", "First name is required");
        }
        if (isBlank(registration.getSurname())) {
            result.rejectValue("surname", "error.surname", "Surname is required");
        }
        if (isBlank(registration.getEmail())) {
            result.rejectValue("email", "error.email", "Email is required");
        } else if (!EMAIL_PATTERN.matcher(registration.getEmail()).matches()) {
            result.rejectValue("email", "error.email", "Please enter a valid email address");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
