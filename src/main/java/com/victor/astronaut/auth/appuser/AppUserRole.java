package com.victor.astronaut.auth.appuser;

import org.springframework.security.core.GrantedAuthority;

public enum AppUserRole implements GrantedAuthority {

    APP_USER("ROLE_APP_USER"),
    APP_ADMIN("ROLE_APP_ADMIN");


    private final String authority;

    AppUserRole(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
