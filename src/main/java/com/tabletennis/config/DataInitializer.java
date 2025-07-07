package com.tabletennis.config;

import com.tabletennis.entity.User;
import com.tabletennis.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Data initialization component to create default admin user on startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) {
        // Check if admin user already exists
        if (userService.findByUsername("admin").isEmpty()) {
            // Generate a secure random password
            String randomPassword = generateSecurePassword();

            // Create admin user
            userService.createUser("admin", randomPassword, User.Role.ADMIN);

            // Display the password for the admin to see
            log.warn("=====================================");
            log.warn("🔑 ADMIN USER CREATED SUCCESSFULLY");
            log.warn("=====================================");
            log.warn("Username: admin");
            log.warn("Password: {}", randomPassword);
            log.warn("=====================================");
            log.warn("⚠️  IMPORTANT: Save this password securely!");
            log.warn("   This password will not be shown again.");
            log.warn("=====================================");
        } else {
            log.info("ℹ️  Admin user already exists, skipping creation.");
        }
    }

    /**
     * Generate a secure random password with 16 characters
     * Including uppercase, lowercase, numbers, and special characters
     */
    private String generateSecurePassword() {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        String allChars = upperCase + lowerCase + numbers + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // Fill remaining characters randomly
        for (int i = 4; i < 16; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password to randomize the order
        return shuffleString(password.toString(), random);
    }

    /**
     * Shuffle the characters in a string
     */
    private String shuffleString(String input, Random random) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
}
