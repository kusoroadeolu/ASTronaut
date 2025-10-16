package com.victor.astronaut.appuser.impl;

import com.victor.astronaut.appuser.AppUserRole;
import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.appuser.dtos.AppUserLoginRequest;
import com.victor.astronaut.appuser.dtos.AppUserLoginResponse;
import com.victor.astronaut.appuser.dtos.AppUserRegisterRequest;
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
                .build();
    }

    public AppUserLoginResponse toResponse(AppUser appUser, String jwtToken){
        return new AppUserLoginResponse(appUser.getUsername(), appUser.getEmail(), jwtToken);
    }

    public AppUserLoginRequest toLoginRequest(AppUser appUser) {
        return new AppUserLoginRequest(appUser.getEmail(), appUser.getPassword());
    }
}
