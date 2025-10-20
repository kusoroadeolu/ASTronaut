package com.victor.astronaut.appuser;

import com.victor.astronaut.appuser.dtos.*;

public interface AppUserService {

    AppUserAuthResponse registerAppUser(AppUserRegisterRequest registerRequest);

    AppUserAuthResponse loginAppUser(AppUserLoginRequest loginRequest);

    void deleteAppUser(long userId, AppUserDeleteRequest deleteRequest);

    UpdatePreferencesResponse updateAppUserPreferences(long userId, UpdatePreferencesRequest request);
}
