package com.victor.astronaut.appuser.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AppUserRegisterRequest(
        @NotEmpty(message = "Username cannot be empty")
        @NotNull(message = "Username cannot be null")
        String username,

        @NotEmpty(message = "Email cannot be empty")
        @NotNull(message = "Email cannot be null")
        @Email(message = "String must be an email")
        String email,

        @Size(min = 6, max = 100, message = "Password must at least have 6 values")
        @NotNull(message = "Password cannot be null")
        String password,

        @Size(min = 6, max = 100, message = "Password must at least have 6 values")
        @NotNull(message = "Password cannot be null")
        String confirmPassword
) {
    public AppUserRegisterRequest{
        username = username.trim();
        email = email.trim();
        password = password.trim();
        confirmPassword = confirmPassword.trim();
    }
}
