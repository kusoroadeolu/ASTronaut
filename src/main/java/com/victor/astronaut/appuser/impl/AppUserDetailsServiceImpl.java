package com.victor.astronaut.appuser.impl;

import com.victor.astronaut.appuser.AppUserPrincipalDto;
import com.victor.astronaut.appuser.AppUserPrincipalCacheService;
import com.victor.astronaut.appuser.AppUserDetailsService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserDetailsServiceImpl implements AppUserDetailsService {

    private final AppUserPrincipalCacheService appUserPrincipalCacheService;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        throw new UsernameNotFoundException("Cannot load a user by it's username because usernames are not unique");
    }

    @Override
    public AppUserPrincipalDto loadById(long id) {
       return this.appUserPrincipalCacheService.getPrincipal(id);
    }

}
