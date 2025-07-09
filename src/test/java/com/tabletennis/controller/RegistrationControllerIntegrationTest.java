package com.tabletennis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tabletennis.TestDataFactory;
import com.tabletennis.dto.RegistrationRequest;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RegistrationControllerIntegrationTest {

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
    void showRegistrationForm_ShouldReturnRegistrationPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tournament Registration")));
    }

    @Test
    void processRegistration_WithValidData_ShouldReturnSuccess() throws Exception {
        // Given
        var request = new RegistrationRequest();
        request.setTournamentId(tournament.getId());
        request.setFirstName("John");
        request.setSurname("Doe");
        request.setEmail("john.doe@example.com");

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    void processRegistration_WithInvalidTournamentId_ShouldReturnError() throws Exception {
        // Given
        var request = new RegistrationRequest();
        request.setTournamentId(99999L);
        request.setFirstName("John");
        request.setSurname("Doe");
        request.setEmail("john.doe@example.com");

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Tournament not found")));
    }

    @Test
    void processRegistration_WithMissingFields_ShouldReturnValidationError() throws Exception {
        // Given
        var request = new RegistrationRequest();
        request.setTournamentId(tournament.getId());
        // Missing required fields

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void showSuccessPage_ShouldReturnSuccessPage() throws Exception {
        mockMvc.perform(get("/success"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Registration Successful")));
    }

    @Test
    void showRegistrations_ShouldReturnRegistrationsPage() throws Exception {
        mockMvc.perform(get("/registrations"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tournament Registrations")));
    }
}
