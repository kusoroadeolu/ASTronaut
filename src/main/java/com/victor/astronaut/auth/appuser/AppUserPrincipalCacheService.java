package com.victor.astronaut.auth.appuser;

public interface AppUserPrincipalCacheService {
    //Cache's a jwt token
    AppUserPrincipal cachePrincipal(long id, AppUserPrincipal principal);

    AppUserPrincipal getPrincipal(long id);

    void evictPrincipal(long id);
}
