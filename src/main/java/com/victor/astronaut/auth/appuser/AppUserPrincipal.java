package com.victor.astronaut.auth.appuser;

import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;

@Setter
public class AppUserPrincipal implements UserDetails {

    private final AppUser appUser;

    private AppUserPrincipal(){
        this.appUser = null;
    }

    public AppUserPrincipal(@NonNull AppUser appUser){
        this.appUser = appUser;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(appUser.getRole());
    }

    @Override
    public String getPassword() {
        return appUser.getPassword();
    }

    @Override
    public String getUsername() {
        throw new UsernameNotFoundException("");
    }

    public Long getId(){
        return appUser.getId();
    }

    public String getEmail(){
        return appUser.getEmail();
    }
}
