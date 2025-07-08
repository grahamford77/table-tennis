package com.tabletennis.mapping;

import com.tabletennis.TestDataFactory;
import com.tabletennis.dto.RegistrationDto;
import com.tabletennis.dto.TournamentDto;
import com.tabletennis.entity.Tournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TournamentMapperTest {

    private TournamentMapper tournamentMapper;
    private Tournament tournament;
    private List<RegistrationDto> registrationDtos;

    @BeforeEach
    void setUp() {
        tournamentMapper = new TournamentMapper();

        // Create test data using TestDataFactory
        tournament = TestDataFactory.createTournament();
        registrationDtos = List.of(
            TestDataFactory.createRegistrationDto(),
            TestDataFactory.createRegistrationDto(),
            TestDataFactory.createRegistrationDto()
        );
    }

    @Test
    void convertToDto_ShouldMapTournamentToDto() {
        // Given
        boolean isStarted = false;

        // When
        TournamentDto result = tournamentMapper.convertToDto(tournament, registrationDtos, isStarted);

        // Then
        assertNotNull(result);
        assertEquals(tournament.getId(), result.getId());
        assertEquals(tournament.getName(), result.getName());
        assertEquals(tournament.getDescription(), result.getDescription());
        assertEquals(tournament.getDate(), result.getDate());
        assertEquals(tournament.getTime(), result.getTime());
        assertEquals(tournament.getLocation(), result.getLocation());
        assertEquals(tournament.getMaxEntrants(), result.getMaxEntrants());
        assertEquals(registrationDtos, result.getRegistrations());
        assertEquals(isStarted, result.isStarted());
    }

    @Test
    void convertToDto_WithoutRegistrations_ShouldMapTournamentToDto() {
        // Given
        boolean isStarted = true;

        // When
        TournamentDto result = tournamentMapper.convertToDto(tournament, List.of(), isStarted);

        // Then
        assertNotNull(result);
        assertEquals(tournament.getId(), result.getId());
        assertEquals(tournament.getName(), result.getName());
        assertEquals(tournament.getDescription(), result.getDescription());
        assertEquals(tournament.getDate(), result.getDate());
        assertEquals(tournament.getTime(), result.getTime());
        assertEquals(tournament.getLocation(), result.getLocation());
        assertEquals(tournament.getMaxEntrants(), result.getMaxEntrants());
        assertTrue(result.getRegistrations().isEmpty());
        assertTrue(result.isStarted());
    }

    @Test
    void convertToDto_SimpleMapping_ShouldMapTournamentToDto() {
        // When
        TournamentDto result = tournamentMapper.convertToDto(tournament);

        // Then
        assertNotNull(result);
        assertEquals(tournament.getId(), result.getId());
        assertEquals(tournament.getName(), result.getName());
        assertEquals(tournament.getDescription(), result.getDescription());
        assertEquals(tournament.getDate(), result.getDate());
        assertEquals(tournament.getTime(), result.getTime());
        assertEquals(tournament.getLocation(), result.getLocation());
        assertEquals(tournament.getMaxEntrants(), result.getMaxEntrants());
        assertNull(result.getRegistrations());
        assertFalse(result.isStarted());
    }

    @Test
    void convertToDto_WithNullTournament_ShouldThrowException() {
        // When & Then
        assertThrows(NullPointerException.class, () ->
            tournamentMapper.convertToDto(null, registrationDtos, false));
    }

    @Test
    void convertToDto_WithNullRegistrations_ShouldHandleGracefully() {
        // When
        TournamentDto result = tournamentMapper.convertToDto(tournament, null, false);

        // Then
        assertNotNull(result);
        assertEquals(tournament.getId(), result.getId());
        assertNull(result.getRegistrations());
        assertFalse(result.isStarted());
    }

    @Test
    void convertToEntity_ShouldMapDtoToEntity() {
        // Given
        var tournamentDto = TestDataFactory.createTournamentDtoFromTournament(tournament);

        // When
        Tournament result = tournamentMapper.convertToEntity(tournamentDto);

        // Then
        assertNotNull(result);
        assertEquals(tournamentDto.getId(), result.getId());
        assertEquals(tournamentDto.getName(), result.getName());
        assertEquals(tournamentDto.getDescription(), result.getDescription());
        assertEquals(tournamentDto.getDate(), result.getDate());
        assertEquals(tournamentDto.getTime(), result.getTime());
        assertEquals(tournamentDto.getLocation(), result.getLocation());
        assertEquals(tournamentDto.getMaxEntrants(), result.getMaxEntrants());
    }

    @Test
    void convertToEntity_WithNullDto_ShouldThrowException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> tournamentMapper.convertToEntity(null));
    }
}
