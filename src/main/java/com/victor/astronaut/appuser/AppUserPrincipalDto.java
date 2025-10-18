package com.victor.astronaut.appuser;

import lombok.Builder;

@Builder
public record AppUserPrincipalDto(
        Long id,
        AppUserRole role,
        String username,
        String email
) {
}
