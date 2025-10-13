package com.victor.astronaut.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisCacheConfig {

    private final AppPrincipalCacheConfigProperties appPrincipalCacheConfigProperties;

    public LettuceConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        return RedisCacheManager.create(redisConnectionFactory);
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer cacheManagerBuilderCustomizer(GenericJackson2JsonRedisSerializer redisSerializer){
        final RedisCacheConfiguration jwtConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(this.appPrincipalCacheConfigProperties.getTtl()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .disableCachingNullValues();


        return (builder) -> builder
                .withCacheConfiguration(this.appPrincipalCacheConfigProperties.getName(), jwtConfig);
    }

    @Bean(name = "redisSerializer")
    public GenericJackson2JsonRedisSerializer redisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }

}
