package io.github.kusoroadeolu.astronaut.services;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import io.github.kusoroadeolu.astronaut.entities.SnippetIndex;
import io.github.kusoroadeolu.astronaut.visitors.VisitorOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    private final VisitorOrchestrator visitorOrchestrator;
    private final JavaParser parser;

    /**
     * Parses the snippet content and extracts available metadata, then saves the snippet.
     * @param snippet the snippet to parse and process
     */
    public void parseSnippetContent(SnippetIndex snippet, String content){
        ParseResult<CompilationUnit> result = this.parseContent(content);

        if(result.getResult().isEmpty()){
            log.info("Parse failed for snippet: {}", snippet.getFileName());
            return;
        }

        CompilationUnit unit = result.getResult().get();
        log.info("Attempting to parse snippet: {}", snippet.getFileName());
        extractMetaData(snippet, unit);
        log.info("Successfully parsed snippet: {}", snippet.getFileName());
    }

    //A helper method to build the AST of a content
    private ParseResult<CompilationUnit> parseContent(String content){
        return parser.parse(content);
    }

    /**
     * Extracts metadata from the compilation unit and stores it in the snippet.
     * </br>Runs all visitors to gather structural information (annotations, methods, fields, etc.)
     * and marks the snippet as having available metadata.
     *
     * @param snippet the snippet to populate with metadata
     * @param unit the parsed compilation unit to analyze
     */
    private void extractMetaData(SnippetIndex snippet, CompilationUnit unit){
        visitorOrchestrator.visitAllVisitors(unit, snippet);
    }



}