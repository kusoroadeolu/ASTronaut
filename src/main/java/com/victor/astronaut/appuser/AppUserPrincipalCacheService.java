package com.victor.astronaut.appuser;

public interface AppUserPrincipalCacheService {
    //Cache's a jwt token
    AppUserPrincipalDto cachePrincipal(long id, AppUserPrincipalDto principal);

    AppUserPrincipalDto getPrincipal(long id);

    void evictPrincipal(long id);
}
