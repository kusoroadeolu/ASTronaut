package com.victor.astronaut.appuser;

public interface AppUserPrincipalCacheService {
    AppUserPrincipalDto cachePrincipal(long id, AppUserPrincipalDto principal);

    AppUserPrincipalDto getPrincipal(long id);

    void evictPrincipal(long id);
}
