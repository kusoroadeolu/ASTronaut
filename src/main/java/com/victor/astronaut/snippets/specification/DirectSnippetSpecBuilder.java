package com.victor.astronaut.snippets.specification;

import com.victor.astronaut.appuser.AppUser;
import com.victor.astronaut.snippets.Snippet;
import com.victor.astronaut.snippets.enums.SnippetLanguage;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DirectSnippetSpecBuilder implements SnippetSpecBuilder {

    private final static String TAGS = "tags";
    private final static String NAME = "name";
    private final static String LANGUAGE = "language";
    private final List<Specification<Snippet>> specifications;
    private DirectSnippetSpecBuilder(){
        this.specifications = new ArrayList<>();
    }

    public static DirectSnippetSpecBuilder builder(){
        return new DirectSnippetSpecBuilder();
    }

    /**
     * Builds a specification to get all snippets that belong to the given app user
     * @param a The {@link AppUser} to get the snippet for
     * @return an instance of this class
     * */
    @Override
    public DirectSnippetSpecBuilder hasUser(AppUser a){
        final Specification<Snippet> spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("appUser"), a);
        this.specifications.add(spec);
        return this;
    }

    /**
     * Builds a specification to get all snippets which contain any of the names in the set
     * @param expectedLangs A set of expected names
     * @return an instance of this class
     * */
    @Override
    public DirectSnippetSpecBuilder hasAnyLanguage(Set<SnippetLanguage> expectedLangs){
        if(!expectedLangs.isEmpty()){
            final Specification<Snippet> spec = this.hasLanguageInSet(LANGUAGE, expectedLangs);
            this.specifications.add(spec);
        }
        return this;
    }


    /**
     * Builds a specification to get all snippets whose field contains any of the values in the given set
     * @param fieldName The field we are filtering from
     * @param expectedVals The set we are filtering with
     * @return an instance of this class
     * */
    @Override
    public DirectSnippetSpecBuilder hasValFromElementCollection(String fieldName, Set<String> expectedVals){
        if(!fieldName.isEmpty() && !expectedVals.isEmpty()){
            final Specification<Snippet> spec = this.hasValFromElementCollectionInSet(fieldName, expectedVals);
            this.specifications.add(spec);
        }
        return this;
    }


    //Checks for any snippet that has a tag or a name from the expected tags or names
    @Override
    public DirectSnippetSpecBuilder hasTagOrName(Set<String> expectedTagsOrNames){
        if (!expectedTagsOrNames.isEmpty()){
            final Specification<Snippet> hasTags = this.hasValFromElementCollectionInSet(TAGS, expectedTagsOrNames);
            final Specification<Snippet> hasNames = this.hasNameInSet(NAME, expectedTagsOrNames);
            final Specification<Snippet> spec = Specification.anyOf(hasNames, hasTags);
            this.specifications.add(spec);
        }
        return this;
    }

    @Override
    public Specification<Snippet> build(){
        return Specification.allOf(this.specifications);
    }


    //Checks if an entity in the DB has a value in their set(element collection) that matches the values in this set
    private Specification<Snippet> hasValFromElementCollectionInSet(String fieldName, Set<String> set){
        final Set<String> lowerSet = set.stream().map(String::toLowerCase).collect(Collectors.toSet());
        return (root, query, cb) -> root.join(fieldName).in(lowerSet);
    }


    //Checks if an entity has a name that exists in the expected names set
    private Specification<Snippet> hasNameInSet(String fieldName, Set<String> set){
        final Set<String> lowerSet = set.stream().map(String::toLowerCase).collect(Collectors.toSet());
        return  (root, query, cb) -> cb.lower(root.get(fieldName)).in(lowerSet);
    }

    //Checks if an entity has a name that exists in the expected language set
    private Specification<Snippet> hasLanguageInSet(String fieldName, Set<SnippetLanguage> expectedLangs){
        return  (root, query, cb) -> root.get(fieldName).in(expectedLangs);
    }



}
