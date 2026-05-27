package io.github.kusoroadeolu.astronaut.snippets.impl;

import com.victor.astronaut.snippets.services.impl.SnippetDiffServiceImpl;
import io.github.kusoroadeolu.astronaut.dtos.diffs.ChangeType;
import io.github.kusoroadeolu.astronaut.dtos.diffs.DiffLine;
import io.github.kusoroadeolu.astronaut.dtos.diffs.SnippetDiff;
import io.github.kusoroadeolu.astronaut.dtos.diffs.SnippetDiffPair;
import io.github.kusoroadeolu.astronaut.entities.SnippetIndex;
import io.github.kusoroadeolu.astronaut.services.SnippetCrudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SnippetDiffServiceImplTest {

    @Mock
    private SnippetCrudService snippetCrudService;

    @InjectMocks
    private SnippetDiffServiceImpl snippetDiffService;

    private SnippetIndex comparing;
    private SnippetIndex comparingTo;

    @BeforeEach
    public void setUp(){
        comparing = SnippetIndex
                .builder()
                .fileName("HMAC JWT Decoder")
                .content("""
                @Bean
                public JwtDecoder jwtDecoder()
                {
                  SecretKey key = Keys.hmac(secret);
                  return NimbusJwtDecoder.withSecretKey(key).build();
                }""")
                .build();

        comparingTo = SnippetIndex
                .builder()
                .fileName("RSA JWT Decoder")
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

        when(snippetCrudService.findBySnippetId(userId, comparingId)).thenReturn(comparing);
        when(snippetCrudService.findBySnippetId(userId, comparingToId)).thenReturn(comparingTo);

        //Act
        SnippetDiffPair pair = snippetDiffService.generateSnippetDiff(userId, comparingId, comparingToId);

        //Assert
        // *** Simple Assertions ***
        assertNotNull(pair);
        final SnippetDiff comparingDiff = pair.comparing();
        final SnippetDiff comparingToDiff = pair.comparingTo();
        assertNotNull(comparingDiff);
        assertNotNull(comparingToDiff);
        assertEquals(comparing.getFileName(), comparingDiff.snippetName());
        assertEquals(comparingTo.getFileName(), comparingToDiff.snippetName());


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