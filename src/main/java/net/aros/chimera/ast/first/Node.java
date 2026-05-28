package net.aros.chimera.ast.first;

import net.aros.chimera.ast.SourcePos;

import java.util.List;
import java.util.Optional;

public sealed interface Node permits Type, Expr, Stmt, Program, Node.Annotation, Node.Parameter, Node.Argument {
    record Annotation(String name, Optional<List<Argument>> args, SourcePos pos) implements Node {}
    record Parameter(List<Annotation> annotations, String name, Optional<Type> type, Optional<Expr> defaultValue, SourcePos pos) implements Node {}
    record Argument(Optional<String> name, Expr value, SourcePos pos) implements Node {}

    SourcePos pos();
}
