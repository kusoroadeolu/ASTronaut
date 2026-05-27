package io.github.kusoroadeolu.astronaut.config;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import io.github.kusoroadeolu.astronaut.SnippetCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MiscConfig {

    @Value("${github.token}")
    private String patToken;

    @Bean
    public SnippetCache cache() {
        return new SnippetCache();
    }

    @Bean
    public RestClient client() {
        return RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Authorization", "Bearer %s".formatted(patToken))
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2026-03-10")
                .build();
    }

    @Bean
    public JavaParser javaParser() {
        ParserConfiguration configuration = new ParserConfiguration();
        configuration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
        return new JavaParser(configuration);

    }
}
