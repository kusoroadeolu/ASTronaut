package com.victor.astronaut.snippets.snippetparser.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public final class MethodReturnTypeVisitor extends VoidVisitorAdapter<Set<String>> {

    @Override
    public void visit(MethodDeclaration n, Set<String> arg) {
        super.visit(n, arg);
        arg.add(n.getTypeAsString().toLowerCase());
    }
}
