package com.victor.astronaut.auth.appuser;

import com.victor.astronaut.auth.appuser.dtos.AppUserLoginRequest;
import com.victor.astronaut.auth.appuser.dtos.AppUserLoginResponse;
import com.victor.astronaut.auth.appuser.dtos.AppUserRegisterRequest;
import com.victor.astronaut.auth.appuser.entities.AppUser;
import lombok.NonNull;
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

    public AppUserLoginResponse toResponse(AppUser appUser){
        return new AppUserLoginResponse(appUser.getUsername(), appUser.getEmail());
    }

    public AppUserLoginRequest toLoginRequest(AppUser appUser) {
        return new AppUserLoginRequest(appUser.getEmail(), appUser.getPassword());
    }
}
