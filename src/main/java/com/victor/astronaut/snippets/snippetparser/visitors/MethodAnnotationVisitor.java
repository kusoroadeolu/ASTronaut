package com.victor.astronaut.snippets.snippetparser.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public final class MethodAnnotationVisitor extends VoidVisitorAdapter<Set<String>> {

    @Override
    public void visit(MethodDeclaration n, Set<String> arg) {
        super.visit(n, arg);
        Set<String> annotations = n.getAnnotations().stream().map(a -> a.getName().asString()).collect(Collectors.toSet());
        arg.addAll(annotations);
    }
}
