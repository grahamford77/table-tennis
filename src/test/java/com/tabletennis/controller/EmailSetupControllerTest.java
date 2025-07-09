package com.tabletennis.controller;

import com.tabletennis.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(EmailSetupController.class)
class EmailSetupControllerTest {

    private static final String SETUP_EMAIL_ENDPOINT = "/setup-email";
    private static final String ADMIN_REDIRECT_URL = "/admin";
    private static final String SETUP_EMAIL_VIEW = "setup-email";
    private static final String EMAIL_SETUP_FORM_ATTRIBUTE = "emailSetupForm";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String EMAIL_PARAM = "email";
    private static final String TEST_USERNAME = "testuser";
    private static final String VALID_EMAIL = "test@example.com";
    private static final String INVALID_EMAIL = "invalid-email";
    private static final String TAKEN_EMAIL = "taken@example.com";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Setup common mock behavior
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void showEmailSetupForm_WhenUserNeedsEmailSetup_ShouldShowForm() throws Exception {
        // Given
        when(userService.needsEmailSetup(TEST_USERNAME)).thenReturn(true);

        // When & Then
        mockMvc.perform(get(SETUP_EMAIL_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(view().name(SETUP_EMAIL_VIEW))
                .andExpect(model().attributeExists(EMAIL_SETUP_FORM_ATTRIBUTE));

        verify(userService).needsEmailSetup(TEST_USERNAME);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void showEmailSetupForm_WhenUserDoesNotNeedEmailSetup_ShouldRedirectToAdmin() throws Exception {
        // Given
        when(userService.needsEmailSetup(TEST_USERNAME)).thenReturn(false);

        // When & Then
        mockMvc.perform(get(SETUP_EMAIL_ENDPOINT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ADMIN_REDIRECT_URL));

        verify(userService).needsEmailSetup(TEST_USERNAME);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void processEmailSetup_WithValidEmail_ShouldUpdateEmailAndRedirect() throws Exception {
        // Given
        when(userService.isEmailTaken(VALID_EMAIL, TEST_USERNAME)).thenReturn(false);
        doNothing().when(userService).updateUserEmail(TEST_USERNAME, VALID_EMAIL);

        // When & Then
        mockMvc.perform(post(SETUP_EMAIL_ENDPOINT)
                        .with(csrf())
                        .param(EMAIL_PARAM, VALID_EMAIL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ADMIN_REDIRECT_URL));

        verify(userService).isEmailTaken(VALID_EMAIL, TEST_USERNAME);
        verify(userService).updateUserEmail(TEST_USERNAME, VALID_EMAIL);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void processEmailSetup_WithInvalidEmail_ShouldReturnFormWithErrors() throws Exception {
        // When & Then
        mockMvc.perform(post(SETUP_EMAIL_ENDPOINT)
                        .with(csrf())
                        .param(EMAIL_PARAM, INVALID_EMAIL))
                .andExpect(status().isOk())
                .andExpect(view().name(SETUP_EMAIL_VIEW))
                .andExpect(model().attributeExists(EMAIL_SETUP_FORM_ATTRIBUTE))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void processEmailSetup_WithEmptyEmail_ShouldReturnFormWithErrors() throws Exception {
        // When & Then
        mockMvc.perform(post(SETUP_EMAIL_ENDPOINT)
                        .with(csrf())
                        .param(EMAIL_PARAM, ""))
                .andExpect(status().isOk())
                .andExpect(view().name(SETUP_EMAIL_VIEW))
                .andExpect(model().attributeExists(EMAIL_SETUP_FORM_ATTRIBUTE))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void processEmailSetup_WithTakenEmail_ShouldReturnFormWithErrors() throws Exception {
        // Given
        when(userService.isEmailTaken(TAKEN_EMAIL, TEST_USERNAME)).thenReturn(true);

        // When & Then
        mockMvc.perform(post(SETUP_EMAIL_ENDPOINT)
                        .with(csrf())
                        .param(EMAIL_PARAM, TAKEN_EMAIL))
                .andExpect(status().isOk())
                .andExpect(view().name(SETUP_EMAIL_VIEW))
                .andExpect(model().attributeExists(EMAIL_SETUP_FORM_ATTRIBUTE))
                .andExpect(model().hasErrors());

        verify(userService).isEmailTaken(TAKEN_EMAIL, TEST_USERNAME);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void processEmailSetup_WhenServiceThrowsException_ShouldReturnFormWithError() throws Exception {
        // Given
        when(userService.isEmailTaken(VALID_EMAIL, TEST_USERNAME)).thenReturn(false);
        doThrow(new RuntimeException("Database error")).when(userService).updateUserEmail(TEST_USERNAME, VALID_EMAIL);

        // When & Then
        mockMvc.perform(post(SETUP_EMAIL_ENDPOINT)
                        .with(csrf())
                        .param(EMAIL_PARAM, VALID_EMAIL))
                .andExpect(status().isOk())
                .andExpect(view().name(SETUP_EMAIL_VIEW))
                .andExpect(model().attributeExists(EMAIL_SETUP_FORM_ATTRIBUTE))
                .andExpect(model().attributeExists(ERROR_ATTRIBUTE));

        verify(userService).isEmailTaken(VALID_EMAIL, TEST_USERNAME);
        verify(userService).updateUserEmail(TEST_USERNAME, VALID_EMAIL);
    }
}
