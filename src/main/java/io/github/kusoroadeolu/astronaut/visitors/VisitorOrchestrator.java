package io.github.kusoroadeolu.astronaut.visitors;

import com.github.javaparser.ast.CompilationUnit;
import io.github.kusoroadeolu.astronaut.entities.SnippetIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public final class VisitorOrchestrator {

    private final ClassNameVisitor classNameVisitor;
    private final MethodNameVisitor methodNameVisitor;

    /**
     * Visits the compilation unit and extracts all code metadata into the snippet.
     *
     * This method runs all visitors to gather information about the code, including class names and method names
     * The extracted data is then stored in the provided snippet object.
     *
     * @param unit the code to analyze
     * @param snippet the object to store the extracted metadata
     */
    public void visitAllVisitors(CompilationUnit unit, SnippetIndex snippet){
        setSnippetClassName(unit, snippet);
        setSnippetMethodName(unit, snippet);
    }


    private void setSnippetClassName(CompilationUnit unit, SnippetIndex snippet){
        final HashSet<String> classNames = new HashSet<>();
        this.classNameVisitor.visit(unit, classNames);
        snippet.setClassNames(classNames);
    }


    private void setSnippetMethodName(CompilationUnit unit, SnippetIndex snippet){
        final HashSet<String> methodNames = new HashSet<>();
        this.methodNameVisitor.visit(unit, methodNames);
        snippet.setMethodNames(methodNames);
    }

}