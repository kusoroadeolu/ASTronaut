package com.victor.astronaut.security;

import com.victor.astronaut.appuser.AppUserPrincipal;
import io.jsonwebtoken.Claims;

public interface JwtService {
    String generateToken(AppUserPrincipal principal);

    Claims extractAllClaims(String jwtToken);

    Long extractId(String jwtToken);

    String extractEmail(String jwtToken);

    boolean isTokenValid(String jwtToken, AppUserPrincipal principal);

    String refreshTokenIfNeeded(String jwtToken, AppUserPrincipal principal);
}
