package com.victor.astronaut.auth.appuser;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface AppUserPrincipalCacheService {
    //Cache's a jwt token
    AppUserPrincipal cachePrincipal(String token, AppUserPrincipal principal);

    AppUserPrincipal getCachedPrincipal(String token);

    void evictPrincipal(Long id);
}
