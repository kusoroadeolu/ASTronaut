package com.victor.astronaut.appuser.impl;

import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.appuser.repositories.AppUserRepository;
import com.victor.astronaut.appuser.AppUserQueryService;
import com.victor.astronaut.exceptions.AppUserAlreadyExistsException;
import com.victor.astronaut.exceptions.NoSuchUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserQueryServiceImpl implements AppUserQueryService {

    private final AppUserRepository appUserRepository;

    /**
     * Finds an app user based on the ID
     * @return The app user with the given ID
     * @throws com.victor.astronaut.exceptions.NoSuchUserException If no app user with the given ID was found
     * */
    @Override
    public AppUser findById(long id) throws NoSuchUserException{
        return appUserRepository.findAppUserByIdAndIsDeletedFalse(id).orElseThrow(() -> new NoSuchUserException(String.format("Failed to find a user with ID: %s", id)));
    }

    @Override
    public void validateEmail(String email){
        if (this.appUserRepository.existsAppUsersByEmailAndIsDeletedFalse(email)){
            log.info("Found user with similar email address.");
            throw new AppUserAlreadyExistsException("This email address is already taken. Please use a different email");
        }
    }

}
