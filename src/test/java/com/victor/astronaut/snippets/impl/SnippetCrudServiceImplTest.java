package com.victor.astronaut.snippets.impl;

import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.appuser.AppUserQueryService;
import com.victor.astronaut.exceptions.NoSuchSnippetException;
import com.victor.astronaut.exceptions.SnippetPersistenceException;
import com.victor.astronaut.snippets.Snippet;
import com.victor.astronaut.snippets.enums.SnippetLanguage;
import com.victor.astronaut.snippets.repos.SnippetRepository;
import com.victor.astronaut.snippets.dto.SnippetCreationRequest;
import com.victor.astronaut.snippets.dto.SnippetResponse;
import com.victor.astronaut.snippets.dto.SnippetUpdateRequest;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import com.victor.astronaut.snippets.snippetparser.SnippetParsingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class SnippetCrudServiceImplTest {

    @Mock
    private AppUserQueryService appUserQueryService;
    @Mock
    private SnippetRepository snippetRepository;
    @Mock
    private SnippetMapper snippetMapper;
    @Mock
    private SnippetParsingService snippetParsingService;

    @InjectMocks
    private SnippetCrudServiceImpl snippetCrudService;

    private AppUser user;
    private Snippet snippet;
    private SnippetResponse response;

    @BeforeEach
    public void setUp(){
        this.user = AppUser
                .builder()
                .id(1L)
                .email("email")
                .username("username")
                .build();

        this.snippet = Snippet
                .builder()
                .tags(Set.of("tag0", "tag1"))
                .name("name")
                .language(SnippetLanguage.JAVA)
                .isDraft(true)
                .id(1L)
                .build();

        this.response = SnippetResponse
                .builder()
                .id(1L)
                .name("name")
                .tags(Set.of("tag0", "tag1"))
                .language(SnippetLanguage.JAVA.getLanguage())
                .isDraft(true)
                .build();
    }

    @Test
    public void createSnippet_shouldReturnSnippetResponse_onSuccess(){
        //Arrange
        final long id = 1;
        final SnippetCreationRequest request = SnippetCreationRequest
                .builder()
                .snippetName("name")
                .tags(Set.of("tag0", "tag1"))
                .language(SnippetLanguage.JAVA)
                .build();

        when(this.appUserQueryService.findById(id)).thenReturn(this.user);
        when(this.snippetRepository.save(any(Snippet.class))).thenReturn(this.snippet);
        when(this.snippetMapper.toResponse(this.snippet)).thenReturn(this.response);

        //Act
        SnippetResponse resp = this.snippetCrudService.createSnippet(id, request);

        //Assert
        assertNotNull(resp);
        assertEquals(request.snippetName(), resp.name());
        assertEquals(request.language().getLanguage(), resp.language());
    }

    @Test
    public void createSnippet_shouldThrowSnippetPersistenceException_onDataIntegrityException(){
        //Arrange
        final long id = 1;
        final SnippetCreationRequest request = SnippetCreationRequest
                .builder()
                .snippetName("name")
                .tags(Set.of("tag0", "tag1"))
                .language(SnippetLanguage.JAVA)
                .build();

        when(this.appUserQueryService.findById(id)).thenReturn(this.user);
        when(this.snippetRepository.save(any(Snippet.class))).thenThrow(new DataIntegrityViolationException(""));

        //Act
        SnippetPersistenceException ex = assertThrows(SnippetPersistenceException.class, () -> {
           this.snippetCrudService.createSnippet(id, request);
        });


    }

    @Test
    public void createSnippet_shouldThrowSnippetPersistenceException_onGenericException(){
        //Arrange
        final long id = 1;
        final SnippetCreationRequest request = SnippetCreationRequest
                .builder()
                .snippetName("name")
                .tags(Set.of("tag0", "tag1"))
                .language(SnippetLanguage.JAVA)
                .build();

        when(this.appUserQueryService.findById(id)).thenReturn(this.user);
        when(this.snippetRepository.save(any(Snippet.class))).thenThrow(new RuntimeException());

        //Act
        SnippetPersistenceException ex = assertThrows(SnippetPersistenceException.class, () -> {
            this.snippetCrudService.createSnippet(id, request);
        });

    }

    @Test
    public void deleteSnippet_shouldReturnACountOfOne_onDeletion(){
        //Arrange
        final long userId = 1;
        final long snippetId = 1;

        when(this.appUserQueryService.findById(userId)).thenReturn(this.user);
        when(this.snippetRepository.deleteSnippetByAppUserAndId(this.user, snippetId)).thenReturn(1);

        //Act
        this.snippetCrudService.deleteSnippet(userId, snippetId);

        //Assert
        verify(this.appUserQueryService, times(1)).findById(userId);
        verify(this.snippetRepository, times(1)).deleteSnippetByAppUserAndId(this.user, snippetId);
    }

    @Test
    public void deleteSnippet_shouldThrowPersistenceException_givenACountOfZero(){
        //Arrange
        final long userId = 1;
        final long snippetId = 1;

        when(this.appUserQueryService.findById(userId)).thenReturn(this.user);
        when(this.snippetRepository.deleteSnippetByAppUserAndId(this.user, snippetId)).thenReturn(0);

        //Act & Assert
        assertThrows(SnippetPersistenceException.class, () -> {
            this.snippetCrudService.deleteSnippet(userId, snippetId);
        });
        verify(this.appUserQueryService, times(1)).findById(userId);
        verify(this.snippetRepository, times(1)).deleteSnippetByAppUserAndId(this.user, snippetId);
    }


    @Test
    public void updateSnippet_shouldSuccessfullyUpdateSnippet_givenUpdateRequest(){
        //Arrange
        final long snippetId = 1;
        final long userId = 1;
        final SnippetUpdateRequest updateRequest = SnippetUpdateRequest
                .builder()
                .content("content")
                .extraNotes("my_notes")
                .build();

        final SnippetResponse updateResponse = SnippetResponse
                .builder()
                .id(1L)
                .name("name")
                .tags(Set.of("tag0", "tag1"))
                .content("content")
                .extraNotes("my_notes")
                .language(SnippetLanguage.JAVA.getLanguage())
                .isDraft(true)
                .build();

        when(this.appUserQueryService.findById(userId)).thenReturn(this.user);
        when(this.snippetRepository.findSnippetByAppUserAndId(this.user, snippetId)).thenReturn(Optional.of(this.snippet));
        when(this.snippetRepository.save(this.snippet)).thenReturn(this.snippet);
        when(this.snippetMapper.toResponse(this.snippet)).thenReturn(updateResponse);


        //Act
        SnippetResponse response = this.snippetCrudService.updateSnippet(snippetId, userId, updateRequest);

        //Assert
        assertNotNull(response);
        assertEquals(this.snippet.getContent(), response.content());
        assertEquals(this.snippet.getExtraNotes(), response.extraNotes());
        verify(this.snippetParsingService, times(1)).parseSnippetContent(this.snippet);
    }

    @Test
    public void updateSnippet_shouldThrowPersistenceException_onDataIntegrityException(){
        //Arrange
        final long snippetId = 1;
        final long userId = 1;
        final SnippetUpdateRequest updateRequest = SnippetUpdateRequest
                .builder()
                .content("content")
                .extraNotes("my_notes")
                .build();

        final SnippetResponse updateResponse = SnippetResponse
                .builder()
                .id(1L)
                .name("name")
                .tags(Set.of("tag0", "tag1"))
                .content("content")
                .extraNotes("my_notes")
                .language(SnippetLanguage.JAVA.getLanguage())
                .isDraft(true)
                .build();

        when(this.appUserQueryService.findById(userId)).thenReturn(this.user);
        when(this.snippetRepository.findSnippetByAppUserAndId(this.user, snippetId)).thenReturn(Optional.of(this.snippet));
        when(this.snippetRepository.save(this.snippet)).thenThrow(new RuntimeException());


        //Act & Assert
        assertThrows(SnippetPersistenceException.class, () -> {
           this.snippetCrudService.updateSnippet(snippetId, userId, updateRequest);
        });
        //Ensure java parser isnt called
        verify(this.snippetParsingService, times(0)).parseSnippetContent(this.snippet);

    }

    @Test
    public void updateSnippet_shouldNotCallJavaParser_givenOtherLanguageEnum(){
        //Arrange
        final long snippetId = 1;
        final long userId = 1;
        final SnippetUpdateRequest updateRequest = SnippetUpdateRequest
                .builder()
                .content("content")
                .extraNotes("my_notes")
                .build();

        final Snippet otherLanguageSnippet = Snippet
                .builder()
                .id(1L)
                .name("name")
                .tags(Set.of("tag0", "tag1"))
                .content("content")
                .extraNotes("my_notes")
                .language(SnippetLanguage.OTHER)
                .isDraft(true)
                .build();

        final SnippetResponse updateResponse = SnippetResponse
                .builder()
                .id(1L)
                .name("name")
                .tags(Set.of("tag0", "tag1"))
                .content("content")
                .extraNotes("my_notes")
                .language(SnippetLanguage.OTHER.getLanguage())
                .isDraft(true)
                .build();

        when(this.appUserQueryService.findById(userId)).thenReturn(this.user);
        when(this.snippetRepository.findSnippetByAppUserAndId(this.user, snippetId)).thenReturn(Optional.of(otherLanguageSnippet));
        when(this.snippetRepository.save(otherLanguageSnippet)).thenReturn(otherLanguageSnippet);
        when(this.snippetMapper.toResponse(otherLanguageSnippet)).thenReturn(updateResponse);


        //Act
        SnippetResponse response = this.snippetCrudService.updateSnippet(snippetId, userId, updateRequest);

        //Assert
        assertNotNull(response);
        assertEquals(otherLanguageSnippet.getContent(), response.content());
        assertEquals(otherLanguageSnippet.getExtraNotes(), response.extraNotes());
        verify(this.snippetParsingService, times(0)).parseSnippetContent(this.snippet);
    }

    @Test
    public void findById_shouldReturnSnippet_givenId(){
        //Arrange
        final long userId = 1;
        final long snippetId = 1;
        when(this.appUserQueryService.findById(userId)).thenReturn(this.user);
        when(this.snippetRepository.findSnippetByAppUserAndId(this.user, snippetId)).thenReturn(Optional.of(this.snippet));
        when(this.snippetMapper.toResponse(this.snippet)).thenReturn(this.response);

        //Act
        SnippetResponse response = this.snippetCrudService.findById(userId, snippetId);

        //Assert
        assertNotNull(response);
        assertEquals(snippetId, this.snippet.getId());
        verify(this.snippetRepository, times(1)).findSnippetByAppUserAndId(this.user, snippetId);
    }

    @Test
    public void findById_shouldThrowNoSuchSnippetException_givenInvalidId(){
        //Arrange
        final long userId = 1;
        final long snippetId = 9000;
        when(this.appUserQueryService.findById(userId)).thenReturn(this.user);
        when(this.snippetRepository.findSnippetByAppUserAndId(this.user, snippetId)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(NoSuchSnippetException.class, () -> {
             this.snippetCrudService.findById(userId, snippetId);
        });

    }

    @Test
    public void findAllById_shouldReturnSnippetPage_givenUserId(){
        //Arrange
        final long userId = 1;
        final Pageable pageable = PageRequest.of(1, 20);
        when(this.appUserQueryService.findById(userId)).thenReturn(this.user);
        when(this.snippetRepository.findAllByAppUser(this.user, pageable)).thenReturn(Page.empty());

        //Act
        Page<SnippetPreview> responses = this.snippetCrudService.findSnippetsByUser(userId, pageable);

        //Assert
        assertNotNull(responses);
        assertEquals(0, responses.getSize());
        verify(this.snippetRepository, times(1)).findAllByAppUser(this.user, pageable);

    }


}