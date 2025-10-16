package com.victor.astronaut.appuser;

import com.victor.astronaut.appuser.dtos.AppUserLoginRequest;
import com.victor.astronaut.appuser.dtos.AppUserLoginResponse;
import com.victor.astronaut.appuser.dtos.AppUserRegisterRequest;
import com.victor.astronaut.appuser.impl.AppUserService;
import com.victor.astronaut.security.CookieUtils;
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

    private final AppUserService appUserService;
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
