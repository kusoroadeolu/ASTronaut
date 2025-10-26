package com.victor.astronaut.snippets.services.impl;

import com.victor.astronaut.appuser.entites.AppUser;
import com.victor.astronaut.appuser.services.AppUserQueryService;
import com.victor.astronaut.snippets.entities.Snippet;
import com.victor.astronaut.snippets.services.SnippetQueryService;
import com.victor.astronaut.snippets.repos.SnippetRepository;
import com.victor.astronaut.snippets.dtos.SearchCriteria;
import com.victor.astronaut.snippets.projections.SnippetPreview;
import com.victor.astronaut.snippets.specifications.SnippetSpecBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SnippetQueryServiceImpl implements SnippetQueryService {

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
     * @return a {@link Page} of {@link SnippetPreview} based on the given criteria from {@link SearchCriteria}
     * */
    @Override
    public Page<SnippetPreview> searchBasedOnCriteria(long id, @NonNull SearchCriteria criteria, Pageable pageable){
        final AppUser a = this.userQueryService.findById(id);
        final Specification<Snippet> root;

        final Specification<Snippet> directSpecs = this.buildSpec(a, criteria, SnippetSpecBuilder.SpecType.DIRECT);
        if(a.getEnableFuzzySearch() != null && a.getEnableFuzzySearch()){
            log.info("Fuzzy search enabled");
            final Specification<Snippet> fuzzySpecs = this.buildSpec(a, criteria, SnippetSpecBuilder.SpecType.FUZZY);
            //A specification which is any of the direct specs and the fuzzy specs. Basically OR not AND
            root = Specification.anyOf(directSpecs, fuzzySpecs);
        }else {
            log.info("Fuzzy search disabled");
            root = directSpecs;
        }

        return this.snippetRepository.findAll(root, pageable)
                .map(this.snippetMapper::toPreview);
    }

    public Specification<Snippet> buildSpec(AppUser user, SearchCriteria filterDto, SnippetSpecBuilder.SpecType type){
        return SnippetSpecBuilder
                .typeOf(type)
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
