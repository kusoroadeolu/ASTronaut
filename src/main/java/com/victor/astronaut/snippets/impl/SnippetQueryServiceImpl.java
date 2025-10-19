package com.victor.astronaut.snippets.impl;

import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.appuser.AppUserQueryService;
import com.victor.astronaut.snippets.Snippet;
import com.victor.astronaut.snippets.SnippetMapper;
import com.victor.astronaut.snippets.SnippetRepository;
import com.victor.astronaut.snippets.dto.SearchFilterDto;
import com.victor.astronaut.snippets.dto.SnippetResponse;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import com.victor.astronaut.snippets.specification.DirectSnippetSpecBuilder;
import com.victor.astronaut.snippets.specification.FuzzySnippetSpecBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SnippetQueryServiceImpl {

    private final SnippetRepository snippetRepository;
    private final AppUserQueryService userQueryService;
    private final SnippetMapper snippetMapper;
    private static final String CLASS_ANNOTATIONS = "classAnnotations";
    private static final String CLASS_NAMES = "classNames";
    private static final String CLASS_FIELDS = "classFields";
    private static final String CLASS_FIELD_ANNOTATIONS = "classFieldAnnotations";
    private static final String METHOD_RETURN_TYPES = "methodReturnTypes";
    private static final String METHOD_ANNOTATIONS = "methodAnnotations";


    /**
     * @return a {@link Page} of {@link SnippetPreview} based on the given criteria from {@link SearchFilterDto}
     * */
    public Page<SnippetPreview> searchBasedOnCriteria(long id, SearchFilterDto filterDto, Pageable pageable){
        final AppUser a = userQueryService.findById(id);
        Specification<Snippet> root = null;

        final Specification<Snippet> directSpecs = this.buildSpec(a, filterDto);
        if(a.getEnableFuzzySearch() != null && a.getEnableFuzzySearch()){
            log.info("Fuzzy search enabled");
            Specification<Snippet> fuzzySpecs = this.buildSpecFuzzy(a, filterDto);
            root = Specification.anyOf(directSpecs, fuzzySpecs);
        }else {
            log.info("Fuzzy search disabled");
            root = directSpecs;
        }

        return this.snippetRepository.findAll(root, pageable)
                .map(this.snippetMapper::toPreview);
    }

    private Specification<Snippet> buildSpec(AppUser user, SearchFilterDto filterDto){
        return DirectSnippetSpecBuilder
                .builder()
                .hasUser(user)
                .hasAnyLanguage(filterDto.languages())
                .hasTagOrName(filterDto.tagsOrNames())
                .hasValFromElementCollection(CLASS_ANNOTATIONS, filterDto.classAnnotations())
                .hasValFromElementCollection(CLASS_NAMES, filterDto.classNames())
                .hasValFromElementCollection(CLASS_FIELDS, filterDto.classFields())
                .hasValFromElementCollection(CLASS_FIELD_ANNOTATIONS, filterDto.classFieldAnnotations())
                .hasValFromElementCollection(METHOD_RETURN_TYPES, filterDto.methodReturnTypes())
                .hasValFromElementCollection(METHOD_ANNOTATIONS, filterDto.methodAnnotations())
                .build();
    }

    private Specification<Snippet> buildSpecFuzzy(AppUser user, SearchFilterDto filterDto){
        return FuzzySnippetSpecBuilder
                .builder()
                .hasUser(user)
                .hasAnyLanguage(filterDto.languages())
                .hasTagOrName(filterDto.tagsOrNames())
                .hasValFromElementCollection(CLASS_ANNOTATIONS, filterDto.classAnnotations())
                .hasValFromElementCollection(CLASS_NAMES, filterDto.classNames())
                .hasValFromElementCollection(CLASS_FIELDS, filterDto.classFields())
                .hasValFromElementCollection(CLASS_FIELD_ANNOTATIONS, filterDto.classFieldAnnotations())
                .hasValFromElementCollection(METHOD_RETURN_TYPES, filterDto.methodReturnTypes())
                .hasValFromElementCollection(METHOD_ANNOTATIONS, filterDto.methodAnnotations())
                .build();
    }



}
