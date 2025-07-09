package com.tabletennis.repository;

import com.tabletennis.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TournamentRegistrationRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TournamentRegistrationRepository registrationRepository;

    @Test
    void findByTournament_WhenRegistrationsExist_ShouldReturnRegistrations() {
        // Given
        var tournament = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament);

        var registration1 = TestDataFactory.createTournamentRegistrationWithTournament(tournament);
        var registration2 = TestDataFactory.createTournamentRegistrationWithTournament(tournament);

        // Persist players first before persisting registrations
        entityManager.persistAndFlush(registration1.getPlayer());
        entityManager.persistAndFlush(registration2.getPlayer());
        entityManager.persistAndFlush(registration1);
        entityManager.persistAndFlush(registration2);

        // When
        var result = registrationRepository.findByTournament(tournament);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getId().equals(registration1.getId())));
        assertTrue(result.stream().anyMatch(r -> r.getId().equals(registration2.getId())));
    }

    @Test
    void findByTournament_WhenNoRegistrationsExist_ShouldReturnEmptyList() {
        // Given
        var tournament = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament);

        // When
        var result = registrationRepository.findByTournament(tournament);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByTournament_WithDifferentTournament_ShouldReturnEmptyList() {
        // Given
        var tournament1 = TestDataFactory.createTournament();
        var tournament2 = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament1);
        entityManager.persistAndFlush(tournament2);

        var registration = TestDataFactory.createTournamentRegistrationWithTournament(tournament1);
        // Persist player first before persisting registration
        entityManager.persistAndFlush(registration.getPlayer());
        entityManager.persistAndFlush(registration);

        // When
        var result = registrationRepository.findByTournament(tournament2);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByTournamentId_WhenRegistrationsExist_ShouldReturnRegistrations() {
        // Given
        var tournament = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament);

        var registration1 = TestDataFactory.createTournamentRegistrationWithTournament(tournament);
        var registration2 = TestDataFactory.createTournamentRegistrationWithTournament(tournament);

        // Persist players first before persisting registrations
        entityManager.persistAndFlush(registration1.getPlayer());
        entityManager.persistAndFlush(registration2.getPlayer());
        entityManager.persistAndFlush(registration1);
        entityManager.persistAndFlush(registration2);

        // When
        var result = registrationRepository.findByTournamentId(tournament.getId());

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getId().equals(registration1.getId())));
        assertTrue(result.stream().anyMatch(r -> r.getId().equals(registration2.getId())));
    }

    @Test
    void findByTournamentId_WhenNoRegistrationsExist_ShouldReturnEmptyList() {
        // Given
        var tournament = TestDataFactory.createTournament();
        entityManager.persistAndFlush(tournament);

        // When
        var result = registrationRepository.findByTournamentId(tournament.getId());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByTournamentId_WithNonExistentTournamentId_ShouldReturnEmptyList() {
        // Given
        var nonExistentTournamentId = 999999L;

        // When
        var result = registrationRepository.findByTournamentId(nonExistentTournamentId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
