package io.github.kusoroadeolu.astronaut.services;

import io.github.kusoroadeolu.astronaut.CompressionUtils;
import io.github.kusoroadeolu.astronaut.dtos.GistCreationResponse;
import io.github.kusoroadeolu.astronaut.dtos.GistMultiFetchRequest;
import io.github.kusoroadeolu.astronaut.dtos.SnippetCreationRequest;
import io.github.kusoroadeolu.astronaut.dtos.SnippetResponse;
import io.github.kusoroadeolu.astronaut.entities.SnippetIndex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import static io.github.kusoroadeolu.astronaut.CompressionUtils.compressToBase64;
import static io.github.kusoroadeolu.astronaut.CompressionUtils.hash;

@Slf4j
@Service
public class SnippetMapper {

    public SnippetIndex toSnippetIndex(SnippetCreationRequest request, GistCreationResponse response){
        var val = response.files().values().stream().toList();
        return SnippetIndex
                .builder()
                .id(response.id())
                .fileName(request.fileName())
                .description(request.description())
                .tags(request.tags())
                .language(val.getFirst().language())
                .content(compressToBase64(request.content()))
                .contentHash(hash(request.content()))
                .createdAt(response.createdAt())
                .updatedAt(response.createdAt())
                .build();
    }

    public SnippetIndex fromMultiFetchRequest(GistMultiFetchRequest request, String content){
        log.info("Multi fetch request: {}", request);
        var val = request.files().values().stream().toList();
        return SnippetIndex
                .builder()
                .id(request.id())
                .fileName(val.getFirst().filename())
                .description(request.description())
                .tags(new HashSet<>())
                .classNames(new HashSet<>())
                .methodNames(new HashSet<>())
                .language(val.getFirst().language())
                .content(compressToBase64(content))
                .contentHash(hash(content))
                .createdAt(request.createdAt())
                .updatedAt(request.updatedAt())
                .build();
    }

    public SnippetResponse toSnippetResponse(SnippetIndex index) {
       return SnippetResponse
               .builder()
               .id(index.getId())
               .name(index.getFileName())
               .tags(index.getTags())
               .description(index.getDescription())
               .language(index.getLanguage())
               .createdAt(parseDate(index.getCreatedAt()))
               .updatedAt(parseDate(index.getUpdatedAt()))
               .build();
    }


    LocalDateTime parseDate(String date) {
       return ZonedDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME).toLocalDateTime();
    }






}
