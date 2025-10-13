package com.victor.astronaut.auth.appuser.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AppUserLoginResponse(
        String username,
        String email,
        @JsonIgnore
        String jwtToken
) {
}
