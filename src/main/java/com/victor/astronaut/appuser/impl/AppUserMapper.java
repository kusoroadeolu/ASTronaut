package com.victor.astronaut.appuser.impl;

import com.victor.astronaut.appuser.AppUserRole;
import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.appuser.dtos.*;
import org.springframework.stereotype.Service;

@Service
public class AppUserMapper {

    //Maps an app user register request to an AppUser
    public AppUser toAppUser(AppUserRegisterRequest registerRequest, String encodedPassword){
        return AppUser
                .builder()
                .password(encodedPassword)
                .username(registerRequest.username())
                .email(registerRequest.email())
                .role(AppUserRole.APP_USER)
                .isDeleted(false)
                .build();
    }

    public AppUserAuthResponse toPreferencesResponse(AppUser appUser, String jwtToken){
        return new AppUserAuthResponse(appUser.getId(), appUser.getUsername(), appUser.getEmail(), jwtToken);
    }

    public UpdatePreferencesResponse toPreferencesResponse(AppUser appUser){
        return new UpdatePreferencesResponse(appUser.getEnableFuzzySearch());
    }
}
