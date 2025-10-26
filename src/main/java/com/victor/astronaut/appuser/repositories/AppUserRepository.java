package com.victor.astronaut.appuser.repositories;

import com.victor.astronaut.appuser.entites.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Modifying
    void deleteAppUsersById(Long id);

    boolean existsAppUsersByEmailAndIsDeletedFalse(String email);

     Optional<AppUser> findAppUserByIdAndIsDeletedFalse(long attr0);

    Optional<AppUser> findAppUserByEmailAndIsDeletedFalse(String email);

    void deleteAppUsersByIsDeletedTrue();
}
