package com.victor.astronaut.snippets.snippetparser;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.victor.astronaut.exceptions.SnippetParseException;
import com.victor.astronaut.snippets.Snippet;
import com.victor.astronaut.snippets.repos.SnippetRepository;
import com.victor.astronaut.snippets.snippetparser.visitors.VisitorOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Parses Java code snippets and extracts structural metadata using JavaParser.
 * </br>Attempts to parse the snippet content directly. If parsing fails, wraps the content
 * in a class to handle code fragments. Extracted metadata (annotations, methods, fields, etc.)
 * is stored with the snippet for structural searching.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SnippetParsingService {

    private final SnippetRepository snippetRepository;
    private final VisitorOrchestrator visitorOrchestrator;
    private final static String WRAPPER = "Wrapper";
    private final static String WRAPPER_CLASS = """
                public class Wrapper{
                    %s
                }
            """;

    /**
     * Parses the snippet content and extracts available metadata.
     * </br>First attempts to parse the code as is. If that fails,
     * wraps it in a class and tries again. Sets the metaDataAvailable flag based on
     * whether parsing succeeded, then saves the snippet.
     *
     * @param snippet the snippet to parse and process
     */
    @Async
    public void parseSnippetContent(Snippet snippet) throws SnippetParseException{
        try{
            CompilationUnit unit;
            log.info("Attempting to parse snippet: {}", snippet.getName());
            unit = StaticJavaParser.parse(snippet.getContent());
            this.extractMetaData(snippet, unit);
            log.info("Successfully parsed snippet: {}", snippet.getName());
        }catch (ParseProblemException e){
            log.warn("Parse failed for snippet: {}, error: {}", snippet.getName(), e.getMessage());
            this.wrapAndRetry(snippet);
        }
    }

    /**
     * Wraps the snippet content in a class and retries parsing.
     * </br>Used when initial parsing fails. Wraps the code fragment in a public class
     * to make it valid Java syntax. If successful, removes the wrapper class name
     * from the extracted metadata. If this also fails, marks metadata as unavailable.
     *
     * @param snippet the snippet to wrap and parse
     * @throws SnippetParseException If the reparse fails
     */
    private void wrapAndRetry(Snippet snippet) throws SnippetParseException{
        try{
            final String wrappedContent = this.wrapContent(snippet.getContent());
            final var unit = StaticJavaParser.parse(wrappedContent);
            this.extractMetaData(snippet, unit);
            snippet.getClassNames().remove(WRAPPER); //Remove the wrapper class from the snippet names
            log.info("Successfully wrapped snippet content and extracted meta data");
        }catch (ParseProblemException e){
            log.info("Failed to parse snippet: {} after wrapping it in a class", snippet.getName() ,e);
            snippet.setMetaDataAvailable(false);
            snippetRepository.save(snippet);
            throw new SnippetParseException("Failed to parse snippet: %s. Please re-check the snippet content".formatted(snippet.getName()));
        }
    }

    /**
     * Extracts metadata from the compilation unit and stores it in the snippet.
     * </br>Runs all visitors to gather structural information (annotations, methods, fields, etc.)
     * and marks the snippet as having available metadata.
     *
     * @param snippet the snippet to populate with metadata
     * @param unit the parsed compilation unit to analyze
     */
    private void extractMetaData(Snippet snippet, CompilationUnit unit){
        visitorOrchestrator.visitAllVisitors(unit, snippet);
        snippet.setMetaDataAvailable(true);
        log.info("Successfully extracted available meta data from snippet: {}", snippet.getName());
        snippetRepository.save(snippet);
    }

    /**
     * Wraps code content in a valid Java class structure.
     * </br>Used for parsing code fragments that aren't complete compilation units.
     *
     * @param content the code content to wrap
     * @return the wrapped code as a valid Java class
     */
    private String wrapContent(String content){
        return WRAPPER_CLASS.formatted(content);
    }


}