package com.tabletennis.config;

import java.io.IOException;

import com.tabletennis.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Custom authentication success handler to redirect users to email setup if needed
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSetupAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        String username = authentication.getName();

        // Check if user needs to set up email
        if (userService.needsEmailSetup(username)) {
            log.info("User {} needs email setup, redirecting to setup page", username);
            response.sendRedirect("/setup-email");
        } else {
            log.info("User {} authenticated successfully, redirecting to admin", username);
            response.sendRedirect("/admin");
        }
    }
}
