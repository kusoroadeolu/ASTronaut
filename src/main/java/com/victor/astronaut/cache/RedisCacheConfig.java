package com.victor.astronaut.cache;

import com.victor.astronaut.appuser.AppUserPrincipalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisCacheConfig {

    private final AppPrincipalCacheConfigProperties appPrincipalCacheConfigProperties;


    @Bean
    public RedisCacheManagerBuilderCustomizer cacheManagerBuilderCustomizer(){
        Jackson2JsonRedisSerializer<AppUserPrincipalDto> principalSerializer = new Jackson2JsonRedisSerializer<>(AppUserPrincipalDto.class);
        final RedisCacheConfiguration principalConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(this.appPrincipalCacheConfigProperties.getTtl()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(principalSerializer))
                .disableCachingNullValues();


        return (builder) -> builder
                .withCacheConfiguration(this.appPrincipalCacheConfigProperties.getName(), principalConfig);
    }


}
