package com.victor.astronaut.appuser.impl;

import com.victor.astronaut.appuser.AppUserPrincipal;
import com.victor.astronaut.appuser.AppUserPrincipalCacheService;
import com.victor.astronaut.appuser.repositories.AppUserRepository;
import com.victor.astronaut.appuser.dtos.AppUserLoginRequest;
import com.victor.astronaut.appuser.dtos.AppUserLoginResponse;
import com.victor.astronaut.appuser.dtos.AppUserRegisterRequest;
import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.security.JwtService;
import com.victor.astronaut.exceptions.AppUserAlreadyExistsException;
import com.victor.astronaut.exceptions.AppUserPersistenceException;
import com.victor.astronaut.exceptions.InvalidCredentialsException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;
    private final AppUserPrincipalCacheService cacheService;


    /**
     * Registers an app user and logs them in
     * @param registerRequest A dto containing the needed info to register the user. The dto must have been validated before being passed down
     * @return a dto containing the needed values after login
     * */
    @Transactional
    @Override
    public AppUserLoginResponse registerAppUser(@NonNull AppUserRegisterRequest registerRequest){
        log.info("Registering app user with username: {} and email: {}", registerRequest.username(), registerRequest.email());
        try{
            final String encodedPassword = this.passwordEncoder.encode(registerRequest.password());
            final AppUser appUser = this.appUserMapper.toAppUser(registerRequest, encodedPassword);
            final AppUser savedAppUser = this.appUserRepository.save(appUser);
            log.info("Successfully registered app user with username: {} and email: {}", registerRequest.username(), registerRequest.email());
            return this.loginAppUser(this.appUserMapper.toLoginRequest(savedAppUser));
        }catch (DataIntegrityViolationException e){
            log.info("Found user with similar email address.", e);
            throw new AppUserAlreadyExistsException("This email address is already taken. Please use a different email", e);
        }catch (Exception e){
            log.info("An unexpected error occurred while trying to register user: {}", registerRequest.email(), e);
            throw new AppUserPersistenceException(String.format("An unexpected error occurred while trying to register user: %s", registerRequest.email()), e);
        }
    }

    /**
     * Logs in an app user. Generates a jwt token for the app user, caches the user principal.
     * @param loginRequest A dto containing the email and password for the log in
     * @return a dto containing the needed values after login
     * */
    @Transactional(readOnly = true)
    @Override
    public AppUserLoginResponse loginAppUser(@NonNull AppUserLoginRequest loginRequest){
        log.info("Attempting to login app user with email: {}", loginRequest.email());
        final AppUser savedAppUser = this.appUserRepository
                .findAppUserByEmail(loginRequest.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials. Please re-check your email or password"));
        final AppUserPrincipal principal = new AppUserPrincipal(savedAppUser);

        if(!this.passwordEncoder.matches(loginRequest.password(), savedAppUser.getPassword())){
            log.info("Failed to login user due to invalid password");
            throw new InvalidCredentialsException("Invalid credentials. Please re-check your email or password");
        }

        final String jwtToken = jwtService.generateToken(principal);
        cacheService.cachePrincipal(savedAppUser.getId(), principal); //Cache the user principal
        log.info("Successfully logged in app user with email: {}", loginRequest.email());
        return this.appUserMapper.toResponse(savedAppUser, jwtToken);
    }

}
