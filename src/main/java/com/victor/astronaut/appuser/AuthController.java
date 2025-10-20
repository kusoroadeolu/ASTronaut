package com.victor.astronaut.appuser;

import com.victor.astronaut.appuser.dtos.AppUserLoginRequest;
import com.victor.astronaut.appuser.dtos.AppUserAuthResponse;
import com.victor.astronaut.appuser.dtos.AppUserRegisterRequest;
import com.victor.astronaut.security.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Slf4j
public final class AuthController {

    private final AppUserService appUserService;
    private final CookieUtils cookieUtils;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AppUserAuthResponse> registerUser(@Valid @RequestBody AppUserRegisterRequest registerRequest, HttpServletResponse response){
        AppUserAuthResponse loginResponse = this.appUserService.registerAppUser(registerRequest);
        response.addCookie(cookieUtils.createJwtCookie(loginResponse.jwtToken()));
        return new ResponseEntity<>(loginResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AppUserAuthResponse> loginUser(@Valid @RequestBody AppUserLoginRequest loginRequest, HttpServletResponse response){
        AppUserAuthResponse loginResponse = this.appUserService.loginAppUser(loginRequest);
        response.addCookie(cookieUtils.createJwtCookie(loginResponse.jwtToken()));
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }




}
