package com.victor.astronaut.appuser;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserDetailsService extends UserDetailsService {

     AppUserPrincipalDto loadById(long id);

}
