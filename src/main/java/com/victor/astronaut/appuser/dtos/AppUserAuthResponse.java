package com.victor.astronaut.appuser.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AppUserAuthResponse(
        long id,
        String username,
        String email,
        @JsonIgnore
        String jwtToken
) {
}
