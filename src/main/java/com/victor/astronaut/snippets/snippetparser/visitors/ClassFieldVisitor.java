package com.victor.astronaut.snippets.snippetparser.visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public final class ClassFieldVisitor extends VoidVisitorAdapter<Set<String>> {

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Set<String> arg) {
        super.visit(n, arg);
        Set<String> fieldNames = n.getFields().stream().flatMap(a -> a.getVariables().stream()).map(v -> v.getName().asString()).collect(Collectors.toSet());
        arg.addAll(fieldNames);
    }
}
