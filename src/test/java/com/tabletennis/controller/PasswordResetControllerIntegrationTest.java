package com.tabletennis.controller;

import com.tabletennis.TestDataFactory;
import com.tabletennis.entity.User;
import com.tabletennis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PasswordResetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up database
        userRepository.deleteAll();

        // Create test user
        testUser = TestDataFactory.createUser();
        testUser.setEmail(TestDataFactory.randomEmail()); // Set email for password reset tests
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser = userRepository.save(testUser);
    }

    @Test
    void forgotPassword_ShouldReturnForgotPasswordPage() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Forgot Your Password?")))
                .andExpect(content().string(containsString("Enter your email address")));
    }

    @Test
    void processForgotPassword_WithValidEmail_ShouldRedirectToSuccessPage() throws Exception {
        mockMvc.perform(post("/forgot-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", testUser.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password-success"));
    }

    @Test
    void processForgotPassword_WithInvalidEmail_ShouldRedirectToSuccessPage() throws Exception {
        var invalidEmail = TestDataFactory.randomEmail();

        mockMvc.perform(post("/forgot-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", invalidEmail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password-success"));
    }

    @Test
    void processForgotPassword_WithBlankEmail_ShouldReturnFormWithErrors() throws Exception {
        mockMvc.perform(post("/forgot-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Email is required")));
    }

    @Test
    void processForgotPassword_WithInvalidEmailFormat_ShouldReturnFormWithErrors() throws Exception {
        mockMvc.perform(post("/forgot-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "invalid-email"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Please enter a valid email address")));
    }

    @Test
    void forgotPasswordSuccess_ShouldReturnSuccessPage() throws Exception {
        mockMvc.perform(get("/forgot-password-success"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Password Reset Email Sent")))
                .andExpect(content().string(containsString("Check your email inbox")));
    }

    @Test
    void resetPassword_WithValidToken_ShouldReturnResetPasswordPage() throws Exception {
        // Generate password reset token
        var resetToken = UUID.randomUUID().toString();
        testUser.setPasswordResetToken(resetToken);
        testUser.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(testUser);

        mockMvc.perform(get("/reset-password").param("token", resetToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Reset Your Password")))
                .andExpect(content().string(containsString("Enter your new password")));
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldReturnErrorPage() throws Exception {
        var invalidToken = UUID.randomUUID().toString();

        mockMvc.perform(get("/reset-password").param("token", invalidToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Invalid Reset Link")))
                .andExpect(content().string(containsString("Invalid or expired password reset link")));
    }

    @Test
    void resetPassword_WithExpiredToken_ShouldReturnErrorPage() throws Exception {
        // Generate expired password reset token
        var expiredToken = UUID.randomUUID().toString();
        testUser.setPasswordResetToken(expiredToken);
        testUser.setPasswordResetTokenExpiry(LocalDateTime.now().minusHours(1)); // Expired
        userRepository.save(testUser);

        mockMvc.perform(get("/reset-password").param("token", expiredToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Invalid Reset Link")));
    }

    @Test
    void processResetPassword_WithValidData_ShouldRedirectToLogin() throws Exception {
        // Generate password reset token
        var resetToken = UUID.randomUUID().toString();
        testUser.setPasswordResetToken(resetToken);
        testUser.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(testUser);

        var newPassword = TestDataFactory.randomPassword();

        mockMvc.perform(post("/reset-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("token", resetToken)
                .param("password", newPassword)
                .param("confirmPassword", newPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void processResetPassword_WithMismatchedPasswords_ShouldReturnFormWithErrors() throws Exception {
        // Generate password reset token
        var resetToken = UUID.randomUUID().toString();
        testUser.setPasswordResetToken(resetToken);
        testUser.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(testUser);

        var password1 = TestDataFactory.randomPassword();
        var password2 = TestDataFactory.randomPassword();

        mockMvc.perform(post("/reset-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("token", resetToken)
                .param("password", password1)
                .param("confirmPassword", password2))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Passwords do not match")));
    }

    @Test
    void processResetPassword_WithShortPassword_ShouldReturnFormWithErrors() throws Exception {
        // Generate password reset token
        var resetToken = UUID.randomUUID().toString();
        testUser.setPasswordResetToken(resetToken);
        testUser.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(testUser);

        var shortPassword = "123"; // Too short

        mockMvc.perform(post("/reset-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("token", resetToken)
                .param("password", shortPassword)
                .param("confirmPassword", shortPassword))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Password must be at least 8 characters")));
    }

    @Test
    void processResetPassword_WithInvalidToken_ShouldReturnErrorPage() throws Exception {
        var invalidToken = UUID.randomUUID().toString();
        var newPassword = TestDataFactory.randomPassword();

        mockMvc.perform(post("/reset-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("token", invalidToken)
                .param("password", newPassword)
                .param("confirmPassword", newPassword))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Invalid or expired password reset link")));
    }
}
