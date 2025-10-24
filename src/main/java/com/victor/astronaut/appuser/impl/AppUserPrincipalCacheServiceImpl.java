package com.victor.astronaut.appuser.impl;

import com.victor.astronaut.appuser.AppUserPrincipalDto;
import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.appuser.AppUserPrincipalCacheService;
import com.victor.astronaut.appuser.repositories.AppUserRepository;
import com.victor.astronaut.exceptions.NoSuchAppUserException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserPrincipalCacheServiceImpl implements AppUserPrincipalCacheService {

    private final AppUserRepository appUserRepository;

    //Caches the needed details to build the app principal
    @CachePut(key = "#id", cacheNames = "principalCache")
    @Override
    public AppUserPrincipalDto cachePrincipal(long id, @NonNull AppUserPrincipalDto principal){
        return principal;
    }

    @Cacheable(key = "#id", cacheNames = "principalCache")
    @Override
    public AppUserPrincipalDto getPrincipal(long id) {
        AppUser user = this.appUserRepository.findById(id).orElseThrow(
                () -> new NoSuchAppUserException("Failed to find user with ID: %s in the DB".formatted(id))
        );

        return AppUserPrincipalDto
                .builder()
                .email(user.getEmail())
                .role(user.getRole())
                .username(user.getUsername())
                .id(user.getId())
                .build();
    }

    @CacheEvict(key = "#id", cacheNames = "principalCache")
    @Override
    public void evictPrincipal(long id){

    }


}
