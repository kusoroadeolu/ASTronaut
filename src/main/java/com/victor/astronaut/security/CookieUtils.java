package com.victor.astronaut.security;

import com.victor.astronaut.security.jwt.JwtConfigProperties;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CookieUtils {

    private final JwtConfigProperties configProperties;

    public Cookie createJwtCookie(String jwtToken){
        final Cookie cookie = new Cookie(this.configProperties.getCookieName(), jwtToken);
        cookie.setMaxAge(this.configProperties.getCookieMaxAge());
        cookie.setHttpOnly(this.configProperties.isCookieHttpOnly());
        cookie.setSecure(this.configProperties.isCookieSecure());
        cookie.setPath(this.configProperties.getCookiePath());
        return cookie;
    }


}
