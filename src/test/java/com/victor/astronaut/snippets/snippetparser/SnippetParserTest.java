package com.victor.astronaut.snippets.snippetparser;

import com.victor.astronaut.exceptions.SnippetParseException;
import com.victor.astronaut.snippets.Snippet;
import com.victor.astronaut.snippets.SnippetRepository;
import com.victor.astronaut.snippets.snippetparser.visitors.VisitorOrchestrator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class SnippetParserTest {
    @MockitoBean
    private SnippetRepository snippetRepository;

    @Autowired
    private VisitorOrchestrator visitorOrchestrator;

    @Autowired
    private SnippetParser parser;

    //This test will only pass with the async annotation commented out on the actual method
    @Test
    public void parseSnippetContent_shouldSuccessfullyParseSnippetContent_givenFormattedJavaCode() {
        String content = """
                public class Snippet{
                    int a  = 0;
                    int b = 0;
                }
                """;


        Snippet snippet = Snippet
                .builder()
                .content(content)
                .build();


        //Act
        this.parser.parseSnippetContent(snippet);

        //Assert
        assertEquals(1, snippet.getClassNames().size());
        assertEquals(2, snippet.getClassFields().size());
        verify(snippetRepository, times(1)).save(snippet);
        assertTrue(snippet.getMetaDataAvailable());
    }

    //This test will only pass with the async annotation commented out on the actual method
    @Test
    public void parseSnippetContent_shouldSuccessfullyParseSnippetContent_afterWrappingJavaCodeInClass() throws InterruptedException {
        String content = """
                public void hey(){
                    IO.println("Hey");
                }
                """;


        Snippet snippet = Snippet
                .builder()
                .content(content)
                .build();


        //Act
        this.parser.parseSnippetContent(snippet);

        //Assert
        assertEquals(1, snippet.getMethodReturnTypes().size());
        assertEquals(0, snippet.getClassNames().size());
        verify(snippetRepository, times(1)).save(snippet);
        assertTrue(snippet.getMetaDataAvailable());
    }

    @Test
    public void parseSnippetContent_shouldThrowSnippetParseException_givenInvalidCode() throws InterruptedException {
        String content = """
                public class Snippet{
                    int a  = 0;
                    int b = 0;
                
                """;


        Snippet snippet = Snippet
                .builder()
                .content(content)
                .build();


        //Act & Assert
        assertThrows(SnippetParseException.class, () -> this.parser.parseSnippetContent(snippet));
        verify(snippetRepository, times(1)).save(snippet);
        assertFalse(snippet.getMetaDataAvailable());
    }


}