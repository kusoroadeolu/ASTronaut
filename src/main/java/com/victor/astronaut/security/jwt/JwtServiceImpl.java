package com.victor.astronaut.security.jwt;

import com.victor.astronaut.appuser.AppUserPrincipalDto;
import com.victor.astronaut.security.JwtService;
import com.victor.astronaut.exceptions.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JwtConfigProperties jwtConfigProperties;

    /**
     * Decodes the JWT Key
     * @return A Secret key using a sha 256 and the JWT Key
     * */
    private SecretKey generateKey(){
        byte[] kb = Decoders.BASE64.decode(this.jwtConfigProperties.getSecret()); //Decode the jwt secret to bytes
        return Keys.hmacShaKeyFor(kb);
    }


    /**
     * Creates a jwt token, assigns the user claim and sets the ttl for the JWT token.
     * @param principal The user principal to which the JWT Token is assigned to
     * @return A signed JWT Token
     * */
    @Override
    public String generateToken(AppUserPrincipalDto principal){
        final String subject = String.valueOf(principal.id());
        final HashMap<String, String> claims = new HashMap<>(1);
        claims.put("email", principal.email());

        final long currentMillis = System.currentTimeMillis();
        final Date issuedAt = new Date(currentMillis);
        final Date expiresAt = new Date(currentMillis + this.jwtConfigProperties.getTtl());

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(generateKey())
                .compact();
    }


    /**
     * Extracts the claims from a JWT token.
     * @param jwtToken The token to extract the claims from
     * @return An claims
     * */
    @Override
    public Claims extractAllClaims(String jwtToken){
        try{
            return Jwts
                    .parser()
                    .verifyWith(this.generateKey())
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
        }catch (Exception e){
            log.info("Failed to extract claims from a jwt token");
            throw new JwtException("An error occurred while trying to extract claims from a jwt token", e);
        }
    }

    /**
     * Extracts the user's ID from the JWT token.
     * @param jwtToken The JWT token
     * @return The user's ID
     * */
    @Override
    public Long extractId(String jwtToken){
        final Claims claims = this.extractAllClaims(jwtToken);
        return Long.parseLong(claims.getSubject());
    }


    /**
     * Extracts the user's email from the JWT token.
     * @param jwtToken The JWT token
     * @return The user's email
     * */
    @Override
    public String extractEmail(String jwtToken){
        final Claims claims = this.extractAllClaims(jwtToken);
        return (String) claims.get("email");
    }

    /**
     * Validates a JWT token, checking if the user ID equals the token subject and the token has not expired
     * @param jwtToken The JWT token given
     * @param principal The user principal
     * @return a boolean value indicating if the token is valid or not
     * */
    @Override
    public boolean isTokenValid(String jwtToken, AppUserPrincipalDto principal){
        final long subject = this.extractId(jwtToken);
        return principal.id().equals(subject) && !isTokenExpired(jwtToken);
    }


    //Checks if a token has expired
    private boolean isTokenExpired(String jwtToken){
        final long currentTimeMillis = System.currentTimeMillis();
        return this.extractAllClaims(jwtToken).getExpiration().before(new Date(currentTimeMillis));
    }

    @Override
    public String refreshTokenIfNeeded(String jwtToken, AppUserPrincipalDto principal){
        final long currentTimeMillis = System.currentTimeMillis();
        //Should return true if the date is after a minute before the current millis
        boolean shouldRefresh = this.extractAllClaims(jwtToken).getExpiration().before(new Date(currentTimeMillis + jwtConfigProperties.getRefreshBefore()));

        if(shouldRefresh){
            return this.generateToken(principal);
        }

        return jwtToken;
    }






}
