package com.victor.astronaut.auth.appuser.impl;

import com.victor.astronaut.auth.CookieUtils;
import com.victor.astronaut.auth.appuser.*;
import com.victor.astronaut.auth.appuser.dtos.AppUserLoginRequest;
import com.victor.astronaut.auth.appuser.dtos.AppUserLoginResponse;
import com.victor.astronaut.auth.appuser.dtos.AppUserRegisterRequest;
import com.victor.astronaut.auth.appuser.entities.AppUser;
import com.victor.astronaut.auth.jwt.JwtService;
import com.victor.astronaut.exceptions.AppUserAlreadyExistsException;
import com.victor.astronaut.exceptions.InvalidCredentialsException;
import io.jsonwebtoken.InvalidClaimException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.spi.support.CacheUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserServiceImpl {

    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;
    private final AppUserPrincipalCacheService cacheService;
    private final CookieUtils cookieUtils;


    /**
     * Registers an app user and logs them in
     * @param registerRequest A dto containing the needed info to register the user. The dto must have been validated before being passed down
     * @return a dto containing the needed values after login
     * */
    @Transactional
    public AppUserLoginResponse registerAppUser(@NonNull AppUserRegisterRequest registerRequest){
        try{
            final String encodedPassword = this.passwordEncoder.encode(registerRequest.password());
            final AppUser appUser = this.appUserMapper.toAppUser(registerRequest, encodedPassword);
            final AppUser savedAppUser = this.appUserRepository.save(appUser);
            return this.loginAppUser(this.appUserMapper.toLoginRequest(savedAppUser));
        }catch (DataIntegrityViolationException e){
            log.info("Found user with similar email address.");
            throw new AppUserAlreadyExistsException("This email address is already taken. Please use a different email", e);
        }catch (Exception e){
            log.info("An unexpected error occurred while trying to register user: {}", registerRequest.email());
            throw new AppUserAlreadyExistsException(String.format("An unexpected error occurred while trying to register user: %s", registerRequest.email()));
        }
    }

    /**
     * Logs in an app user. Generates a jwt token for the app user, caches the user principal.
     * @param loginRequest A dto containing the email and password for the log in
     * @return a dto containing the needed values after login
     * */
    public AppUserLoginResponse loginAppUser(@NonNull AppUserLoginRequest loginRequest){
        final AppUser savedAppUser = this.appUserRepository
                .findAppUserByEmailAndPassword(loginRequest.email(), loginRequest.password())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials. Please re-check your email or password"));
        final AppUserPrincipal principal = new AppUserPrincipal(savedAppUser);
        final String jwtToken = jwtService.generateToken(principal);
        cookieUtils.addJwtCookie(jwtToken); //Add the jwt token to a cookie
        cacheService.cachePrincipal(jwtToken, principal); //Cache the user principal
        return new AppUserLoginResponse(savedAppUser.getUsername(), savedAppUser.getEmail());
    }

}
