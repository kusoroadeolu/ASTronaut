package com.victor.astronaut.appuser.impl;

import com.victor.astronaut.appuser.dtos.AppUserLoginRequest;
import com.victor.astronaut.appuser.dtos.AppUserLoginResponse;
import com.victor.astronaut.appuser.dtos.AppUserRegisterRequest;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface AppUserService {
    @Transactional
    AppUserLoginResponse registerAppUser(@NonNull AppUserRegisterRequest registerRequest);

    @Transactional(readOnly = true)
    AppUserLoginResponse loginAppUser(@NonNull AppUserLoginRequest loginRequest);
}
