package com.tabletennis.service;

import com.tabletennis.TestDataFactory;
import com.tabletennis.dto.RegistrationDto;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.mapping.RegistrationMapper;
import com.tabletennis.repository.TournamentRegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private TournamentRegistrationRepository registrationRepository;

    @Mock
    private RegistrationMapper registrationMapper;

    private RegistrationService registrationService;

    private Tournament tournament;
    private TournamentRegistration registration;
    private RegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationService(registrationRepository, registrationMapper);

        // Create test data using TestDataFactory
        tournament = TestDataFactory.createTournament();
        registration = TestDataFactory.createTournamentRegistrationWithTournament(tournament);
        registrationDto = TestDataFactory.createRegistrationDto();
    }

    @Test
    void findAllDto_ShouldReturnRegistrationDtos() {
        // Given
        var registrations = List.of(registration);
        when(registrationRepository.findAll()).thenReturn(registrations);
        when(registrationMapper.convertToDto(registration)).thenReturn(registrationDto);

        // When
        var result = registrationService.findAllDto();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(registrationDto, result.getFirst());
        verify(registrationRepository).findAll();
        verify(registrationMapper).convertToDto(registration);
    }

    @Test
    void findByTournament_ShouldReturnRegistrationsForTournament() {
        // Given
        var registrations = TestDataFactory.createTournamentRegistrationsForTournament(tournament, 3);
        when(registrationRepository.findByTournament(tournament)).thenReturn(registrations);

        // When
        var result = registrationService.findByTournament(tournament);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(registrationRepository).findByTournament(tournament);
    }

    @Test
    void save_ShouldSaveRegistration() {
        // When
        registrationService.save(registration);

        // Then
        verify(registrationRepository).save(registration);
    }

    @Test
    void findAll_ShouldReturnAllRegistrations() {
        // Given
        var registrations = List.of(registration);
        when(registrationRepository.findAll()).thenReturn(registrations);

        // When
        var result = registrationService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(registration, result.getFirst());
        verify(registrationRepository).findAll();
    }

    @Test
    void getTournamentCounts_ShouldReturnCountsGroupedByTournamentName() {
        // Given
        List<TournamentRegistration> registrations = List.of(registration);
        when(registrationRepository.findAll()).thenReturn(registrations);

        // When
        var result = registrationService.getTournamentCounts();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(registration.getTournament().getName()));
        assertEquals(1L, result.get(registration.getTournament().getName()));
        verify(registrationRepository).findAll();
    }

    @Test
    void findByTournamentIdDto_ShouldReturnRegistrationDtosForTournament() {
        // Given
        var tournamentId = TestDataFactory.randomId();
        var registrations = List.of(registration);
        when(registrationRepository.findByTournamentId(tournamentId)).thenReturn(registrations);
        when(registrationMapper.convertToDto(registration)).thenReturn(registrationDto);

        // When
        var result = registrationService.findByTournamentIdDto(tournamentId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(registrationDto, result.getFirst());
        verify(registrationRepository).findByTournamentId(tournamentId);
        verify(registrationMapper).convertToDto(registration);
    }
}
