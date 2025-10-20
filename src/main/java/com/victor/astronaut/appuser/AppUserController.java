package com.victor.astronaut.appuser;

import com.victor.astronaut.appuser.dtos.AppUserDeleteRequest;
import com.victor.astronaut.appuser.dtos.UpdatePreferencesRequest;
import com.victor.astronaut.appuser.dtos.UpdatePreferencesResponse;
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
@Slf4j
@PreAuthorize("hasAnyRole('APP_USER', 'APP_ADMIN')")
public class AppUserController {

    private final AppUserService appUserService;

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAppUser(@AuthenticationPrincipal AppUserPrincipal principal, @RequestBody AppUserDeleteRequest request){
        this.appUserService.deleteAppUser(principal.getId(), request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/preferences")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UpdatePreferencesResponse> updateUserPreferences(@AuthenticationPrincipal AppUserPrincipal principal, @RequestBody UpdatePreferencesRequest request){
        var response = this.appUserService.updateAppUserPreferences(principal.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
