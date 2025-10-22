package com.victor.astronaut.cache;

import com.victor.astronaut.appuser.AppUserPrincipalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){
        var stringRedisSerializer = new StringRedisSerializer();
        var genericSerializer = new GenericJackson2JsonRedisSerializer();

        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(genericSerializer);
        template.setHashValueSerializer(genericSerializer);
        return template;
    }



}
