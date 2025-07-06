package com.tabletennis.controller;

import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.service.RegistrationService;
import com.tabletennis.service.TournamentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Integration tests for TournamentController.
 * Tests all web endpoints using MockMvc.
 */
@WebMvcTest(value = TournamentController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@DisplayName("Tournament Controller Tests")
class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TournamentService tournamentService;

    @MockBean
    private RegistrationService registrationService;

    private Tournament tournament;
    private TournamentRegistration registration;

    @BeforeEach
    void setUp() {
        tournament = new Tournament("Test Tournament", "Test Description",
                LocalDate.of(2025, 8, 15), LocalTime.of(14, 0),
                "Test Location", 32);
        tournament.setId(1L);

        registration = new TournamentRegistration("John", "Doe", "john@email.com", tournament);
        registration.setId(1L);
    }

    @Test
    @DisplayName("Should show registration form with tournaments")
    void shouldShowRegistrationForm() throws Exception {
        // Given
        when(tournamentService.findAllOrderByDate()).thenReturn(Arrays.asList(tournament));

        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("registration"))
                .andExpect(model().attributeExists("tournaments"));

        verify(tournamentService, times(1)).findAllOrderByDate();
    }

    @Test
    @DisplayName("Should process valid registration successfully")
    void shouldProcessValidRegistration() throws Exception {
        // Given
        when(tournamentService.findById(1L)).thenReturn(Optional.of(tournament));
        when(registrationService.save(any(TournamentRegistration.class))).thenReturn(registration);

        // When & Then
        mockMvc.perform(post("/register")
                .param("firstName", "John")
                .param("surname", "Doe")
                .param("email", "john@email.com")
                .param("tournamentId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/success"));

        verify(tournamentService, times(1)).findById(1L);
        verify(registrationService, times(1)).save(any(TournamentRegistration.class));
    }

    @Test
    @DisplayName("Should reject registration with missing tournament")
    void shouldRejectRegistrationWithMissingTournament() throws Exception {
        // Given
        when(tournamentService.findAllOrderByDate()).thenReturn(Arrays.asList(tournament));

        // When & Then
        mockMvc.perform(post("/register")
                .param("firstName", "John")
                .param("surname", "Doe")
                .param("email", "john@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().hasErrors());

        verify(registrationService, never()).save(any(TournamentRegistration.class));
    }

    @Test
    @DisplayName("Should reject registration with invalid email")
    void shouldRejectRegistrationWithInvalidEmail() throws Exception {
        // Given
        when(tournamentService.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentService.findAllOrderByDate()).thenReturn(Arrays.asList(tournament));

        // When & Then
        mockMvc.perform(post("/register")
                .param("firstName", "John")
                .param("surname", "Doe")
                .param("email", "invalid-email")
                .param("tournamentId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().hasErrors());

        verify(registrationService, never()).save(any(TournamentRegistration.class));
    }

    @Test
    @DisplayName("Should show success page")
    void shouldShowSuccessPage() throws Exception {
        mockMvc.perform(get("/success"))
                .andExpect(status().isOk())
                .andExpect(view().name("success"));
    }

    @Test
    @DisplayName("Should show registrations with statistics")
    void shouldShowRegistrationsWithStatistics() throws Exception {
        // Given
        Map<String, Long> tournamentCounts = new HashMap<>();
        tournamentCounts.put("Test Tournament", 2L);

        when(registrationService.findAll()).thenReturn(Arrays.asList(registration));
        when(registrationService.getTournamentCounts()).thenReturn(tournamentCounts);

        // When & Then
        mockMvc.perform(get("/registrations"))
                .andExpect(status().isOk())
                .andExpect(view().name("registrations"))
                .andExpect(model().attributeExists("registrations"))
                .andExpect(model().attributeExists("tournamentCounts"));

        verify(registrationService, times(1)).findAll();
        verify(registrationService, times(1)).getTournamentCounts();
    }

    @Test
    @DisplayName("Should show tournaments list")
    void shouldShowTournamentsList() throws Exception {
        // Given
        when(tournamentService.findAllOrderByDate()).thenReturn(Arrays.asList(tournament));
        when(registrationService.findAll()).thenReturn(Arrays.asList(registration));

        // When & Then
        mockMvc.perform(get("/tournaments"))
                .andExpect(status().isOk())
                .andExpect(view().name("tournaments"))
                .andExpect(model().attributeExists("tournaments"))
                .andExpect(model().attributeExists("tournamentRegistrations"));

        verify(tournamentService, times(1)).findAllOrderByDate();
        verify(registrationService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should show create tournament form")
    void shouldShowCreateTournamentForm() throws Exception {
        mockMvc.perform(get("/tournaments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-tournament"))
                .andExpect(model().attributeExists("tournament"));
    }

    @Test
    @DisplayName("Should create tournament successfully")
    void shouldCreateTournamentSuccessfully() throws Exception {
        // Given
        when(tournamentService.save(any(Tournament.class))).thenReturn(tournament);

        // When & Then
        mockMvc.perform(post("/tournaments/create")
                .param("name", "New Tournament")
                .param("description", "Description")
                .param("date", "2025-08-15")
                .param("time", "14:00")
                .param("location", "Location")
                .param("maxEntrants", "32"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tournaments"));

        verify(tournamentService, times(1)).save(any(Tournament.class));
    }

    @Test
    @DisplayName("Should show edit tournament form")
    void shouldShowEditTournamentForm() throws Exception {
        // Given
        when(tournamentService.findById(1L)).thenReturn(Optional.of(tournament));

        // When & Then
        mockMvc.perform(get("/tournaments/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-tournament"))
                .andExpect(model().attributeExists("tournament"));

        verify(tournamentService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should redirect when tournament not found for edit")
    void shouldRedirectWhenTournamentNotFoundForEdit() throws Exception {
        // Given
        when(tournamentService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/tournaments/edit/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tournaments"));

        verify(tournamentService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should update tournament successfully")
    void shouldUpdateTournamentSuccessfully() throws Exception {
        // Given
        when(tournamentService.save(any(Tournament.class))).thenReturn(tournament);

        // When & Then
        mockMvc.perform(post("/tournaments/edit/1")
                .with(csrf())
                .param("name", "Updated Tournament")
                .param("description", "Updated Description")
                .param("date", "2025-08-15")
                .param("time", "14:00")
                .param("location", "Updated Location")
                .param("maxEntrants", "64"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tournaments"));

        verify(tournamentService, times(1)).save(any(Tournament.class));
    }
}
