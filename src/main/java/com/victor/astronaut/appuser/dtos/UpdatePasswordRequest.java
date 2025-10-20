package com.victor.astronaut.appuser.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
        @NotNull(message = "Current password cannot be null")
        String currentPassword,
        @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
        @NotNull(message = "New password cannot be null")
        String newPassword,
        @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
        @NotNull(message = "Confirm password cannot be null")
        String confirmNewPassword
) {}