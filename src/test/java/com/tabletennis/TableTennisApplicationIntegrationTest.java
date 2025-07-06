package com.tabletennis;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.repository.TournamentRegistrationRepository;
import com.tabletennis.repository.TournamentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the complete Table Tennis Tournament application.
 * Tests the full application context and database interactions.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@DisplayName("Table Tennis Application Integration Tests")
class TableTennisApplicationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentRegistrationRepository registrationRepository;

    @Test
    @DisplayName("Should start application context successfully")
    void shouldStartApplicationContext() {
        // Test passes if application context loads without errors
        assertNotNull(restTemplate);
        assertNotNull(tournamentRepository);
        assertNotNull(registrationRepository);
    }

    @Test
    @DisplayName("Should complete full tournament registration workflow")
    void shouldCompleteFullTournamentRegistrationWorkflow() throws Exception {
        // Step 1: Create a tournament
        Tournament tournament = new Tournament(
                "Integration Test Tournament",
                "A tournament for integration testing",
                LocalDate.of(2025, 9, 15),
                LocalTime.of(15, 0),
                "Test Venue",
                16
        );
        tournament = tournamentRepository.save(tournament);
        assertNotNull(tournament.getId());

        // Step 2: Register a player for the tournament
        restTemplate.postForEntity("/register",
                new TournamentRegistration("Integration", "Tester", "integration@test.com", tournament),
                String.class);

        // Step 3: Verify registration was saved
        long registrationCount = registrationRepository.count();
        assertEquals(1, registrationCount);

        TournamentRegistration savedRegistration = registrationRepository.findAll().get(0);
        assertEquals("Integration", savedRegistration.getFirstName());
        assertEquals("Tester", savedRegistration.getSurname());
        assertEquals("integration@test.com", savedRegistration.getEmail());
        assertEquals(tournament.getId(), savedRegistration.getTournament().getId());

        // Step 4: View registrations page
        ResponseEntity<String> response = restTemplate.getForEntity("/registrations", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("registrations"));
        assertTrue(response.getBody().contains("tournamentCounts"));
    }

    @Test
    @DisplayName("Should handle tournament CRUD operations")
    void shouldHandleTournamentCrudOperations() throws Exception {
        // Create tournament
        restTemplate.postForEntity("/tournaments/create"
                + "?name=CRUD Test Tournament"
                + "&description=Testing CRUD operations"
                + "&date=2025-10-01"
                + "&time=18:00"
                + "&location=CRUD Test Location"
                + "&maxEntrants=8", null, String.class);

        // Verify tournament was created
        Tournament savedTournament = tournamentRepository.findAll().stream()
                .filter(t -> "CRUD Test Tournament".equals(t.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(savedTournament);
        assertEquals("Testing CRUD operations", savedTournament.getDescription());

        // Update tournament
        restTemplate.postForEntity("/tournaments/edit/" + savedTournament.getId()
                + "?name=Updated CRUD Tournament"
                + "&description=Updated description"
                + "&date=2025-10-02"
                + "&time=19:00"
                + "&location=Updated Location"
                + "&maxEntrants=16", null, String.class);

        // Verify tournament was updated
        Tournament updatedTournament = tournamentRepository.findById(savedTournament.getId()).orElse(null);
        assertNotNull(updatedTournament);
        assertEquals("Updated CRUD Tournament", updatedTournament.getName());
        assertEquals("Updated description", updatedTournament.getDescription());
        assertEquals(16, updatedTournament.getMaxEntrants());
    }

    @Test
    @DisplayName("Should validate registration form correctly")
    void shouldValidateRegistrationFormCorrectly() throws Exception {
        // First create a tournament for testing
        Tournament tournament = new Tournament(
                "Validation Test Tournament",
                "A tournament for validation testing",
                LocalDate.of(2025, 8, 15),
                LocalTime.of(14, 0),
                "Validation Test Venue",
                8
        );
        tournament = tournamentRepository.save(tournament);
        assertNotNull(tournament.getId());

        // Test with missing required fields
        ResponseEntity<String> response = restTemplate.postForEntity("/register"
                + "?firstName="
                + "&surname="
                + "&email="
                + "&tournamentId=", null, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("registration"));

        // Test with invalid email but valid tournament ID
        response = restTemplate.postForEntity("/register"
                + "?firstName=Test"
                + "&surname=User"
                + "&email=invalid-email"
                + "&tournamentId=" + tournament.getId(), null, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("registration"));

        // Verify no registrations were saved due to validation errors
        assertEquals(0, registrationRepository.count());
    }
}
