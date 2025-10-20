package com.victor.astronaut.appuser;

import com.victor.astronaut.exceptions.NoSuchAppUserException;

public interface AppUserQueryService {
    AppUser findById(long id) throws NoSuchAppUserException;

    void validateEmail(String email);
}
