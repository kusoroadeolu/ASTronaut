package com.victor.astronaut.appuser.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AppUserLoginResponse(
        String username,
        String email,
        @JsonIgnore
        String jwtToken
) {
}
