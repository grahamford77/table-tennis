package com.tabletennis.controller;

import com.tabletennis.dto.ForgotPasswordRequest;
import com.tabletennis.dto.PasswordResetRequest;
import com.tabletennis.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling password reset functionality
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {

    private static final String ERROR_MESSAGE_ATTRIBUTE_NAME = "errorMessage";

    private final PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@Valid ForgotPasswordRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/forgot-password";
        }

        var success = passwordResetService.initiatePasswordReset(request);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "If an account with that email exists, we've sent you a password reset link.");
        } else {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME,
                    "There was an error processing your request. Please try again.");
        }

        return "redirect:/forgot-password-success";
    }

    @GetMapping("/forgot-password-success")
    public String showForgotPasswordSuccess() {
        return "auth/forgot-password-success";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        var userOptional = passwordResetService.validateResetToken(token);

        if (userOptional.isEmpty()) {
            model.addAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Invalid or expired password reset link.");
            return "auth/reset-password-error";
        }

        var request = new PasswordResetRequest();
        request.setToken(token);
        model.addAttribute("passwordResetRequest", request);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@Valid PasswordResetRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validate passwords match
        if (request.arePasswordsDifferent()) {
            result.rejectValue("confirmPassword", "error.passwordResetRequest",
                    "Passwords do not match");
        }

        if (result.hasErrors()) {
            return "auth/reset-password";
        }

        var success = passwordResetService.resetPassword(request);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Your password has been successfully reset. You can now login with your new password.");
            return "redirect:/login";
        } else {
            model.addAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME,
                    "Invalid or expired password reset link. Please request a new one.");
            return "auth/reset-password-error";
        }
    }
}
