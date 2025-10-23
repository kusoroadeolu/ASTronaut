package com.victor.astronaut.appuser.scheduled;

import com.victor.astronaut.appuser.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserSoftDeleteScheduler {

    private final AppUserRepository appUserRepository;

    /**
     * A simple scheduled method which runs every saturday and checks for soft deleted users
     * */
    @Scheduled(cron = "0 0 0 * * Sat")
    public void periodicallyDeleteSoftDeletedUsers(){
        log.info("Checking for soft deleted users");
        try{
            this.appUserRepository.deleteAppUsersByIsDeletedTrue();
            log.info("Successfully hard deleted all soft deleted app users");
        }catch (Exception e){
            log.error("An unexpected error occurred while trying to hard delete soft deleted users", e);
        }
    }

}
