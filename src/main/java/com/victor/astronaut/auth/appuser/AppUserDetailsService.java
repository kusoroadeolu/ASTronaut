package com.victor.astronaut.auth.appuser;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserDetailsService extends UserDetailsService {

    public AppUser loadByEmail(String email);

    public AppUser loadById(Long id);

}
