package canhnam357.shortenurlproject.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address", regexp = "^[A-Za-z0-9+_.-]+@(.+)$")
        @Size(max = 255, message = "Email is too long")
        String email,
        @Size(min = 6, max = 64, message = "Passwords should be between 8 and 64 characters long.")
        String password) {
}
