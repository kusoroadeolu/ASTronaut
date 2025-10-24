package com.victor.astronaut.security.ratelimits;


import com.victor.astronaut.exceptions.RateLimitException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RateLimitConfigProperties rateLimitConfigProperties;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        if(!this.rateLimitConfigProperties.getExcludedIps().contains(ip)){

            try{
                this.handleRateLimit(ip, request);
            }catch (RateLimitException | IllegalArgumentException e){
                response.sendError(429, "Slow down. Too many requests");
                filterChain.doFilter(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }



    public void handleRateLimit(String ip, HttpServletRequest request){
        final LocalDateTime now = LocalDateTime.now();
        final int currentMinute = now.getMinute();
        final int elapsedSeconds = now.getSecond(); //Get the elapsed seconds for this minute
        final int lastMinute = currentMinute == 0 ? 59 : currentMinute - 1; //Check if the current minute is 0, if so the last minute was 59

        final String currentMinuteKey = this.constructKey(ip, currentMinute);
        final Long requestThisMinute = redisTemplate.opsForValue().increment(currentMinuteKey, 1); //Increment the request made this minute
        final Long requestLastMinute = (Long) redisTemplate.opsForValue().get(this.constructKey(ip, lastMinute));

        if(requestThisMinute != null && requestLastMinute != null){
            double avgRequest = ((elapsedSeconds * requestThisMinute)
                    + ((60 - elapsedSeconds) * requestLastMinute)) / 60.0; //Get the average request of requests made this minute and last minute

            //If the avg request is greater than the allowed requests, throw
            if(avgRequest > this.rateLimitConfigProperties.getRequestsPerMinute()){
                log.info("Rate limit for user with IP: {} exceeded", ip);
                throw new RateLimitException();
            }
        }

        redisTemplate.expire(currentMinuteKey, Duration.ofMinutes(this.rateLimitConfigProperties.getDefaultKeyExpiration()));
    }

    //Builds the value to store in the redis template
    private String constructKey(String ip, int currentMinute){
        return "%s&%s".formatted(ip, currentMinute);
    }
}
