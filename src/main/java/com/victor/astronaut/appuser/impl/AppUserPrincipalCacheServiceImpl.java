package com.victor.astronaut.appuser.impl;

import com.victor.astronaut.appuser.AppUserPrincipal;
import com.victor.astronaut.appuser.AppUserPrincipalCacheService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AppUserPrincipalCacheServiceImpl implements AppUserPrincipalCacheService {

    //Cache's a jwt token
    @CachePut(key = "#id", cacheNames = "principalCache")
    @Override
    public AppUserPrincipal cachePrincipal(long id, AppUserPrincipal principal){
        return principal;
    }

    @Cacheable(key = "#id", cacheNames = "principalCache")
    @Override
    public AppUserPrincipal getPrincipal(long id) {
        return null;
    }

    @CacheEvict(key = "#id", cacheNames = "principalCache")
    @Override
    public void evictPrincipal(long id){

    }


}
