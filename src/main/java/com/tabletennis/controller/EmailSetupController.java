package com.tabletennis.controller;

import com.tabletennis.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Controller for handling email setup after login
 */
@Controller
@RequestMapping("/setup-email")
@RequiredArgsConstructor
@Slf4j
public class EmailSetupController {

    private static final String EMAIL_SETUP_FORM_ATTRIBUTE = "emailSetupForm";
    private static final String SETUP_EMAIL_PAGE = "setup-email";

    private final UserService userService;

    /**
     * Show email setup form
     */
    @GetMapping
    public String showEmailSetupForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        var username = principal.getName();
        if (!userService.needsEmailSetup(username)) {
            return "redirect:/admin";
        }

        model.addAttribute(EMAIL_SETUP_FORM_ATTRIBUTE, new EmailSetupForm());
        return SETUP_EMAIL_PAGE;
    }

    /**
     * Process email setup form submission
     */
    @PostMapping
    public String processEmailSetup(
            @ModelAttribute @Validated EmailSetupForm emailSetupForm,
            BindingResult bindingResult,
            Principal principal,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        var username = principal.getName();

        // Check if email is already taken
        if (userService.isEmailTaken(emailSetupForm.getEmail(), username)) {
            bindingResult.rejectValue("email", "email.taken", "This email address is already in use");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute(EMAIL_SETUP_FORM_ATTRIBUTE, emailSetupForm);
            return SETUP_EMAIL_PAGE;
        }

        try {
            userService.updateUserEmail(username, emailSetupForm.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", "Email address has been successfully set up!");
            log.info("Email setup completed for user: {}", username);
            return "redirect:/admin";
        } catch (Exception e) {
            log.error("Error updating email for user: {}", username, e);
            model.addAttribute("error", "An error occurred while setting up your email. Please try again.");
            model.addAttribute(EMAIL_SETUP_FORM_ATTRIBUTE, emailSetupForm);
            return SETUP_EMAIL_PAGE;
        }
    }

    /**
     * Form object for email setup
     */
    @Setter
    @Getter
    public static class EmailSetupForm {
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        private String email;
    }
}
