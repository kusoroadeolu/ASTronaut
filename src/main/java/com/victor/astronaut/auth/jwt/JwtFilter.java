package com.victor.astronaut.auth.jwt;

import com.victor.astronaut.auth.appuser.AppUserDetailsService;
import com.victor.astronaut.auth.appuser.AppUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtConfigProperties jwtConfigProperties;
    private final AppUserDetailsService appUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final Cookie jwtCookie = WebUtils.getCookie(request, this.jwtConfigProperties.getCookieName());

        if(jwtCookie == null){
            log.info("JWT Cookie is null");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = jwtCookie.getValue();

        if(jwtToken == null || jwtToken.isEmpty()){
            log.info("JWT Token cannot be empty or null");
            filterChain.doFilter(request, response);
            return;
        }


        final long id = this.jwtService.extractId(jwtToken);

        if(SecurityContextHolder.getContext().getAuthentication() != null){
            log.info("Security context must be empty.");
            SecurityContextHolder.clearContext();
        }

        //Principal cannot be null because the service already guards against null checks
        AppUserPrincipal principal = this.appUserDetailsService.loadById(id); //TODO implement a redis cache to store user principal based on JWT Token

        //Check if the token is valid before proceeding
        if(!jwtService.isTokenValid(jwtToken, principal)){
            log.info("JWT Token has expired");
            filterChain.doFilter(request, response);
            return;
        }


        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        //TODO Implement a method that refreshes the token if it would soon expire
        filterChain.doFilter(request, response);

    }
}
