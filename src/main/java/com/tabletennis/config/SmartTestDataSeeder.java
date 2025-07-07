package com.tabletennis.config;

import com.tabletennis.entity.Player;
import com.tabletennis.entity.Tournament;
import com.tabletennis.entity.TournamentRegistration;
import com.tabletennis.entity.User;
import com.tabletennis.service.PlayerService;
import com.tabletennis.service.RegistrationService;
import com.tabletennis.service.TournamentService;
import com.tabletennis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Smart data seeder that only creates data when tables are empty
 */
@Component
@Order(2) // Run after DataInitializer
public class SmartTestDataSeeder implements CommandLineRunner {

    private final UserService userService;
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
        "Green", "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell",
        "Carter", "Roberts", "Gomez", "Phillips", "Evans", "Turner", "Diaz", "Parker",
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

    @Autowired
    public SmartTestDataSeeder(UserService userService, PlayerService playerService,
                              TournamentService tournamentService, RegistrationService registrationService) {
        this.userService = userService;
        this.playerService = playerService;
        this.tournamentService = tournamentService;
        this.registrationService = registrationService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔍 Checking for existing data...");

        boolean playersEmpty = playerService.findAll().isEmpty();
        boolean tournamentsEmpty = tournamentService.findAllOrderByDate().isEmpty();
        boolean registrationsEmpty = registrationService.findAll().isEmpty();

        if (!playersEmpty && !tournamentsEmpty && !registrationsEmpty) {
            System.out.println("📊 Data already exists. Skipping test data generation.");
            return;
        }

        System.out.println("🎲 Generating test data for empty tables...");

        List<Player> players = new ArrayList<>();
        List<Tournament> tournaments = new ArrayList<>();

        // Create test users (always check these)
        createTestUsers();

        // Create players if table is empty
        if (playersEmpty) {
            players = createTestPlayers();
            System.out.println("👥 Created " + players.size() + " players");
        } else {
            players = playerService.findAll();
            System.out.println("👥 Using existing " + players.size() + " players");
        }

        // Create tournaments if table is empty
        if (tournamentsEmpty) {
            tournaments = createTestTournaments();
            System.out.println("🏆 Created " + tournaments.size() + " tournaments");
        } else {
            tournaments = tournamentService.findAllOrderByDate();
            System.out.println("🏆 Using existing " + tournaments.size() + " tournaments");
        }

        // Create registrations if table is empty and we have players and tournaments
        if (registrationsEmpty && !players.isEmpty() && !tournaments.isEmpty()) {
            createTestRegistrations(players, tournaments);
            long registrationCount = registrationService.findAll().size();
            System.out.println("📝 Created " + registrationCount + " registrations");
        } else if (!registrationsEmpty) {
            System.out.println("📝 Using existing registrations");
        }

        System.out.println("✅ Test data setup completed!");
    }

    private void createTestUsers() {
        // Create test users with known passwords for testing
        if (userService.findByUsername("testuser").isEmpty()) {
            userService.createUser("testuser", "password123", User.Role.USER);
            System.out.println("👤 Created test user: testuser / password123");
        }

        if (userService.findByUsername("testadmin").isEmpty()) {
            userService.createUser("testadmin", "admin123", User.Role.ADMIN);
            System.out.println("👤 Created test admin: testadmin / admin123");
        }
    }

    private List<Player> createTestPlayers() {
        List<Player> players = new ArrayList<>();
        int playerCount = 20 + random.nextInt(20); // 20-40 players

        System.out.println("👥 Generating " + playerCount + " test players...");

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
        int tournamentCount = 6 + random.nextInt(6); // 6-12 tournaments

        System.out.println("🏆 Generating " + tournamentCount + " test tournaments...");

        for (int i = 0; i < tournamentCount; i++) {
            String name = generateUniqueTournamentName();
            String description = generateTournamentDescription(name);
            LocalDate date = generateFutureDate();
            LocalTime time = generateRandomTime();
            String location = locations.get(random.nextInt(locations.size()));
            Integer maxEntrants = 8 + random.nextInt(24); // 8-32 players

            Tournament tournament = new Tournament(name, description, date, time, location, maxEntrants);
            tournament = tournamentService.save(tournament);
            tournaments.add(tournament);
        }

        return tournaments;
    }

    private void createTestRegistrations(List<Player> players, List<Tournament> tournaments) {
        System.out.println("📝 Generating tournament registrations...");

        for (Tournament tournament : tournaments) {
            // Randomly register 30-70% of players for each tournament
            double registrationRate = 0.3 + random.nextDouble() * 0.4;
            int maxRegistrations = Math.min(
                tournament.getMaxEntrants(),
                (int) (players.size() * registrationRate)
            );

            // Ensure at least 2 players per tournament for testing
            int registrationCount = Math.max(2, maxRegistrations);

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
        String[] domains = {"rightmove.co.uk", "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "company.com"};
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
        } while (playerService.findByEmail(baseEmail).isPresent() && attempt < 10);

        return baseEmail;
    }

    private String generateUniqueTournamentName() {
        String baseName;
        int attempt = 0;

        do {
            baseName = tournamentNames.get(random.nextInt(tournamentNames.size()));
            if (attempt > 0) {
                baseName += " " + (2025 + attempt); // Add year to make it unique
            }
            attempt++;
        } while (tournamentNameExists(baseName) && attempt < 10);

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
        int daysFromNow = 1 + random.nextInt(120);
        return LocalDate.now().plusDays(daysFromNow);
    }

    private LocalTime generateRandomTime() {
        // Generate times during typical tournament hours (9 AM - 7 PM)
        int hour = 9 + random.nextInt(11); // 9-19 (7 PM)
        int minute = random.nextBoolean() ? 0 : 30; // Either :00 or :30
        return LocalTime.of(hour, minute);
    }
}
