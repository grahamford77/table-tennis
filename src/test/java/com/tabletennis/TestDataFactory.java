package com.tabletennis;

import com.tabletennis.dto.GameDto;
import com.tabletennis.dto.PlayerDto;
import com.tabletennis.dto.RegistrationDto;
import com.tabletennis.dto.TournamentDto;
import com.tabletennis.dto.TournamentRequest;
import com.tabletennis.entity.Game;
import com.tabletennis.entity.Player;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.entity.User;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Factory class for creating test data objects using Faker
 */
public class TestDataFactory {

    private static final Faker faker = new Faker();

    // Player creation methods
    public static Player createPlayer() {
        var player = new Player();
        player.setId(faker.number().randomNumber());
        player.setFirstName(faker.name().firstName());
        player.setSurname(faker.name().lastName());
        player.setEmail(faker.internet().emailAddress());
        return player;
    }

    public static PlayerDto createPlayerDtoFromPlayer(Player player) {
        var playerDto = new PlayerDto();
        playerDto.setId(player.getId());
        playerDto.setFirstName(player.getFirstName());
        playerDto.setSurname(player.getSurname());
        playerDto.setEmail(player.getEmail());
        return playerDto;
    }

    // Tournament creation methods
    public static Tournament createTournament() {
        var tournament = new Tournament();
        tournament.setId(faker.number().randomNumber());
        tournament.setName(faker.esports().game() + " Championship");
        tournament.setDescription(faker.lorem().sentence(15));
        tournament.setDate(LocalDate.now().plusDays(faker.number().numberBetween(1, 60)));
        tournament.setTime(LocalTime.of(faker.number().numberBetween(9, 18), 0));
        tournament.setLocation(faker.address().cityName() + " Arena");
        tournament.setMaxEntrants(faker.number().numberBetween(16, 128));
        return tournament;
    }

    public static TournamentDto createTournamentDtoFromTournament(Tournament tournament) {
        var tournamentDto = new TournamentDto();
        tournamentDto.setId(tournament.getId());
        tournamentDto.setName(tournament.getName());
        tournamentDto.setDescription(tournament.getDescription());
        tournamentDto.setDate(tournament.getDate());
        tournamentDto.setTime(tournament.getTime());
        tournamentDto.setLocation(tournament.getLocation());
        tournamentDto.setMaxEntrants(tournament.getMaxEntrants());
        return tournamentDto;
    }

    public static TournamentRequest createTournamentRequest() {
        var tournamentRequest = new TournamentRequest();
        tournamentRequest.setName(faker.esports().game() + " Championship");
        tournamentRequest.setDescription(faker.lorem().sentence(15));
        tournamentRequest.setDate(LocalDate.now().plusDays(faker.number().numberBetween(1, 60)));
        tournamentRequest.setTime(LocalTime.of(faker.number().numberBetween(9, 18), 0));
        tournamentRequest.setLocation(faker.address().cityName() + " Arena");
        tournamentRequest.setMaxEntrants(faker.number().numberBetween(16, 128));
        return tournamentRequest;
    }

    // Game creation methods
    public static Game createGame() {
        var tournament = createTournament();
        var player1 = createPlayer();
        var player2 = createPlayer();

        var game = new Game();
        game.setId(faker.number().randomNumber());
        game.setTournament(tournament);
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setGameOrder(faker.number().numberBetween(1, 10));
        game.setPlayer1Score(faker.number().numberBetween(0, 21));
        game.setPlayer2Score(faker.number().numberBetween(0, 21));
        game.setPlayedAt(LocalDateTime.now());
        return game;
    }

    public static Game createGameWithTournament(Tournament tournament) {
        var game = createGame();
        game.setTournament(tournament);
        return game;
    }

    public static GameDto createGameDto() {
        var game = createGame();
        var gameDto = new GameDto();
        gameDto.setId(game.getId());
        gameDto.setTournamentId(game.getTournament().getId());
        gameDto.setTournamentName(game.getTournament().getName());
        gameDto.setGameOrder(game.getGameOrder());
        gameDto.setPlayer1Score(game.getPlayer1Score());
        gameDto.setPlayer2Score(game.getPlayer2Score());
        gameDto.setPlayedAt(game.getPlayedAt());
        return gameDto;
    }

    // Registration creation methods
    public static TournamentRegistration createTournamentRegistration() {
        var registration = new TournamentRegistration();
        registration.setId(faker.number().randomNumber());
        registration.setTournament(createTournament());
        registration.setPlayer(createPlayer());
        return registration;
    }

    public static TournamentRegistration createTournamentRegistrationWithTournament(Tournament tournament) {
        var registration = createTournamentRegistration();
        registration.setTournament(tournament);
        return registration;
    }

    public static RegistrationDto createRegistrationDto() {
        var registration = createTournamentRegistration();
        var registrationDto = new RegistrationDto();
        registrationDto.setId(registration.getId());
        registrationDto.setPlayer(createPlayerDtoFromPlayer(registration.getPlayer()));
        registrationDto.setTournament(createTournamentDtoFromTournament(registration.getTournament()));
        registrationDto.setPlayerName(registration.getPlayer().getFullName());
        registrationDto.setPlayerEmail(registration.getPlayer().getEmail());
        registrationDto.setTournamentName(registration.getTournament().getName());
        return registrationDto;
    }

    // User creation methods
    public static User createUser() {
        var user = new User();
        user.setId(faker.number().randomNumber());
        user.setUsername(faker.internet().username());
        user.setPassword(faker.internet().password());
        return user;
    }

    public static List<TournamentRegistration> createTournamentRegistrationsForTournament(Tournament tournament, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createTournamentRegistrationWithTournament(tournament))
                .toList();
    }

    public static List<Game> createGamesForTournament(Tournament tournament, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createGameWithTournament(tournament))
                .toList();
    }

    // Utility methods
    public static Long randomId() {
        return faker.number().randomNumber();
    }

    public static String randomEmail() {
        return faker.internet().emailAddress();
    }

    public static String randomName() {
        return faker.name().firstName();
    }

    public static String randomSurname() {
        return faker.name().lastName();
    }

    public static int randomScore() {
        return faker.number().numberBetween(0, 21);
    }
}
