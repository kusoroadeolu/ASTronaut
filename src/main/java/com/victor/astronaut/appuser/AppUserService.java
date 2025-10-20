package com.victor.astronaut.appuser;

import com.victor.astronaut.appuser.dtos.*;
import org.springframework.transaction.annotation.Transactional;

public interface AppUserService {

    AppUserAuthResponse registerAppUser(AppUserRegisterRequest registerRequest);

    AppUserAuthResponse loginAppUser(AppUserLoginRequest loginRequest);

    void deleteAppUser(long userId, AppUserDeleteRequest deleteRequest);

    UpdatePreferencesResponse updateAppUserPreferences(long userId, UpdatePreferencesRequest request);

    //Updates a user's email or username
    @Transactional
    void updateUsernameOrEmail(long userId, AppUserUpdateRequest request);

    @Transactional
    void updatePassword(long userId, UpdatePasswordRequest request);
}
