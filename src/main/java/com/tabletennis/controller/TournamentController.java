package com.tabletennis.controller;

import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.service.RegistrationService;
import com.tabletennis.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String processRegistration(@ModelAttribute("registration") TournamentRegistration registration,
                                    BindingResult result,
                                    @RequestParam(value = "tournamentId", required = false) Long tournamentId,
                                    Model model) {

        // First check if tournament was selected
        if (tournamentId == null) {
            result.rejectValue("tournament", "error.tournament", "Please select a tournament");
        } else {
            // Set the tournament on the registration object
            Tournament tournament = tournamentService.findById(tournamentId).orElse(null);
            if (tournament == null) {
                result.rejectValue("tournament", "error.tournament", "Please select a valid tournament");
            } else {
                registration.setTournament(tournament);
            }
        }

        // Now manually validate the registration object with tournament set
        if (registration.getTournament() != null) {
            validateRegistrationFields(registration, result);
        }

        if (result.hasErrors()) {
            model.addAttribute("tournaments", tournamentService.findAllOrderByDate());
            return "registration";
        }

        registrationService.save(registration);
        return "redirect:/success";
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
    public String createTournament(@Valid @ModelAttribute("tournament") Tournament tournament,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "create-tournament";
        }

        tournamentService.save(tournament);
        return "redirect:/tournaments";
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
    public String updateTournament(@PathVariable Long id,
                                 @Valid @ModelAttribute("tournament") Tournament tournament,
                                 BindingResult result) {
        if (result.hasErrors()) {
            tournament.setId(id); // Ensure the ID is preserved for the form
            return "edit-tournament";
        }

        tournament.setId(id); // Ensure we're updating the correct tournament
        tournamentService.save(tournament);
        return "redirect:/tournaments";
    }

    @PostMapping("/tournaments/delete/{id}")
    public String deleteTournament(@PathVariable Long id) {
        // Check if tournament exists before deleting
        if (tournamentService.findById(id).isPresent()) {
            tournamentService.deleteById(id);
        }
        return "redirect:/tournaments";
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
