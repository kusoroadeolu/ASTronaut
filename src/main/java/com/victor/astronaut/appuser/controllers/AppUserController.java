package com.victor.astronaut.appuser.controllers;

import com.victor.astronaut.appuser.dtos.*;
import com.victor.astronaut.appuser.entites.AppUserPrincipal;
import com.victor.astronaut.appuser.services.AppUserService;
import com.victor.astronaut.exceptions.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "User account management endpoints")
@Slf4j
@PreAuthorize("hasAnyRole('APP_USER', 'APP_ADMIN')")
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "User is not authenticated", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content(schema = @Schema(implementation = ApiError.class)))
})
public class AppUserController {

    private final AppUserService appUserService;

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete current user", description = "Deletes the authenticated user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Password confirmation doesn't match", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> deleteAppUser(@AuthenticationPrincipal AppUserPrincipal principal, @Valid @RequestBody AppUserDeleteRequest request){
        this.appUserService.deleteAppUser(principal.getId(), request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Log out current user", description = "Logs out the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User logged out successfully")
    })
    public ResponseEntity<Void> logoutUser(@AuthenticationPrincipal AppUserPrincipal principal){
        this.appUserService.logoutUser(principal.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/preferences")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Gets user preferences", description = "Gets user preferences (e.g., fuzzy search toggle)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferences gotten successfully", content = @Content(schema = @Schema(implementation = UpdatePreferencesResponse.class)))
    })
    public ResponseEntity<UpdatePreferencesResponse> getUserPreferences(@AuthenticationPrincipal AppUserPrincipal principal){
        final var response = this.appUserService.getUserPreferences(principal.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/preferences")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user preferences", description = "Updates user preferences (e.g., fuzzy search toggle)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferences updated successfully", content = @Content(schema = @Schema(implementation = UpdatePreferencesResponse.class)))
    })
    public ResponseEntity<UpdatePreferencesResponse> updateUserPreferences(@AuthenticationPrincipal AppUserPrincipal principal, @Valid @RequestBody UpdatePreferencesRequest request){
        final var response = this.appUserService.updateAppUserPreferences(principal.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update username or email", description = "Updates the authenticated user's username or email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details updated successfully"),
            @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> updateUsernameOrEmail(@AuthenticationPrincipal AppUserPrincipal principal, @Valid @RequestBody AppUserUpdateRequest request){
        this.appUserService.updateUsernameOrEmail(principal.getId(), request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update password", description = "Updates the authenticated user's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid current password or password confirmation doesn't match", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal AppUserPrincipal principal, @Valid @RequestBody UpdatePasswordRequest request){
        this.appUserService.updatePassword(principal.getId(), request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}