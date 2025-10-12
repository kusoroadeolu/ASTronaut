package com.victor.astronaut.auth.appuser;

import exceptions.EmailNotFoundException;
import exceptions.NoSuchUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserDetailsServiceImpl implements AppUserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UsernameNotFoundException("Cannot load a user by it's username because usernames are not unique");
    }


    @Override
    public AppUser loadByEmail(String email) {
        return this.appUserRepository.findAppUserByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(String.format("Failed to find a user with email: %s", email)));
    }

    @Override
    public AppUser loadById(Long id) {
        return this.appUserRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException(String.format("Failed to find a user with ID: %s", id)));
    }
}
