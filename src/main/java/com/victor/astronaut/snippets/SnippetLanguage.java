package com.victor.astronaut.snippets;

public enum SnippetLanguage {
    JAVA("JAVA"),
    OTHER("OTHER");

    private final String language;

     SnippetLanguage(String lang){
        this.language = lang;
    }

    public String getLanguage(){
         return this.language;
    }

}
