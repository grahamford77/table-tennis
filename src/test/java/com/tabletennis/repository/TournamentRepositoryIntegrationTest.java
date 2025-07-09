package com.tabletennis.repository;

import java.time.LocalDate;

import com.tabletennis.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TournamentRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TournamentRepository tournamentRepository;

    @BeforeEach
    void setUp() {
        // Clean up any existing data before each test
        tournamentRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findAllByOrderByDateAsc_ShouldReturnTournamentsOrderedByDate() {
        // Given
        var tournament1 = TestDataFactory.createTournament();
        tournament1.setDate(LocalDate.now().plusDays(10));

        var tournament2 = TestDataFactory.createTournament();
        tournament2.setDate(LocalDate.now().plusDays(5));

        var tournament3 = TestDataFactory.createTournament();
        tournament3.setDate(LocalDate.now().plusDays(15));

        entityManager.persistAndFlush(tournament1);
        entityManager.persistAndFlush(tournament2);
        entityManager.persistAndFlush(tournament3);

        // When
        var result = tournamentRepository.findAllByOrderByDateAsc();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());

        // Verify ordering: tournament2 (day 5), tournament1 (day 10), tournament3 (day 15)
        assertEquals(tournament2.getId(), result.get(0).getId());
        assertEquals(tournament1.getId(), result.get(1).getId());
        assertEquals(tournament3.getId(), result.get(2).getId());

        assertTrue(result.get(0).getDate().isBefore(result.get(1).getDate()));
        assertTrue(result.get(1).getDate().isBefore(result.get(2).getDate()));
    }

    @Test
    void findAllByOrderByDateAsc_WithEmptyDatabase_ShouldReturnEmptyList() {
        // When
        var result = tournamentRepository.findAllByOrderByDateAsc();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
