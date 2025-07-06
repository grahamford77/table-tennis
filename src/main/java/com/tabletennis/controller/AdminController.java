package com.tabletennis.controller;

import com.tabletennis.service.RegistrationService;
import com.tabletennis.service.TournamentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for admin area functionality.
 * Provides access to tournament management and registration viewing.
 * Requires ADMIN role for access.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RegistrationService registrationService;
    private final TournamentService tournamentService;

    public AdminController(RegistrationService registrationService, TournamentService tournamentService) {
        this.registrationService = registrationService;
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public String showAdminDashboard(Model model, Authentication authentication) {
        // Get summary statistics for the dashboard
        long totalTournaments = tournamentService.findAllOrderByDate().size();
        long totalRegistrations = registrationService.findAll().size();

        // NEW: Count tournaments starting within the next 2 weeks
        long activeTournaments = tournamentService.countActiveTournaments();

        model.addAttribute("totalTournaments", totalTournaments);
        model.addAttribute("totalRegistrations", totalRegistrations);
        model.addAttribute("activeTournaments", activeTournaments);
        model.addAttribute("tournamentCounts", registrationService.getTournamentCounts());

        // Add username to model for display
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }

        return "admin/dashboard";
    }
}
