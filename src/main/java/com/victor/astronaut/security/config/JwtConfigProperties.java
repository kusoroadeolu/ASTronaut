package com.victor.astronaut.security.config;

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
    private long ttl;
    private long refreshBefore;
    private String cookieName;
    private String cookiePath;
    private int cookieMaxAge;
    private boolean cookieSecure;
    private boolean cookieHttpOnly;
}
