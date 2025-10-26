package com.victor.astronaut.appuser.services;

import com.victor.astronaut.appuser.dtos.*;
import org.springframework.transaction.annotation.Transactional;

public interface AppUserService {

    AppUserAuthResponse registerAppUser(AppUserRegisterRequest registerRequest);

    AppUserAuthResponse loginAppUser(AppUserLoginRequest loginRequest);

    void deleteAppUser(long userId, AppUserDeleteRequest deleteRequest);

    UpdatePreferencesResponse updateAppUserPreferences(long userId, UpdatePreferencesRequest request);

    void updateUsernameOrEmail(long userId, AppUserUpdateRequest request);

    void updatePassword(long userId, UpdatePasswordRequest request);

    void logoutUser(Long id);

    @Transactional(readOnly = true)
    UpdatePreferencesResponse getUserPreferences(long userId);
}
