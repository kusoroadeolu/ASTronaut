package io.github.kusoroadeolu.astronaut.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kusoroadeolu.astronaut.SnippetCache;
import io.github.kusoroadeolu.astronaut.exceptions.IndexPersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexFileService {
    private final ObjectMapper mapper;
    private final SnippetCache cache;
    @Value("${index.file-path}")
    private String indexPath;



    public void writeToIndex(){
        try {
            var file = Path.of(indexPath).toFile();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, cache.values());
        }catch (IOException e) {
            log.error("An ex occurred while writing to index.json", e);
            throw new IndexPersistenceException("Failed to update index.json file", e);
        }
    }
}
