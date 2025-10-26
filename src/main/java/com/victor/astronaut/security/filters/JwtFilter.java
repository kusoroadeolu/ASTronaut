package com.victor.astronaut.security.filters;

import com.victor.astronaut.appuser.services.AppUserDetailsService;
import com.victor.astronaut.appuser.entites.AppUserPrincipal;
import com.victor.astronaut.appuser.dtos.AppUserPrincipalDto;
import com.victor.astronaut.security.config.JwtConfigProperties;
import com.victor.astronaut.security.services.JwtService;
import com.victor.astronaut.utils.CookieUtils;
import com.victor.astronaut.security.config.SecurityConfigProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
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
    private final SecurityConfigProperties securityConfigProperties;
    private final JwtConfigProperties jwtConfigProperties;
    private final AppUserDetailsService appUserDetailsService;
    private final CookieUtils cookieUtils;
    private AppUserPrincipal principal;



    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        this.shouldNotFilter(request);

        log.info("Authenticating user");

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
        final AppUserPrincipalDto principalDto = this.appUserDetailsService.loadById(id);

        //Check if the token is valid before proceeding
        if(!this.jwtService.isTokenValid(jwtToken, principalDto)){
            log.info("JWT Token has expired");
            filterChain.doFilter(request, response);
            return;
        }

         this.principal = new AppUserPrincipal(principalDto);


        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        final String refreshedToken = this.jwtService.refreshTokenIfNeeded(jwtToken, principalDto);
        response.addCookie(cookieUtils.createJwtCookie(refreshedToken));

        filterChain.doFilter(request, response);
        log.info("Successfully authenticated user with ID: {}", id);

    }


    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return this.securityConfigProperties.getExcludedPaths().contains(request.getRequestURI().toLowerCase());
    }
}
