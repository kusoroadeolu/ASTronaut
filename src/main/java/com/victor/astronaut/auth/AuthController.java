package com.victor.astronaut.auth;

import com.victor.astronaut.auth.appuser.dtos.AppUserLoginRequest;
import com.victor.astronaut.auth.appuser.dtos.AppUserLoginResponse;
import com.victor.astronaut.auth.appuser.dtos.AppUserRegisterRequest;
import com.victor.astronaut.auth.appuser.impl.AppUserServiceImpl;
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

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AppUserLoginResponse> registerUser(@Valid @RequestBody AppUserRegisterRequest registerRequest){
        return new ResponseEntity<>(this.appUserService.registerAppUser(registerRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AppUserLoginResponse> loginUser(@Valid @RequestBody AppUserLoginRequest loginRequest){
        return new ResponseEntity<>(this.appUserService.loginAppUser(loginRequest), HttpStatus.CREATED);
    }

}
