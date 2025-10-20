package com.victor.astronaut.appuser.dtos;

public record AppUserDeleteRequest(
        String password,
        String confirmPassword
) {
}
