package com.tabletennis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tabletennis.TestDataFactory;
import com.tabletennis.dto.TournamentRequest;
import com.tabletennis.entity.Tournament;
import com.tabletennis.repository.PlayerRepository;
import com.tabletennis.repository.TournamentRegistrationRepository;
import com.tabletennis.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TournamentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TournamentRegistrationRepository registrationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Tournament tournament;

    @BeforeEach
    void setUp() {
        // Clean up database
        registrationRepository.deleteAll();
        playerRepository.deleteAll();
        tournamentRepository.deleteAll();

        // Create test data
        tournament = TestDataFactory.createTournament();
        tournament = tournamentRepository.save(tournament);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showTournaments_ShouldReturnTournamentsPage() throws Exception {
        mockMvc.perform(get("/tournaments"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tournaments")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showCreateTournamentForm_ShouldReturnCreateTournamentPage() throws Exception {
        mockMvc.perform(get("/tournaments/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Create Tournament")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTournament_WithValidData_ShouldReturnSuccess() throws Exception {
        // Given
        TournamentRequest request = TestDataFactory.createTournamentRequest();

        mockMvc.perform(post("/tournaments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tournament created successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTournament_WithInvalidData_ShouldReturnValidationError() throws Exception {
        // Given
        TournamentRequest request = new TournamentRequest();
        // Missing required fields

        mockMvc.perform(post("/tournaments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showEditTournamentForm_WithValidId_ShouldReturnEditPage() throws Exception {
        mockMvc.perform(get("/tournaments/edit/{id}", tournament.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Edit Tournament")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showEditTournamentForm_WithInvalidId_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/tournaments/edit/{id}", 99999L))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTournament_WithValidData_ShouldReturnSuccess() throws Exception {
        // Given
        TournamentRequest request = new TournamentRequest();
        request.setName("Updated Tournament");
        request.setDescription("Updated description");
        request.setDate(LocalDate.now().plusDays(30));
        request.setTime(LocalTime.of(14, 0));
        request.setLocation("Updated Location");
        request.setMaxEntrants(32);

        mockMvc.perform(post("/tournaments/edit/{id}", tournament.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tournament updated successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTournament_WithInvalidId_ShouldReturnError() throws Exception {
        // Given
        TournamentRequest request = TestDataFactory.createTournamentRequest();

        mockMvc.perform(post("/tournaments/edit/{id}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTournament_WithValidId_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/tournaments/delete/{id}", tournament.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tournament deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTournament_WithInvalidId_ShouldReturnError() throws Exception {
        mockMvc.perform(post("/tournaments/delete/{id}", 99999L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Tournament not found"));
    }

    @Test
    void showTournaments_WithoutAuthentication_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/tournaments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void createTournament_WithoutAuthentication_ShouldRedirectToLogin() throws Exception {
        TournamentRequest request = TestDataFactory.createTournamentRequest();

        mockMvc.perform(post("/tournaments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
