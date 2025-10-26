package com.victor.astronaut.snippets.snippetparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.victor.astronaut.exceptions.SnippetParseException;
import com.victor.astronaut.snippets.entities.Snippet;
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
    private final static String CLASS_WRAPPER = """
                public class %s{
                    %s   \s
                }
           \s""";

    /**
     * Parses the snippet content and extracts available metadata, then saves the snippet.
     * @param snippet the snippet to parse and process
     */
    @Async
    public void parseSnippetContent(Snippet snippet) throws SnippetParseException{
        ParseResult<CompilationUnit> result = this.parseContent(snippet.getContent());

        if(result.getResult().isEmpty()){
            log.info("Parse failed for snippet: {}", snippet.getName());
            return;
        }

        CompilationUnit unit = result.getResult().get();
        log.info("Attempting to parse snippet: {}", snippet.getName());
        this.extractMetaData(snippet, unit);

        //Check if there's a class for this snippet. If not, the snippet didn't parse properly and no metadata was extracted
        if(snippet.getClassNames().isEmpty()){
            log.info("Attempting to reparse snippet: {}", snippet.getName());
            String tempContent = CLASS_WRAPPER.formatted(WRAPPER, snippet.getContent());
            result = this.parseContent(tempContent);

            if(result.getResult().isEmpty()){
                log.info("Reparse failed for snippet: {}", snippet.getName());
                return;
            }

            unit = result.getResult().get();

            this.extractMetaData(snippet, unit);

            //Remove the wrapper class attr
            snippet.getClassNames().remove(WRAPPER.toLowerCase()); //Remove the value as lower case cuz the visitors transform the name to lowercase
            log.info("Successfully reparsed snippet: {}", snippet.getName());
        }

        snippetRepository.save(snippet);
        log.info("Successfully parsed snippet: {}", snippet.getName());
    }

    //A helper method to build the AST of a content
    private ParseResult<CompilationUnit> parseContent(String content){
        final ParserConfiguration configuration = new ParserConfiguration();
        configuration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
        final JavaParser parser = new JavaParser(configuration);
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
    private void extractMetaData(Snippet snippet, CompilationUnit unit){
        visitorOrchestrator.visitAllVisitors(unit, snippet);
        snippet.setMetaDataAvailable(true);

    }



}