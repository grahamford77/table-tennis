package com.tabletennis.mapping;

import com.tabletennis.TestDataFactory;
import com.tabletennis.dto.PlayerDto;
import com.tabletennis.dto.RegistrationDto;
import com.tabletennis.entity.Player;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationMapperTest {

    @Mock
    private PlayerMapper playerMapper;

    @Mock
    private TournamentMapper tournamentMapper;

    private RegistrationMapper registrationMapper;

    private TournamentRegistration registration;
    private Player player;
    private PlayerDto playerDto;

    @BeforeEach
    void setUp() {
        registrationMapper = new RegistrationMapper(playerMapper, tournamentMapper);

        // Create test data using TestDataFactory
        Tournament tournament = TestDataFactory.createTournament();
        player = TestDataFactory.createPlayer();
        registration = TestDataFactory.createTournamentRegistrationWithTournament(tournament);
        registration.setPlayer(player);
        playerDto = TestDataFactory.createPlayerDtoFromPlayer(player);
    }

    @Test
    void convertToDto_ShouldMapRegistrationToDto() {
        // Given
        when(playerMapper.convertToDto(player)).thenReturn(playerDto);
        when(tournamentMapper.convertToDto(registration.getTournament()))
                .thenReturn(TestDataFactory.createTournamentDtoFromTournament(registration.getTournament()));

        // When
        RegistrationDto result = registrationMapper.convertToDto(registration);

        // Then
        assertNotNull(result);
        assertEquals(registration.getId(), result.getId());
        assertEquals(registration.getTournament().getId(), result.getTournament().getId());
        assertEquals(registration.getPlayer().getId(), result.getPlayer().getId());
        assertEquals(playerDto, result.getPlayer());
    }

    @Test
    void convertToDto_WithNullRegistration_ShouldThrowException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> registrationMapper.convertToDto(null));
    }

    @Test
    void convertToDto_WithNullPlayer_ShouldHandleGracefully() {
        // Given
        registration.setPlayer(null);

        // When & Then
        assertThrows(NullPointerException.class, () -> registrationMapper.convertToDto(registration));
    }

    @Test
    void convertToDto_WithNullTournament_ShouldHandleGracefully() {
        // Given
        registration.setTournament(null);

        // When & Then
        assertThrows(NullPointerException.class, () -> registrationMapper.convertToDto(registration));
    }
}
