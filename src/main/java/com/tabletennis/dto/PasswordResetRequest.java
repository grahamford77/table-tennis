package com.tabletennis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for password reset form
 */
@Data
public class PasswordResetRequest {

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    private String token;

    public boolean arePasswordsDifferent() {
        return password == null || !password.equals(confirmPassword);
    }
}
