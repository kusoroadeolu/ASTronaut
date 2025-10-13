package com.victor.astronaut.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("cache.principal")
@Component
@RequiredArgsConstructor
@Setter
@Getter
public class AppPrincipalCacheConfigProperties {
    private String name;
    private int ttl;

}
