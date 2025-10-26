package com.victor.astronaut.appuser.services;

import com.victor.astronaut.appuser.dtos.AppUserPrincipalDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserDetailsService extends UserDetailsService {
     AppUserPrincipalDto loadById(long id);
}
