package com.victor.astronaut.security.config;

import com.victor.astronaut.appuser.services.AppUserDetailsService;
import com.victor.astronaut.security.filters.JwtFilter;
import com.victor.astronaut.security.filters.RateLimitFilter;
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
    private final SecurityConfigProperties securityConfigProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        int arrSize = this.securityConfigProperties.getExcludedPaths().size();
        String[] excludedPaths = this.securityConfigProperties.getExcludedPaths().toArray(new String[arrSize]);
        return security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(excludedPaths).permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(l ->  {
                    l.logoutUrl(this.securityConfigProperties.getLogoutUrl());
                    l.deleteCookies(this.jwtConfigProperties.getCookieName());
                    l.logoutSuccessUrl(this.securityConfigProperties.getRedirectUrl());
                    l.clearAuthentication(this.securityConfigProperties.isClearAuth());
                })
                .addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(this.rateLimitFilter, JwtFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(this.appUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(this.securityConfigProperties.getEncodingStrength());
    }


}
