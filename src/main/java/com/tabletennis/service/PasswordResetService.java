package com.tabletennis.service;

import com.tabletennis.dto.ForgotPasswordRequest;
import com.tabletennis.dto.PasswordResetRequest;
import com.tabletennis.entity.User;
import com.tabletennis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for handling password reset functionality
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Initiates password reset process by generating token and sending email
     */
    @Transactional
    public boolean initiatePasswordReset(ForgotPasswordRequest request) {
        var userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            log.warn("Password reset requested for non-existent email: {}", request.getEmail());
            // Return true for security reasons - don't reveal if email exists
            return true;
        }

        var user = userOptional.get();
        var resetToken = UUID.randomUUID().toString();
        var expiryTime = LocalDateTime.now().plusHours(1); // Token expires in 1 hour

        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(expiryTime);
        userRepository.save(user);

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
            log.info("Password reset email sent for user: {}", user.getUsername());
            return true;
        } catch (Exception e) {
            log.error("Failed to send password reset email for user: {}", user.getUsername(), e);
            // Clear the token if email sending failed
            user.clearPasswordResetToken();
            userRepository.save(user);
            return false;
        }
    }

    /**
     * Validates password reset token
     */
    public Optional<User> validateResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return Optional.empty();
        }

        var userOptional = userRepository.findByPasswordResetToken(token);

        if (userOptional.isEmpty()) {
            log.warn("Invalid password reset token attempted: {}", token);
            return Optional.empty();
        }

        var user = userOptional.get();
        if (!user.isPasswordResetTokenValid()) {
            log.warn("Expired password reset token attempted for user: {}", user.getUsername());
            // Clear expired token
            user.clearPasswordResetToken();
            userRepository.save(user);
            return Optional.empty();
        }

        return userOptional;
    }

    /**
     * Resets user password using valid token
     */
    @Transactional
    public boolean resetPassword(PasswordResetRequest request) {
        if (request.arePasswordsDifferent()) {
            return false;
        }

        var userOptional = validateResetToken(request.getToken());
        if (userOptional.isEmpty()) {
            return false;
        }

        var user = userOptional.get();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.clearPasswordResetToken();
        userRepository.save(user);

        log.info("Password successfully reset for user: {}", user.getUsername());
        return true;
    }
}
