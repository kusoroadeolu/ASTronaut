package com.victor.astronaut.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("jwt")
@Setter
@Getter
public class JwtConfigProperties {
    private String secret;
    private Long ttl;
    private String cookieName;
    private int cookieMaxAge;
    private boolean cookieSecure;
    public boolean cookieHttpOnly;
}
