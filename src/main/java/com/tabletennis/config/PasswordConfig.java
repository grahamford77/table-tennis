package com.tabletennis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for password encoding
 * Separated from SecurityConfig to avoid circular dependencies
 */
@Configuration
public class PasswordConfig {

    /**
     * Password encoder with BCrypt and strength 12 for maximum security
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
