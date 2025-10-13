package com.victor.astronaut.auth.appuser.impl;

import com.victor.astronaut.auth.appuser.AppUserPrincipal;
import com.victor.astronaut.auth.appuser.AppUserPrincipalCacheService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AppUserPrincipalCacheServiceImpl implements AppUserPrincipalCacheService {

    //Cache's a jwt token
    @CachePut(key = "#token", cacheNames = "${cache.principal.name}")
    @Override
    public AppUserPrincipal cachePrincipal(String token, AppUserPrincipal principal){
        return principal;
    }

    @Cacheable(key = "#token", cacheNames = "${cache.principal.name}")
    @Override
    public AppUserPrincipal getCachedPrincipal(String token) {
        return null;
    }

    @CacheEvict(key = "#id", cacheNames = "${cache.principal.name}")
    @Override
    public void evictPrincipal(Long id){

    }


}
