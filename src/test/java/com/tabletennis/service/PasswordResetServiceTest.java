package com.tabletennis.service;

import com.tabletennis.TestDataFactory;
import com.tabletennis.dto.ForgotPasswordRequest;
import com.tabletennis.dto.PasswordResetRequest;
import com.tabletennis.entity.User;
import com.tabletennis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordResetService passwordResetService;

    private User testUser;

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetService(userRepository, emailService, passwordEncoder);
        testUser = TestDataFactory.createUser();
    }

    @Test
    void initiatePasswordReset_WithExistingUser_ShouldGenerateTokenAndSendEmail() {
        // Given
        var request = new ForgotPasswordRequest();
        request.setEmail(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        var result = passwordResetService.initiatePasswordReset(request);

        // Then
        assertTrue(result);
        assertNotNull(testUser.getPasswordResetToken());
        assertNotNull(testUser.getPasswordResetTokenExpiry());
        assertTrue(testUser.getPasswordResetTokenExpiry().isAfter(LocalDateTime.now()));
        verify(userRepository).save(testUser);
        verify(emailService).sendPasswordResetEmail(testUser.getEmail(), testUser.getPasswordResetToken());
    }

    @Test
    void initiatePasswordReset_WithNonExistentUser_ShouldReturnTrueForSecurity() {
        // Given
        var request = new ForgotPasswordRequest();
        request.setEmail(TestDataFactory.randomEmail());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When
        var result = passwordResetService.initiatePasswordReset(request);

        // Then
        assertTrue(result);
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void initiatePasswordReset_WhenEmailServiceFails_ShouldReturnFalseAndClearToken() {
        // Given
        var request = new ForgotPasswordRequest();
        request.setEmail(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doThrow(new RuntimeException("Email service failed"))
            .when(emailService).sendPasswordResetEmail(anyString(), anyString());

        // When
        var result = passwordResetService.initiatePasswordReset(request);

        // Then
        assertFalse(result);
        assertNull(testUser.getPasswordResetToken());
        assertNull(testUser.getPasswordResetTokenExpiry());
        verify(userRepository, times(2)).save(testUser); // Once to set, once to clear
    }

    @Test
    void validateResetToken_WithValidToken_ShouldReturnUser() {
        // Given
        var token = UUID.randomUUID().toString();
        testUser.setPasswordResetToken(token);
        testUser.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(testUser));

        // When
        var result = passwordResetService.validateResetToken(token);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void validateResetToken_WithExpiredToken_ShouldReturnEmptyAndClearToken() {
        // Given
        var token = UUID.randomUUID().toString();
        testUser.setPasswordResetToken(token);
        testUser.setPasswordResetTokenExpiry(LocalDateTime.now().minusHours(1)); // Expired
        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        var result = passwordResetService.validateResetToken(token);

        // Then
        assertFalse(result.isPresent());
        assertNull(testUser.getPasswordResetToken());
        assertNull(testUser.getPasswordResetTokenExpiry());
        verify(userRepository).save(testUser);
    }

    @Test
    void validateResetToken_WithInvalidToken_ShouldReturnEmpty() {
        // Given
        var token = UUID.randomUUID().toString();
        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.empty());

        // When
        var result = passwordResetService.validateResetToken(token);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void validateResetToken_WithNullToken_ShouldReturnEmpty() {
        // When
        var result = passwordResetService.validateResetToken(null);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, never()).findByPasswordResetToken(anyString());
    }

    @Test
    void resetPassword_WithValidTokenAndMatchingPasswords_ShouldUpdatePassword() {
        // Given
        var token = UUID.randomUUID().toString();
        var newPassword = TestDataFactory.randomPassword();
        var encodedPassword = "encoded-" + newPassword;

        testUser.setPasswordResetToken(token);
        testUser.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));

        var request = new PasswordResetRequest();
        request.setToken(token);
        request.setPassword(newPassword);
        request.setConfirmPassword(newPassword);

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        var result = passwordResetService.resetPassword(request);

        // Then
        assertTrue(result);
        assertEquals(encodedPassword, testUser.getPassword());
        assertNull(testUser.getPasswordResetToken());
        assertNull(testUser.getPasswordResetTokenExpiry());
        verify(userRepository).save(testUser);
    }

    @Test
    void resetPassword_WithMismatchedPasswords_ShouldReturnFalse() {
        // Given
        var request = new PasswordResetRequest();
        request.setToken(UUID.randomUUID().toString());
        request.setPassword(TestDataFactory.randomPassword());
        request.setConfirmPassword(TestDataFactory.randomPassword());

        // When
        var result = passwordResetService.resetPassword(request);

        // Then
        assertFalse(result);
        verify(userRepository, never()).findByPasswordResetToken(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldReturnFalse() {
        // Given
        var invalidToken = UUID.randomUUID().toString();
        var password = TestDataFactory.randomPassword();

        var request = new PasswordResetRequest();
        request.setToken(invalidToken);
        request.setPassword(password);
        request.setConfirmPassword(password);

        when(userRepository.findByPasswordResetToken(invalidToken)).thenReturn(Optional.empty());

        // When
        var result = passwordResetService.resetPassword(request);

        // Then
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
}
