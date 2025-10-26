package com.victor.astronaut.appuser.dtos;

import com.victor.astronaut.appuser.entites.AppUserRole;
import lombok.Builder;

@Builder
public record AppUserPrincipalDto(
        Long id,
        AppUserRole role,
        String username,
        String email
) {
}
