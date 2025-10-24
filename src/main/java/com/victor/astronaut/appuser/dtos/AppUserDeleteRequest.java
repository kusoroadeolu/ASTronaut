package com.victor.astronaut.appuser.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AppUserDeleteRequest(
        @Size(min = 6, max = 100, message = "Password must at least have 6 values")
        @NotNull(message = "Password cannot be null")
        String password,
        @Size(min = 6, max = 100, message = "Password must at least have 6 values")
        @NotNull(message = "Password cannot be null")
        String confirmPassword
) {
    public AppUserDeleteRequest{
        password = password.trim();
        confirmPassword = confirmPassword.trim();
    }
}
