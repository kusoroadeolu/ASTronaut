package io.github.kusoroadeolu.astronaut.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kusoroadeolu.astronaut.SnippetCache;
import io.github.kusoroadeolu.astronaut.exceptions.IndexPersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexFileService {
    private final ObjectMapper mapper;
    private final SnippetCache cache;
    private static final File file = new File("C:\\Users\\eastw\\Git Projects\\Personal\\ASTronaut\\src\\main\\resources\\index.json");



    public void writeToIndex(){
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, cache.values());
        }catch (IOException e) {
            log.error("An ex occurred while writing to index.json", e);
            throw new IndexPersistenceException("Failed to update index.json file", e);
        }
    }
}
