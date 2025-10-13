package com.victor.astronaut.auth.jwt;

import com.victor.astronaut.auth.appuser.AppUserPrincipal;
import io.jsonwebtoken.Claims;

public interface JwtService {
    String generateToken(AppUserPrincipal principal);

    Claims extractAllClaims(String jwtToken);

    Long extractId(String jwtToken);

    String extractEmail(String jwtToken);

    boolean isTokenValid(String jwtToken, AppUserPrincipal principal);

    String refreshTokenIfNeeded(String jwtToken, AppUserPrincipal principal);
}
