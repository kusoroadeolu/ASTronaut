package com.victor.astronaut.snippets.impl;

import com.victor.astronaut.snippets.Snippet;
import com.victor.astronaut.snippets.SnippetCrudService;
import com.victor.astronaut.snippets.dto.diffs.ChangeType;
import com.victor.astronaut.snippets.dto.diffs.DiffLine;
import com.victor.astronaut.snippets.dto.diffs.SnippetDiff;
import com.victor.astronaut.snippets.dto.diffs.SnippetDiffPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SnippetDiffServiceImplTest {

    @Mock
    private SnippetCrudService snippetCrudService;

    @InjectMocks
    private SnippetDiffServiceImpl snippetDiffService;

    private Snippet comparing;
    private Snippet comparingTo;

    @BeforeEach
    public void setUp(){
        comparing = Snippet
                .builder()
                .name("HMAC JWT Decoder")
                .content("""
                @Bean
                public JwtDecoder jwtDecoder()
                {
                  SecretKey key = Keys.hmac(secret);
                  return NimbusJwtDecoder.withSecretKey(key).build();
                }""")
                .build();

        comparingTo = Snippet
                .builder()
                .name("RSA JWT Decoder")
                .content("""
                @Bean
                public JwtDecoder jwtDecoder()
                {
                  RSAPublicKey key = loadRSAPublicKey();
                  return NimbusJwtDecoder.withPublicKey(key).build();
                }""")
                .build();
    }

    @Test
    void generateSnippetDiff_shouldReturnASnippetDiffPair_givenTwoSnippets() {
        //Arrange
        final long userId = 1L;
        final long comparingId = 1L;
        final long comparingToId = 2L;

        when(snippetCrudService.findByAppUserIdAndId(userId, comparingId)).thenReturn(comparing);
        when(snippetCrudService.findByAppUserIdAndId(userId, comparingToId)).thenReturn(comparingTo);

        //Act
        SnippetDiffPair pair = snippetDiffService.generateSnippetDiff(userId, comparingId, comparingToId);

        //Assert
        // *** Simple Assertions ***
        assertNotNull(pair);
        final SnippetDiff comparingDiff = pair.comparing();
        final SnippetDiff comparingToDiff = pair.comparingTo();
        assertNotNull(comparingDiff);
        assertNotNull(comparingToDiff);
        assertEquals(comparing.getName(), comparingDiff.snippetName());
        assertEquals(comparingTo.getName(), comparingToDiff.snippetName());


        // *** Asserting the structure of the snippet diffs ***
        long removedCount = comparingDiff.lines().stream().map(DiffLine::lineType).filter(t -> t == ChangeType.REMOVED).count();
        long addedCount = comparingToDiff.lines().stream().map(DiffLine::lineType).filter(t -> t == ChangeType.ADDED).count();
        long unchangedCountComparing = comparingDiff.lines().stream().map(DiffLine::lineType).filter(t -> t == ChangeType.UNCHANGED).count();
        long unchangedCountComparingTo = comparingToDiff.lines().stream().map(DiffLine::lineType).filter(t -> t == ChangeType.UNCHANGED).count();
        assertEquals(2, removedCount);
        assertEquals(2, addedCount);
        assertEquals(4, unchangedCountComparingTo);
        assertEquals(4, unchangedCountComparing);


    }
}