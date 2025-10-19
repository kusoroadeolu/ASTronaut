package com.victor.astronaut.snippets.dto.diffs;

import lombok.Getter;

@Getter
public enum ChangeType{
    UNCHANGED(' '),
    ADDED('+'),
    REMOVED('-');

    private final char changeSymbol;

    ChangeType(char symbol){
        this.changeSymbol = symbol;
    }

}