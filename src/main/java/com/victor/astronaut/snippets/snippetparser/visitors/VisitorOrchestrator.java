package com.victor.astronaut.snippets.snippetparser.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.victor.astronaut.snippets.Snippet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public final class VisitorOrchestrator {

    private final ClassAnnotationVisitor classAnnotationVisitor;
    private final ClassFieldVisitor classFieldVisitor;
    private final ClassFieldAnnotationVisitor classFieldAnnotationVisitor;
    private final ClassNameVisitor classNameVisitor;
    private final MethodAnnotationVisitor methodAnnotationVisitor;
    private final MethodReturnTypeVisitor methodReturnTypeVisitor;

    /**
     * Visits the compilation unit and extracts all code metadata into the snippet.
     *
     * This method runs all visitors to gather information about the code, including
     * class annotations, fields, class names, method annotations, and return types.
     * The extracted data is then stored in the provided snippet object.
     *
     * @param unit the code to analyze
     * @param snippet the object to store the extracted metadata
     */
    public void visitAllVisitors(CompilationUnit unit, Snippet snippet){
        this.setSnippetClassAnnotations(unit, snippet);
        this.setSnippetClassFields(unit, snippet);
        this.setSnippetClassFieldAnnotations(unit, snippet);
        this.setSnippetClassName(unit, snippet);
        this.setSnippetMethodAnnotations(unit, snippet);
        this.setSnippetMethodReturnType(unit, snippet);
    }

    private void setSnippetClassAnnotations(CompilationUnit unit, Snippet snippet){
        final HashSet<String> classAnnotations = new HashSet<>();
        this.classAnnotationVisitor.visit(unit, classAnnotations);
        snippet.setClassAnnotations(classAnnotations);
    }

    private void setSnippetClassFields(CompilationUnit unit, Snippet snippet){
        final HashSet<String> classFields = new HashSet<>();
        this.classFieldVisitor.visit(unit, classFields);
        snippet.setClassFields(classFields);
    }

    private void setSnippetClassFieldAnnotations(CompilationUnit unit, Snippet snippet){
        final HashSet<String> classFieldAnnotations = new HashSet<>();
        this.classFieldAnnotationVisitor.visit(unit, classFieldAnnotations);
        snippet.setClassFieldAnnotations(classFieldAnnotations);
    }

    private void setSnippetClassName(CompilationUnit unit, Snippet snippet){
        final HashSet<String> classNames = new HashSet<>();
        this.classNameVisitor.visit(unit, classNames);
        snippet.setClassNames(classNames);
    }

    private void setSnippetMethodAnnotations(CompilationUnit unit, Snippet snippet){
        final HashSet<String> methodAnnotations = new HashSet<>();
        this.methodAnnotationVisitor.visit(unit, methodAnnotations);
        snippet.setMethodAnnotations(methodAnnotations);
    }

    private void setSnippetMethodReturnType(CompilationUnit unit, Snippet snippet){
        final HashSet<String> methodReturnTypes = new HashSet<>();
        this.methodReturnTypeVisitor.visit(unit, methodReturnTypes);
        snippet.setMethodReturnTypes(methodReturnTypes);
    }

}