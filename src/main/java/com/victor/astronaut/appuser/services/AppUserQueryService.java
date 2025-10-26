package com.victor.astronaut.appuser.services;

import com.victor.astronaut.appuser.entites.AppUser;
import com.victor.astronaut.exceptions.NoSuchAppUserException;

public interface AppUserQueryService {
    AppUser findById(long id) throws NoSuchAppUserException;

    void validateEmail(String email);
}
