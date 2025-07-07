package com.tabletennis.config;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.tabletennis.entity.Player;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.service.PlayerService;
import com.tabletennis.service.RegistrationService;
import com.tabletennis.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Smart data seeder that only creates data when tables are empty
 */
@Component
@Order(2) // Run after DataInitializer
@RequiredArgsConstructor
@Slf4j
public class SmartTestDataSeeder implements CommandLineRunner {

    private static final int BASE_YEAR = 2025;
    private static final int MIN_PLAYERS = 20;
    private static final int PLAYER_RANGE = 20;
    private static final int MIN_TOURNAMENTS = 6;
    private static final int TOURNAMENT_RANGE = 6;
    private static final int MIN_ENTRANTS = 8;
    private static final int ENTRANT_RANGE = 24;
    private static final int MIN_FUTURE_DAYS = 1;
    private static final int FUTURE_DAYS_RANGE = 120;
    private static final int MIN_TOURNAMENT_HOUR = 9;
    private static final int TOURNAMENT_HOUR_RANGE = 11;
    private static final double MIN_REGISTRATION_RATE = 0.3;
    private static final double REGISTRATION_RATE_RANGE = 0.4;
    private static final int MIN_PLAYERS_PER_TOURNAMENT = 2;
    private static final int MAX_EMAIL_ATTEMPTS = 10;
    private static final int MAX_NAME_ATTEMPTS = 10;

    private final PlayerService playerService;
    private final TournamentService tournamentService;
    private final RegistrationService registrationService;

    private final Random random = new SecureRandom();

    // Sample data for generating realistic test data
    private final List<String> firstNames = Arrays.asList(
            "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda",
            "William", "Elizabeth", "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica",
            "Thomas", "Sarah", "Christopher", "Karen", "Charles", "Nancy", "Daniel", "Lisa",
            "Matthew", "Betty", "Anthony", "Helen", "Mark", "Sandra", "Donald", "Donna",
            "Steven", "Carol", "Paul", "Ruth", "Andrew", "Sharon", "Joshua", "Michelle",
            "Kenneth", "Laura", "Kevin", "Sarah", "Brian", "Kimberly", "George", "Deborah",
            "Timothy", "Dorothy", "Ronald", "Lisa", "Jason", "Nancy", "Edward", "Karen",
            "Jeffrey", "Betty", "Ryan", "Helen", "Jacob", "Sandra", "Gary", "Donna",
            "Nicholas", "Carol", "Eric", "Ruth", "Jonathan", "Sharon", "Stephen", "Michelle",
            "Emma", "Olivia", "Ava", "Isabella", "Sophia", "Charlotte", "Mia", "Amelia",
            "Harper", "Evelyn", "Abigail", "Emily", "Ella", "Madison", "Scarlett", "Victoria",
            "Aria", "Grace", "Chloe", "Camila", "Penelope", "Riley", "Layla", "Lillian"
    );

    private final List<String> surnames = Arrays.asList(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
            "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas",
            "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", "White",
            "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson", "Walker", "Young",
            "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores",
            "Green", "AdAMS", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell",
            "Carter", "Roberts", "Gomez", "Phillips", "EvANS", "Turner", "Diaz", "Parker",
            "Cruz", "Edwards", "Collins", "Reyes", "Stewart", "Morris", "Morales", "Murphy",
            "Cook", "Rogers", "Gutierrez", "Ortiz", "Morgan", "Cooper", "Peterson", "Bailey",
            "Reed", "Kelly", "Howard", "Ramos", "Kim", "Cox", "Ward", "Richardson", "Watson",
            "Brooks", "Chavez", "Wood", "James", "Bennett", "Gray", "Mendoza", "Ruiz"
    );

    private final List<String> tournamentNames = Arrays.asList(
            "Spring Championship", "Summer Slam", "Autumn Masters", "Winter Classic",
            "Rightmove Corporate Cup", "Friday Night League", "Weekend Warriors",
            "Beginner's Bonanza", "Intermediate Challenge", "Advanced Masters",
            "Pro Tournament", "Monthly Meetup", "Quarterly Championship",
            "Annual Grand Prix", "Team Building Tournament", "Charity Challenge",
            "Lunch Break League", "After Work Special", "Saturday Showdown",
            "Sunday Spectacular", "Holiday Tournament", "New Year Cup",
            "Office Olympics", "Desk Derby", "Ping Pong Playoffs", "Paddle Battle",
            "Table Tennis Titans", "Corporate Clash", "Workplace Warriors",
            "Coffee Break Competition", "Executive Tournament", "Staff Showdown"
    );

    private final List<String> locations = Arrays.asList(
            "Rightmove Head Office - Sports Hall", "Rightmove Canteen Area",
            "Rightmove Conference Room A", "Rightmove Break Room", "Rightmove Training Center",
            "Community Center - Main Hall", "Local Sports Club", "Recreation Center",
            "University Sports Complex", "Sports Pavilion", "Town Hall - Function Room",
            "Leisure Center", "School Gymnasium", "Hotel Conference Center",
            "Corporate Wellness Center", "Employee Lounge", "Meeting Room B (converted)",
            "Outdoor Covered Area", "Staff Kitchen (extended)", "Innovation Lab"
    );

    @Override
    public void run(String... args) {
        log.info("🔍 Checking for existing data...");

        boolean playersEmpty = playerService.findAll().isEmpty();
        boolean tournamentsEmpty = tournamentService.findAllOrderByDate().isEmpty();
        boolean registrationsEmpty = registrationService.findAll().isEmpty();

        if (!playersEmpty && !tournamentsEmpty && !registrationsEmpty) {
            log.info("📊 Data already exists. Skipping test data generation.");
            return;
        }

        log.info("🎲 Generating test data for empty tables...");

        List<Player> players;
        List<Tournament> tournaments;

        // Create players if table is empty
        if (playersEmpty) {
            players = createTestPlayers();
            log.info("👥 Created {} players", players.size());
        } else {
            players = playerService.findAll();
            log.info("👥 Using existing {} players", players.size());
        }

        // Create tournaments if table is empty
        if (tournamentsEmpty) {
            tournaments = createTestTournaments();
            log.info("🏆 Created {} tournaments", tournaments.size());
        } else {
            tournaments = tournamentService.findAllOrderByDate();
            log.info("🏆 Using existing {} tournaments", tournaments.size());
        }

        // Create registrations if table is empty and we have players and tournaments
        if (registrationsEmpty && !players.isEmpty() && !tournaments.isEmpty()) {
            createTestRegistrations(players, tournaments);
            long registrationCount = registrationService.findAll().size();
            log.info("📝 Created {} registrations", registrationCount);
        } else if (!registrationsEmpty) {
            log.info("📝 Using existing registrations");
        }

        log.info("✅ Test data setup completed!");
    }

    private List<Player> createTestPlayers() {
        List<Player> players = new ArrayList<>();
        int playerCount = MIN_PLAYERS + random.nextInt(PLAYER_RANGE); // 20-40 players

        log.info("👥 Generating {} test players...", playerCount);

        for (int i = 0; i < playerCount; i++) {
            String firstName = firstNames.get(random.nextInt(firstNames.size()));
            String surname = surnames.get(random.nextInt(surnames.size()));
            String email = generateUniqueEmail(firstName, surname);

            Player player = playerService.findOrCreatePlayer(firstName, surname, email);
            players.add(player);
        }

        return players;
    }

    private List<Tournament> createTestTournaments() {
        List<Tournament> tournaments = new ArrayList<>();
        int tournamentCount = MIN_TOURNAMENTS + random.nextInt(TOURNAMENT_RANGE); // 6-12 tournaments

        log.info("🏆 Generating {} test tournaments...", tournamentCount);

        for (int i = 0; i < tournamentCount; i++) {
            String name = generateUniqueTournamentName();
            String description = generateTournamentDescription(name);
            LocalDate date = generateFutureDate();
            LocalTime time = generateRandomTime();
            String location = locations.get(random.nextInt(locations.size()));
            Integer maxEntrants = MIN_ENTRANTS + random.nextInt(ENTRANT_RANGE); // 8-32 players

            Tournament tournament = new Tournament(name, description, date, time, location, maxEntrants);
            tournament = tournamentService.save(tournament);
            tournaments.add(tournament);
        }

        return tournaments;
    }

    private void createTestRegistrations(List<Player> players, List<Tournament> tournaments) {
        log.info("📝 Generating tournament registrations...");

        for (Tournament tournament : tournaments) {
            // Randomly register 30-70% of players for each tournament
            double registrationRate = MIN_REGISTRATION_RATE + random.nextDouble() * REGISTRATION_RATE_RANGE;
            int maxRegistrations = Math.min(
                    tournament.getMaxEntrants(),
                    (int) (players.size() * registrationRate)
            );

            // Ensure at least 2 players per tournament for testing
            int registrationCount = Math.max(MIN_PLAYERS_PER_TOURNAMENT, maxRegistrations);

            // Shuffle players and take the first N
            List<Player> shuffledPlayers = new ArrayList<>(players);
            java.util.Collections.shuffle(shuffledPlayers, random);

            for (int i = 0; i < Math.min(registrationCount, shuffledPlayers.size()); i++) {
                Player player = shuffledPlayers.get(i);
                TournamentRegistration registration = new TournamentRegistration(player, tournament);
                registrationService.save(registration);
            }
        }
    }

    private String generateUniqueEmail(String firstName, String surname) {
        String[] domains = { "rightmove.co.uk", "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "company.com" };
        String domain = domains[random.nextInt(domains.length)];

        String baseEmail;
        int attempt = 0;

        do {
            String[] formats = {
                    firstName.toLowerCase() + "." + surname.toLowerCase(),
                    firstName.toLowerCase() + surname.toLowerCase(),
                    firstName.toLowerCase() + "_" + surname.toLowerCase(),
                    firstName.toLowerCase().charAt(0) + surname.toLowerCase(),
                    firstName.toLowerCase() + random.nextInt(1000),
                    surname.toLowerCase() + random.nextInt(100)
            };

            String localPart = formats[random.nextInt(formats.length)];
            if (attempt > 0) {
                localPart += attempt; // Add attempt number to make it unique
            }
            baseEmail = localPart + "@" + domain;
            attempt++;
        } while (playerService.findByEmail(baseEmail).isPresent() && attempt < MAX_EMAIL_ATTEMPTS);

        return baseEmail;
    }

    private String generateUniqueTournamentName() {
        String baseName;
        int attempt = 0;

        do {
            baseName = tournamentNames.get(random.nextInt(tournamentNames.size()));
            if (attempt > 0) {
                baseName += " " + (BASE_YEAR + attempt); // Add year to make it unique
            }
            attempt++;
        } while (tournamentNameExists(baseName) && attempt < MAX_NAME_ATTEMPTS);

        return baseName;
    }

    private boolean tournamentNameExists(String name) {
        return tournamentService.findAllOrderByDate().stream()
                .anyMatch(tournament -> tournament.getName().equals(name));
    }

    private String generateTournamentDescription(String name) {
        String[] descriptions = {
                "Join us for an exciting " + name + " tournament! Open to players of all skill levels.",
                "Experience the thrill of competitive table tennis in our " + name + " event.",
                "Don't miss this fantastic opportunity to compete in the " + name + "!",
                "Battle it out with fellow colleagues in this thrilling " + name + " competition.",
                "Test your skills against the best in our " + name + " tournament.",
                "Round-robin format ensures everyone gets plenty of games in this " + name + " event.",
                "Whether you're a beginner or expert, the " + name + " has something for everyone!",
                "Corporate team building meets competitive sport in this " + name + " tournament.",
                "Bring your A-game to the " + name + " and show your colleagues what you're made of!",
                "Fun, competitive, and exciting - the " + name + " is not to be missed!"
        };

        return descriptions[random.nextInt(descriptions.length)];
    }

    private LocalDate generateFutureDate() {
        // Generate dates between 1 day and 120 days in the future
        int daysFromNow = MIN_FUTURE_DAYS + random.nextInt(FUTURE_DAYS_RANGE);
        return LocalDate.now().plusDays(daysFromNow);
    }

    private LocalTime generateRandomTime() {
        // Generate times during typical tournament hours (9 AM - 7 PM)
        int hour = MIN_TOURNAMENT_HOUR + random.nextInt(TOURNAMENT_HOUR_RANGE); // 9-19 (7 PM)
        int minute = random.nextBoolean() ? 0 : 30; // Either :00 or :30
        return LocalTime.of(hour, minute);
    }
}
