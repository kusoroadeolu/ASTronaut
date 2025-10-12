package com.victor.astronaut.auth.appuser;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserDetailsService extends UserDetailsService {

     AppUserPrincipal loadByEmail(String email);

     AppUserPrincipal loadById(Long id);

}
