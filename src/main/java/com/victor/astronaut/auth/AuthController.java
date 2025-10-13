package com.victor.astronaut.auth;

import com.victor.astronaut.auth.appuser.dtos.AppUserLoginRequest;
import com.victor.astronaut.auth.appuser.dtos.AppUserLoginResponse;
import com.victor.astronaut.auth.appuser.dtos.AppUserRegisterRequest;
import com.victor.astronaut.auth.appuser.impl.AppUserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AppUserServiceImpl appUserService;
    private final CookieUtils cookieUtils;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AppUserLoginResponse> registerUser(@Valid @RequestBody AppUserRegisterRequest registerRequest, HttpServletResponse response){
        AppUserLoginResponse loginResponse = this.appUserService.registerAppUser(registerRequest);
        response.addCookie(cookieUtils.addJwtCookie(loginResponse.jwtToken()));
        return new ResponseEntity<>(loginResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AppUserLoginResponse> loginUser(@Valid @RequestBody AppUserLoginRequest loginRequest, HttpServletResponse response){
        AppUserLoginResponse loginResponse = this.appUserService.loginAppUser(loginRequest);
        response.addCookie(cookieUtils.addJwtCookie(loginResponse.jwtToken()));
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

}
