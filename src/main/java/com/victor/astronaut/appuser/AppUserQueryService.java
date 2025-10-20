package com.victor.astronaut.appuser;

import com.victor.astronaut.exceptions.NoSuchUserException;

public interface AppUserQueryService {
    AppUser findById(long id) throws NoSuchUserException;

    void validateEmail(String email);
}
