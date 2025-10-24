package com.victor.astronaut.appuser;

import com.victor.astronaut.appuser.dtos.AppUserLoginRequest;
import com.victor.astronaut.appuser.dtos.AppUserAuthResponse;
import com.victor.astronaut.appuser.dtos.AppUserRegisterRequest;
import com.victor.astronaut.exceptions.ApiError;
import com.victor.astronaut.security.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
@Slf4j
public final class AuthController {

    private final AppUserService appUserService;
    private final CookieUtils cookieUtils;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user", description = "Creates a new account with the required details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = AppUserAuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Password and confirm password fields do not match", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "User with given email already exists", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred during registration", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<AppUserAuthResponse> registerUser(@Valid @RequestBody AppUserRegisterRequest registerRequest, HttpServletResponse response){
        final AppUserAuthResponse loginResponse = this.appUserService.registerAppUser(registerRequest);
        response.addCookie(cookieUtils.createJwtCookie(loginResponse.jwtToken()));
        return new ResponseEntity<>(loginResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Log in a user", description = "Authenticates a user with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = AppUserAuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred during login", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<AppUserAuthResponse> loginUser(@Valid @RequestBody AppUserLoginRequest loginRequest, HttpServletResponse response){
       final AppUserAuthResponse loginResponse = this.appUserService.loginAppUser(loginRequest);
        response.addCookie(cookieUtils.createJwtCookie(loginResponse.jwtToken()));
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}