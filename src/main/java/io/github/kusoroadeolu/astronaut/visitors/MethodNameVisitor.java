package io.github.kusoroadeolu.astronaut.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public final class MethodNameVisitor extends VoidVisitorAdapter<Set<String>> {

    @Override
    public void visit(MethodDeclaration n, Set<String> arg) {
        super.visit(n, arg);
        arg.add(n.getName().asString().toLowerCase());
    }
}
