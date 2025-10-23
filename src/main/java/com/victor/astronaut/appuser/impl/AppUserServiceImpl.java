package com.victor.astronaut.appuser.impl;

import com.victor.astronaut.appuser.*;
import com.victor.astronaut.appuser.dtos.*;
import com.victor.astronaut.appuser.repositories.AppUserRepository;
import com.victor.astronaut.security.JwtService;
import com.victor.astronaut.exceptions.InvalidCredentialsException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.victor.astronaut.appuser.utils.AppUserUtils.handleWithException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final AppUserQueryService appUserQueryService;
    private final JwtService jwtService;
    private final AppUserPrincipalCacheService cacheService;


    /**
     * Registers an app user and logs them in
     * @param registerRequest A dto containing the needed info to register the user. The dto must have been validated before being passed down
     * @return a dto containing the needed values after login
     * */
    @Transactional
    @Override
    public AppUserAuthResponse registerAppUser(@NonNull AppUserRegisterRequest registerRequest){
        return handleWithException("register", registerRequest.username(), () -> {
            if(!registerRequest.password().equals(registerRequest.confirmPassword())){
                log.info("App user submitted mismatched passwords during registration");
                throw new InvalidCredentialsException("Password and confirm password fields must match");
            }

            this.appUserQueryService.validateEmail(registerRequest.email()) ;

            final String encodedPassword = this.passwordEncoder.encode(registerRequest.password());
            final AppUser appUser = this.appUserMapper.toAppUser(registerRequest, encodedPassword);
            final AppUser savedAppUser = this.appUserRepository.save(appUser);
            final String jwtToken = this.cachePrincipalAndGenerateJwtToken(appUser);
            return this.appUserMapper.toResponse(savedAppUser, jwtToken);
        });
    }

    /**
     * Logs in an app user.
     * @param loginRequest A dto containing the email and password for the log in
     * @return a dto containing the needed values after login
     * */
    @Transactional(readOnly = true)
    @Override
    public AppUserAuthResponse loginAppUser(@NonNull AppUserLoginRequest loginRequest){
        log.info("Attempting to login app user with email: {}", loginRequest.email());
        final AppUser savedAppUser = this.appUserRepository
                .findAppUserByEmailAndIsDeletedFalse(loginRequest.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials. Please re-check your email or password"));


        if(!this.passwordEncoder.matches(loginRequest.password(), savedAppUser.getPassword())){
            log.info("Failed to login user due to invalid password");
            throw new InvalidCredentialsException("Invalid credentials. Please re-check your email or password");
        }

        final String jwtToken = this.cachePrincipalAndGenerateJwtToken(savedAppUser);
        return this.appUserMapper.toResponse(savedAppUser, jwtToken);
    }

    /**
     * Deletes a user
     * @param deleteRequest the dto containing the info needed to delete the user
     * */
    @Override
    public void deleteAppUser(long userId, @NonNull AppUserDeleteRequest deleteRequest){
        log.info("Attempting to delete user with ID: {}", userId);
        final AppUser user = this.appUserQueryService.findById(userId);
        if(!deleteRequest.password().equals(deleteRequest.confirmPassword())){
            log.info("App user submitted mismatched passwords during deletion");
            throw new InvalidCredentialsException("Password and confirm password fields must match");
        }

        if(!passwordEncoder.matches(deleteRequest.password(), user.getPassword())){
            log.info("Failed to delete user due to invalid password");
            throw new InvalidCredentialsException("Invalid credentials. Please re-check your password");
        }

        this.deleteUser(user);
    }

    //Updates a user's preferences
    @Transactional
    @Override
    public UpdatePreferencesResponse updateAppUserPreferences(long userId, @NonNull UpdatePreferencesRequest request){
        final AppUser user = this.appUserQueryService.findById(userId);
        return handleWithException("update", user.getUsername(), () -> {
            user.setEnableFuzzySearch(request.enableFuzzySearch());
            this.appUserRepository.save(user);
            log.info("Successfully updated user preferences");
            return this.appUserMapper.toResponse(request);
        });
    }

    //Updates a user's email or username
    @Transactional
    @Override
    public void updateUsernameOrEmail(long userId, @NonNull AppUserUpdateRequest request){
        final AppUser user = this.appUserQueryService.findById(userId);
        final boolean isPrevUsername = user.getUsername().equals(request.username());
        final boolean isPrevEmail = user.getEmail().equals(request.email());
        if(!isPrevUsername){
            user.setUsername(request.username());
        }

        if (!isPrevEmail){
            this.appUserQueryService.validateEmail(request.email());
            user.setEmail(request.email());
        }

        //Check if the email and username are the same as before
        if(isPrevEmail && isPrevUsername){
            return;
        }

        handleWithException("update", user.getUsername(), () -> {
            return this.appUserRepository.save(user);
        });
    }

    //Updates a user's passwords
    @Transactional
    @Override
    public void updatePassword(long userId, @NonNull UpdatePasswordRequest request){
        final AppUser user = this.appUserQueryService.findById(userId);
        if(!passwordEncoder.matches(request.currentPassword(), user.getPassword())){
            log.info("Failed to update user password due to invalid password");
            throw new InvalidCredentialsException("Invalid credentials. Please re-check your password");
        }

        if(!request.newPassword().equals(request.confirmNewPassword())){
            log.info("App user submitted mismatched passwords during password update");
            throw new InvalidCredentialsException("Password and confirm password fields must match");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        handleWithException("update", user.getUsername(), () -> this.appUserRepository.save(user));
    }


    @Async
    public void deleteUser(AppUser user){
         try{
             this.appUserRepository.deleteAppUsersById(user.getId());
             log.info("Successfully deleted user: {}", user.getUsername());
         }catch (Exception e){
             log.info("Failed to delete user due to an exception: {}", user.getUsername() ,e);
             user.setIsDeleted(true);
             this.appUserRepository.save(user);
             log.info("Successfully marked user as deleted");
         }
    }

    @Override
    public void logoutUser(Long id) {
        log.info("Logging out user with ID: {}", id);
        try{
            this.cacheService.evictPrincipal(id);
        }catch (Exception e){
            log.info("Failed to remove user principal dto from the cache", e); //Dont throw an exception because its not a sensitive issue
            return;
        }
        log.info("Successfully logged out user with ID: {}", id);
    }

    //Generates a jwt token for the app user, caches the user principal.
    private String cachePrincipalAndGenerateJwtToken(AppUser appUser){
        final AppUserPrincipalDto principal = AppUserPrincipalDto
                .builder()
                .email(appUser.getEmail())
                .id(appUser.getId())
                .role(appUser.getRole())
                .username(appUser.getUsername())
                .build();
        final String jwtToken = jwtService.generateToken(principal);
        cacheService.cachePrincipal(appUser.getId(), principal); //Cache the user principal
        log.info("Successfully logged in app user with email: {}", appUser.getEmail());
        return jwtToken;
    }

}
