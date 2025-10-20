package com.victor.astronaut.appuser.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AppUserUpdateRequest(
        @NotBlank(message = "Username must not be empty or null")
        String username,
        @NotBlank(message = "User email cannot be blank")
        @Email(message = "User email must be an email")
        String email
) {
}
