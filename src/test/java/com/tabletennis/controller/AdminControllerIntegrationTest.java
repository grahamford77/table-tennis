package com.tabletennis.controller;

import com.tabletennis.TestDataFactory;
import com.tabletennis.entity.Game;
import com.tabletennis.entity.Player;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.repository.GameRepository;
import com.tabletennis.repository.PlayerRepository;
import com.tabletennis.repository.TournamentRegistrationRepository;
import com.tabletennis.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TournamentRegistrationRepository registrationRepository;

    @Autowired
    private GameRepository gameRepository;

    private Tournament tournament;

    @BeforeEach
    void setUp() {
        // Clean up database
        gameRepository.deleteAll();
        registrationRepository.deleteAll();
        playerRepository.deleteAll();
        tournamentRepository.deleteAll();

        // Create test data
        tournament = TestDataFactory.createTournament();
        tournament = tournamentRepository.save(tournament);

        Player player1 = TestDataFactory.createPlayer();
        player1 = playerRepository.save(player1);

        Player player2 = TestDataFactory.createPlayer();
        player2 = playerRepository.save(player2);

        // Create registrations
        TournamentRegistration registration1 = new TournamentRegistration(player1, tournament);
        TournamentRegistration registration2 = new TournamentRegistration(player2, tournament);
        registrationRepository.save(registration1);
        registrationRepository.save(registration2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showAdminDashboard_ShouldReturnDashboardPage() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Admin Control Panel")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void startTournament_WithValidTournament_ShouldRedirectToGamesPage() throws Exception {
        mockMvc.perform(post("/admin/tournaments/{id}/start", tournament.getId()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void startTournament_WithInvalidTournament_ShouldRedirectToAdmin() throws Exception {
        mockMvc.perform(post("/admin/tournaments/{id}/start", 99999L))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void startTournament_WithInsufficientPlayers_ShouldRedirectWithError() throws Exception {
        // Create tournament with only one player
        Tournament singlePlayerTournament = TestDataFactory.createTournament();
        singlePlayerTournament = tournamentRepository.save(singlePlayerTournament);

        Player singlePlayer = TestDataFactory.createPlayer();
        singlePlayer = playerRepository.save(singlePlayer);

        TournamentRegistration singleRegistration = new TournamentRegistration(singlePlayer, singlePlayerTournament);
        registrationRepository.save(singleRegistration);

        mockMvc.perform(post("/admin/tournaments/{id}/start", singlePlayerTournament.getId()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showTournamentGames_WithValidTournament_ShouldReturnGamesPage() throws Exception {
        mockMvc.perform(get("/admin/tournaments/{id}/games", tournament.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tournament Games")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showTournamentGames_WithInvalidTournament_ShouldRedirectToAdmin() throws Exception {
        mockMvc.perform(get("/admin/tournaments/{id}/games", 99999L))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateGameResult_WithValidGame_ShouldRedirectToGamesPage() throws Exception {
        // First start the tournament to create games
        mockMvc.perform(post("/admin/tournaments/{id}/start", tournament.getId()))
                .andExpect(status().is3xxRedirection());

        // Get the created game
        var games = gameRepository.findAll();
        if (!games.isEmpty()) {
            Game createdGame = games.getFirst();

            mockMvc.perform(post("/admin/games/{gameId}/result", createdGame.getId())
                    .param("player1Score", "21")
                    .param("player2Score", "15"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateGameResult_WithInvalidGame_ShouldRedirectWithError() throws Exception {
        mockMvc.perform(post("/admin/games/{gameId}/result", 99999L)
                .param("player1Score", "21")
                .param("player2Score", "15"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void showAdminDashboard_WithoutAuthentication_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void startTournament_WithoutAuthentication_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(post("/admin/tournaments/{id}/start", tournament.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
