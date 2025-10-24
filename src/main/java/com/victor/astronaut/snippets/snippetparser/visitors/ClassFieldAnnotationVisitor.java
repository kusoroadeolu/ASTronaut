package com.victor.astronaut.snippets.snippetparser.visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public final class ClassFieldAnnotationVisitor extends VoidVisitorAdapter<Set<String>> {

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Set<String> arg) {
        super.visit(n, arg);
        final Set<String> fieldAnnotations = n
                .getFields()
                .stream()
                .flatMap(a -> a.getAnnotations().stream())
                .map(v -> v.getName().asString().toLowerCase())
                .collect(Collectors.toSet());

        arg.addAll(fieldAnnotations);
    }
}
