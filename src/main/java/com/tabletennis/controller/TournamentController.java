package com.tabletennis.controller;

import java.util.stream.Collectors;

import com.tabletennis.dto.TournamentDto;
import com.tabletennis.dto.TournamentRequest;
import com.tabletennis.service.GameService;
import com.tabletennis.service.RegistrationService;
import com.tabletennis.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling tournament management operations
 */
@Controller
@RequestMapping("/tournaments")
@RequiredArgsConstructor
@Slf4j
public class TournamentController {

    private static final String JSON_SUCCESS_CREATED = "{\"success\": true, \"message\": \"Tournament created successfully\"}";
    private static final String JSON_SUCCESS_UPDATED = "{\"success\": true, \"message\": \"Tournament updated successfully\"}";
    private static final String JSON_SUCCESS_DELETED = "{\"success\": true, \"message\": \"Tournament deleted successfully\"}";
    private static final String JSON_ERROR_NOT_FOUND = "{\"success\": false, \"message\": \"Tournament not found\"}";
    private static final String JSON_SUCCESS_FALSE_PREFIX = "{\"success\": false, \"message\": \"";
    private static final String JSON_SUFFIX = "\"}";

    private final RegistrationService registrationService;
    private final TournamentService tournamentService;
    private final GameService gameService;

    @GetMapping
    public String showTournaments(Model model) {
        var tournaments = tournamentService.findAllOrderByDate();
        var allRegistrations = registrationService.findAllDto();

        // Create a map of tournament ID to list of registrations
        var tournamentRegistrations = allRegistrations.stream()
            .collect(Collectors.groupingBy(r -> r.getTournament() != null ? r.getTournament().getId() : 0L));

        // Create a map of tournament ID to started status
        var tournamentStartedStatus = tournaments.stream()
            .collect(Collectors.toMap(
                TournamentDto::getId,
                t -> gameService.isTournamentStarted(tournamentService.findById(t.getId()).orElse(null))
            ));

        model.addAttribute("tournaments", tournaments);
        model.addAttribute("tournamentRegistrations", tournamentRegistrations);
        model.addAttribute("tournamentStartedStatus", tournamentStartedStatus);
        return "tournaments";
    }

    @GetMapping("/new")
    public String showCreateTournamentForm(Model model) {
        model.addAttribute("tournament", new TournamentDto());
        return "create-tournament";
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTournament(@Valid @RequestBody TournamentRequest tournamentRequest) {
        try {
            tournamentService.createTournament(tournamentRequest);
            return ResponseEntity.ok().body(JSON_SUCCESS_CREATED);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditTournamentForm(@PathVariable Long id, Model model) {
        var tournament = tournamentService.findByIdDto(id).orElse(null);
        if (tournament == null) {
            return "redirect:/tournaments";
        }
        model.addAttribute("tournament", tournament);
        return "edit-tournament";
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<String> updateTournament(@PathVariable Long id,
                                            @Valid @RequestBody TournamentRequest tournamentRequest) {
        try {
            tournamentService.updateTournament(id, tournamentRequest);
            return ResponseEntity.ok().body(JSON_SUCCESS_UPDATED);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable Long id) {
        try {
            var tournamentOpt = tournamentService.findById(id);
            if (tournamentOpt.isPresent()) {
                var tournament = tournamentOpt.get();

                // Check if tournament is started (has games created)
                if (gameService.isTournamentStarted(tournament)) {
                    return ResponseEntity.badRequest().body(JSON_SUCCESS_FALSE_PREFIX + "Cannot delete tournament that has already started" + JSON_SUFFIX);
                }

                tournamentService.deleteById(id);
                return ResponseEntity.ok().body(JSON_SUCCESS_DELETED);
            } else {
                return ResponseEntity.badRequest().body(JSON_ERROR_NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    /**
     * Creates a JSON error response with the given error message
     *
     * @param errorMessage the error message to include in the response
     * @return ResponseEntity with JSON error response
     */
    private ResponseEntity<String> createErrorResponse(String errorMessage) {
        return ResponseEntity.badRequest().body(JSON_SUCCESS_FALSE_PREFIX + errorMessage + JSON_SUFFIX);
    }
}
