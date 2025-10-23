package com.victor.astronaut.snippets.enums;

import lombok.Getter;

@Getter
public enum SnippetLanguage {
    JAVA("JAVA"),
    OTHER("OTHER");

    private final String language;

     SnippetLanguage(String lang){
        this.language = lang;
    }
}
