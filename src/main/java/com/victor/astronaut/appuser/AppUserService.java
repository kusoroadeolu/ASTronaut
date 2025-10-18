package com.victor.astronaut.appuser;

import com.victor.astronaut.appuser.dtos.AppUserLoginRequest;
import com.victor.astronaut.appuser.dtos.AppUserAuthResponse;
import com.victor.astronaut.appuser.dtos.AppUserRegisterRequest;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface AppUserService {
    @Transactional
    AppUserAuthResponse registerAppUser(@NonNull AppUserRegisterRequest registerRequest);

    @Transactional(readOnly = true)
    AppUserAuthResponse loginAppUser(@NonNull AppUserLoginRequest loginRequest);
}
