package com.victor.astronaut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.FileNotFoundException;

@EnableAsync
@SpringBootApplication
public class ASTronautApplication {

    public static void main(String[] args) throws FileNotFoundException {
        SpringApplication.run(ASTronautApplication.class, args);




    }

}
