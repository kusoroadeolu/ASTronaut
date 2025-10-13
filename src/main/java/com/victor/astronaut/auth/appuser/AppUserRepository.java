package com.victor.astronaut.auth.appuser;

import com.victor.astronaut.auth.appuser.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findAppUserByEmail(String email);

    boolean existsAppUsersByEmail(String email);

    boolean existsAppUsersByEmailAndPassword(String email, String password);

}
