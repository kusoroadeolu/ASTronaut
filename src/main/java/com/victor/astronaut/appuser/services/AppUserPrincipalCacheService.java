package com.victor.astronaut.appuser.services;

import com.victor.astronaut.appuser.dtos.AppUserPrincipalDto;

public interface AppUserPrincipalCacheService {
    AppUserPrincipalDto cachePrincipal(long id, AppUserPrincipalDto principal);

    AppUserPrincipalDto getPrincipal(long id);

    void evictPrincipal(long id);
}
