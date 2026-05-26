package io.github.kusoroadeolu.astronaut;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kusoroadeolu.astronaut.entities.SnippetIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class ASTronautApplication {


    public static void main(String[] args) {
      SpringApplication.run(ASTronautApplication.class, args);
    }

    @Component
    @RequiredArgsConstructor
    public static class SnippetCacheLoader implements ApplicationRunner {

        private final SnippetCache cache;
        private final ObjectMapper mapper;

        @Value("${index.path}")
        private String path;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            ClassPathResource rs = new ClassPathResource(path);
            List<SnippetIndex> snippets = mapper.readValue(rs.getInputStream(), new TypeReference<>(){});
            cache.addAll(snippets == null ? List.of() : snippets);

        }
    }

}
