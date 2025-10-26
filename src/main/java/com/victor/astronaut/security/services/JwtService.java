package com.victor.astronaut.security.services;

import com.victor.astronaut.appuser.dtos.AppUserPrincipalDto;
import io.jsonwebtoken.Claims;

public interface JwtService {
    String generateToken(AppUserPrincipalDto principal);

    Claims extractAllClaims(String jwtToken);

    Long extractId(String jwtToken);

    String extractEmail(String jwtToken);

    boolean isTokenValid(String jwtToken, AppUserPrincipalDto principal);

    String refreshTokenIfNeeded(String jwtToken, AppUserPrincipalDto principal);
}
