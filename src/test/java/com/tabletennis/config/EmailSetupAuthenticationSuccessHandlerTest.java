package com.tabletennis.config;

import com.tabletennis.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailSetupAuthenticationSuccessHandlerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private EmailSetupAuthenticationSuccessHandler handler;

    @BeforeEach
    void setUp() {
        handler = new EmailSetupAuthenticationSuccessHandler(userService);
    }

    @Test
    void onAuthenticationSuccess_WhenUserNeedsEmailSetup_ShouldRedirectToSetupEmail() throws IOException {
        // Given
        String username = "testuser";
        when(authentication.getName()).thenReturn(username);
        when(userService.needsEmailSetup(username)).thenReturn(true);

        // When
        handler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(userService).needsEmailSetup(username);
        verify(response).sendRedirect("/setup-email");
    }

    @Test
    void onAuthenticationSuccess_WhenUserDoesNotNeedEmailSetup_ShouldRedirectToAdmin() throws IOException {
        // Given
        String username = "testuser";
        when(authentication.getName()).thenReturn(username);
        when(userService.needsEmailSetup(username)).thenReturn(false);

        // When
        handler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(userService).needsEmailSetup(username);
        verify(response).sendRedirect("/admin");
    }
}
