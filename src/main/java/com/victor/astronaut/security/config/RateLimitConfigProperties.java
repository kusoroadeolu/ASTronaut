package com.victor.astronaut.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Getter
@Setter
@Component
@ConfigurationProperties("rate-limit")
public class RateLimitConfigProperties {
    private int requestsPerMinute;
    private int defaultKeyExpiration;
    private Set<String> excludedIps;
}
