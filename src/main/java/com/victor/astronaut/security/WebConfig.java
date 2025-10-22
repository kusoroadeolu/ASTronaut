package com.victor.astronaut.security;

import com.victor.astronaut.appuser.AppUserDetailsService;
import com.victor.astronaut.security.jwt.JwtConfigProperties;
import com.victor.astronaut.security.jwt.JwtFilter;
import com.victor.astronaut.security.ratelimits.RateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class WebConfig {

    private final AppUserDetailsService appUserDetailsService;
    private final JwtFilter jwtFilter;
    private final RateLimitFilter rateLimitFilter;
    private final JwtConfigProperties jwtConfigProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        return security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/index.html").permitAll();
                    auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs", "/swagger-ui.html").permitAll();
                    auth.requestMatchers("/css/header.css", "/js/header.js").permitAll();
                    auth.requestMatchers("/css/toast.css", "/js/toast.js").permitAll();
                    auth.requestMatchers("/auth.html", "/auth.js").permitAll();
                    auth.requestMatchers("/api-docs").permitAll();
                    auth.requestMatchers("/auth/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(l ->  {
                    l.logoutUrl("/users/logout");
                    l.deleteCookies(jwtConfigProperties.getCookieName());
                    l.logoutSuccessUrl("/index.html");
                    l.clearAuthentication(true);
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitFilter, JwtFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(appUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }


}
