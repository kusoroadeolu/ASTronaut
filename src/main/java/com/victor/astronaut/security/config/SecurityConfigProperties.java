package com.victor.astronaut.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Getter
@Setter
@Component
@ConfigurationProperties("security")
public class SecurityConfigProperties {
    private Set<String> excludedPaths;
    private String logoutUrl;
    private String redirectUrl;
    private boolean clearAuth;
    private int encodingStrength;
}
