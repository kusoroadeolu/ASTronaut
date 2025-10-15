package com.victor.astronaut.auth.appuser;

import com.victor.astronaut.auth.AppUser;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Setter
public class AppUserPrincipal implements UserDetails {

    private AppUser appUser;

    public AppUserPrincipal(){

    }


    public AppUserPrincipal(@NonNull AppUser appUser){
        this.appUser = appUser;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(this.appUser.getRole());
    }

    @Override
    public String getPassword() {
        return this.appUser.getPassword();
    }

    @Override
    public String getUsername() {
        return this.appUser.getUsername();
    }

    public Long getId(){
        return this.appUser.getId();
    }

    public String getEmail(){
        return this.appUser.getEmail();
    }
}
