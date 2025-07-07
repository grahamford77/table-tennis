package com.tabletennis.config;

import com.tabletennis.entity.User;
import com.tabletennis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Data initialization component to create default admin user
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        if (userService.findByUsername("graham").isEmpty()) {
            // Generate a secure random password
            String randomPassword = generateSecurePassword();

            // Create admin user
            userService.createUser("graham", randomPassword, User.Role.ADMIN);

            // Log the password for the user (in production, this would be sent securely)
            System.out.println("=====================================");
            System.out.println("ADMIN USER CREATED SUCCESSFULLY");
            System.out.println("Username: graham");
            System.out.println("Password: " + randomPassword);
            System.out.println("=====================================");
            System.out.println("IMPORTANT: Save this password securely!");
            System.out.println("=====================================");
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
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }
}
