package com.victor.astronaut.appuser;

import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Setter
public class AppUserPrincipal implements UserDetails {

    private final AppUserPrincipalDto principalDto;


    private AppUserPrincipal(){
        this.principalDto = null;
    }

    public AppUserPrincipal(AppUserPrincipalDto principalDto){
        this.principalDto = principalDto;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(this.principalDto.role());
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.principalDto.username();
    }


    public Long getId(){
        return this.principalDto.id();
    }

    public String getEmail(){
        return this.principalDto.email();
    }
}
