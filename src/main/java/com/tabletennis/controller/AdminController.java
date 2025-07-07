package com.tabletennis.controller;

import com.tabletennis.entity.Game;
import com.tabletennis.entity.Tournament;
import com.tabletennis.service.GameService;
import com.tabletennis.service.RegistrationService;
import com.tabletennis.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for admin area functionality.
 * Provides access to tournament management and registration viewing.
 * Requires ADMIN role for access.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private static final String ERROR_ATTRIBUTE = "error";
    private static final String SUCCESS_ATTRIBUTE = "success";
    private static final String REDIRECT_ADMIN = "redirect:/admin";
    private static final String TOURNAMENT_NOT_FOUND = "Tournament not found";

    private final RegistrationService registrationService;
    private final TournamentService tournamentService;
    private final GameService gameService;

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

        // Add tournament details with game status
        model.addAttribute("tournaments", tournamentService.findAllOrderByDate());
        model.addAttribute("gameService", gameService);

        // Add username to model for display
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }

        return "admin/dashboard";
    }

    @PostMapping("/tournaments/{id}/start")
    public String startTournament(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Tournament tournament = tournamentService.findById(id)
                .orElse(null);
            if (tournament == null) {
                redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, TOURNAMENT_NOT_FOUND);
                return REDIRECT_ADMIN;
            }

            List<Game> games = gameService.startTournament(tournament);
            redirectAttributes.addFlashAttribute(SUCCESS_ATTRIBUTE,
                "Tournament started successfully! " + games.size() + " games created.");

            return "redirect:/admin/tournaments/" + id + "/games";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, e.getMessage());
            return REDIRECT_ADMIN;
        }
    }

    @GetMapping("/tournaments/{id}/games")
    public String showTournamentGames(@PathVariable Long id, Model model, Authentication authentication) {
        Tournament tournament = tournamentService.findById(id)
            .orElse(null);
        if (tournament == null) {
            model.addAttribute(ERROR_ATTRIBUTE, TOURNAMENT_NOT_FOUND);
            return REDIRECT_ADMIN;
        }

        List<Game> games = gameService.getGamesForTournament(tournament);
        long totalGames = games.size();
        long completedGames = games.stream().mapToLong(g -> g.getStatus() == Game.GameStatus.COMPLETED ? 1 : 0).sum();

        model.addAttribute("tournament", tournament);
        model.addAttribute("games", games);
        model.addAttribute("totalGames", totalGames);
        model.addAttribute("completedGames", completedGames);
        model.addAttribute("registrations", registrationService.findByTournamentId(id));

        // Add username to model for display
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }

        return "admin/tournament-games";
    }

    @PostMapping("/games/{gameId}/result")
    public String updateGameResult(@PathVariable Long gameId,
                                 @RequestParam Integer player1Score,
                                 @RequestParam Integer player2Score,
                                 RedirectAttributes redirectAttributes) {
        try {
            Game updatedGame = gameService.updateGameScore(gameId, player1Score, player2Score);
            Tournament tournament = updatedGame.getTournament();

            String message = "Game result updated: "
                    + updatedGame.getPlayer1Name()
                    + " "
                    + player1Score
                    + " - "
                    + player2Score
                    + " "
                    + updatedGame.getPlayer2Name();

            redirectAttributes.addFlashAttribute(SUCCESS_ATTRIBUTE, message);

            return "redirect:/admin/tournaments/" + tournament.getId() + "/games";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, e.getMessage());
            return REDIRECT_ADMIN;
        }
    }
}
