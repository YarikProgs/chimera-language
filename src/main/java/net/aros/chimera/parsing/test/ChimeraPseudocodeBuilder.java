package net.aros.chimera.parsing.test;

import net.aros.chimera.ast.Modifier;
import net.aros.chimera.ast.first.ChimeraVisitor;
import net.aros.chimera.ast.first.Expr;
import net.aros.chimera.ast.first.Program;
import net.aros.chimera.ast.first.Stmt;
import net.aros.chimera.ast.ops.NullAccessMode;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ChimeraPseudocodeBuilder implements ChimeraVisitor<String> {
    @Override
    public String visitProgram(Program program) {
        return build(b -> {
            for (Stmt stmt : program.stmts()) {
                b.append(visit(stmt)).append("\n");
            }
        });
    }

    @Override
    public String visitExprStmt(Stmt.ExprStmt stmt) {
        return stmt.annotations().stream().map(a -> visit(a) + "\n").collect(Collectors.joining()) + visit(stmt.expr()) + ";";
    }

    @Override
    public String visitIfStmt(Stmt.IfStmt stmt) {
        return build(b -> {
            b.append(stmt.annotations().stream().map(a -> visit(a) + "\n").collect(Collectors.joining()));
            b.append("if ").append(visit(stmt.cond())).append(" ").append(visit(stmt.thenStmt()));
            stmt.elseStmt().ifPresent(elseStmt -> b.append("\nelse ").append(visit(elseStmt)));
        });
    }

    @Override
    public String visitBlockStmt(Stmt.BlockStmt stmt) {
        return build(b -> {
            b.append("{\n");
            for (Stmt subStmt : stmt.stmts()) {
                b.append("    ").append(visit(subStmt)).append("\n");
            }
            b.append("}");
        });
    }

    @Override
    public String visitWhileStmt(Stmt.WhileStmt stmt) {
        return build(b -> {
            b.append(stmt.annotations().stream().map(a -> visit(a) + "\n").collect(Collectors.joining()));
            b.append("while ").append(visit(stmt.cond())).append(" ").append(visit(stmt.thenBlock()));
        });
    }

    @Override
    public String visitDoWhileStmt(Stmt.DoWhileStmt stmt) {
        return build(b -> {
            b.append(stmt.annotations().stream().map(a -> visit(a) + "\n").collect(Collectors.joining()));
            b.append("do ").append(visit(stmt.doBlock())).append(" ").append(visit(stmt.cond())).append(";");
        });
    }

    @Override
    public String visitForStmt(Stmt.ForStmt stmt) {
        return build(b -> {
            b.append(stmt.annotations().stream().map(a -> visit(a) + "\n").collect(Collectors.joining()));
            b.append("for ").append(String.join(", ", stmt.variables().stream().map(Expr.VarExpr::name).toList())).append(" in ")
                    .append(visit(stmt.iterator())).append(" ").append(visit(stmt.body()));
        });
    }

    @Override
    public String visitReturnStmt(Stmt.ReturnStmt stmt) {
        return stmt.annotations().stream().map(a -> visit(a) + "\n").collect(Collectors.joining())
                + "return" + stmt.expr().map(this::visit).map(s -> " " + s).orElse("") + ";";
    }

    @Override
    public String visitLambdaExpr(Expr.LambdaExpr expr) {
        return build(b -> {
            b.append("fn(")
                    .append(expr.parameters().stream().map(this::visit).collect(Collectors.joining(", ")))
                    .append(") ").append(visit(expr.body()));
        });
    }

    @Override
    public String visitAssignExpr(Expr.AssignExpr expr) {
        return build(b -> {
            b.append(expr.modifiers().stream().map(Modifier::name).collect(Collectors.joining(" ")));
            if (!expr.modifiers().isEmpty()) b.append(" ");
            b.append(visit(expr.target()));
            expr.type().ifPresent(type -> b.append(": ").append(visit(type)));
            b.append(" = ").append(visit(expr.initializer()));
        });
    }

    @Override
    public String visitTernaryExpr(Expr.TernaryExpr expr) {
        return build(b -> {
            b.append("(")
                    .append(visit(expr.cond())).append(" ? ").append(visit(expr.thenExpr())).append(" : ").append(visit(expr.elseExpr()))
                    .append(")");
        });
    }

    @Override
    public String visitNullCoalesceExpr(Expr.NullCoalesceExpr expr) {
        return "(" + visit(expr.first()) + " ?? " + visit(expr.second()) + ")";
    }

    @Override
    public String visitShortTryExpr(Expr.ShortTryExpr expr) {
        return "try " + visit(expr.expr());
    }

    @Override
    public String visitUnwrapExpr(Expr.UnwrapExpr expr) {
        return "(" + visit(expr.expr()) + ")" + (expr.nullAccessMode() == NullAccessMode.PROPAGATE_NULL ? "!" : "!!");
    }

    @Override
    public String visitLiteralExpr(Expr.LiteralExpr expr) {
        return switch (expr) {
            case Expr.LiteralExpr.Int i -> i.value().toString();
            case Expr.LiteralExpr.Float f -> f.value().toString();
            case Expr.LiteralExpr.Bool b -> String.valueOf(b.value());
            case Expr.LiteralExpr.Null n -> "null";
            case Expr.LiteralExpr.String s -> "\"" + s.value() + "\"";
            case Expr.LiteralExpr.List l ->
                    "[" + l.value().stream().map(this::visit).collect(Collectors.joining(", ")) + "]";
            case Expr.LiteralExpr.Map m ->
                    "{" + m.value().entrySet().stream().map(e -> visit(e.getKey()) + ": " + visit(e.getValue())).collect(Collectors.joining(", ")) + "}";
        };
    }

    @Override
    public String visitAnnotationExpr(Expr.AnnotationExpr expr) {
        return build(b -> {
            b.append("[");
            b.append(expr.name());
            expr.args().ifPresent(args -> b.append("(").append(String.join(", ", args.stream().map(this::visit).toList())).append(")"));
            b.append("]");
        });
    }

    @Override
    public String visitBinaryExpr(Expr.BinaryExpr expr) {
        return visit(expr.left()) + " " + expr.op() + " " + visit(expr.right());
    }

    @Override
    public String visitUnaryExpr(Expr.UnaryExpr expr) {
        return expr.op() + " " + visit(expr.expr());
    }

    @Override
    public String visitCallExpr(Expr.CallExpr expr) {
        return visit(expr.callee()) + "(" + expr.args().stream().map(this::visit).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public String visitMemberAccessExpr(Expr.MemberAccessExpr expr) {
        return visit(expr.object()) + (expr.nullAccessMode() == NullAccessMode.PROPAGATE_NULL ? "?" : "") + "." + expr.member();
    }

    @Override
    public String visitVarExpr(Expr.VarExpr expr) {
        return expr.name();
    }

    @Override
    public String visitParameterExpr(Expr.ParameterExpr expr) {
        return build(b -> {
            b.append(expr.annotations().stream().map(a -> visit(a) + " ").collect(Collectors.joining()));
            b.append(expr.name());
            expr.type().ifPresent(type -> b.append(": ").append(visit(type)));
            expr.defaultValue().ifPresent(value -> b.append(" = ").append(visit(value)));
        });
    }

    @Override
    public String visitArgumentExpr(Expr.ArgumentExpr expr) {
        return build(b -> {
            expr.name().ifPresent(name -> b.append(name).append(" = "));
            b.append(visit(expr.value()));
        });
    }

    @Override
    public String visitIdentifierType(Expr.TypeExpr.IdentifierType expr) {
        return expr.name();
    }

    @Override
    public String visitUnionTypeExpr(Expr.TypeExpr.UnionTypeExpr expr) {
        return expr.types().stream().map(this::visit).collect(Collectors.joining(" | "));
    }

    @Override
    public String visitIntersectionTypeExpr(Expr.TypeExpr.IntersectionTypeExpr expr) {
        return expr.types().stream().map(this::visit).collect(Collectors.joining(" & "));
    }

    @Override
    public String visitNullableTypeExpr(Expr.TypeExpr.NullableTypeExpr expr) {
        return visit(expr.type()) + "?";
    }

    @Override
    public String visitTupleTypeExpr(Expr.TypeExpr.TupleTypeExpr expr) {
        return "tuple<" + expr.types().stream().map(this::visit).collect(Collectors.joining(", ")) + ">";
    }

    @Override
    public String visitListTypeExpr(Expr.TypeExpr.ListTypeExpr expr) {
        return "list<" + visit(expr.type()) + ">";
    }

    @Override
    public String visitMapTypeExpr(Expr.TypeExpr.MapTypeExpr expr) {
        return "map<" + visit(expr.keyType()) + ", " + visit(expr.valueType()) + ">";
    }

    @Override
    public String visitFunctionType(Expr.TypeExpr.FunctionType expr) {
        return "(" + expr.params().stream().map(this::visit).collect(Collectors.joining(", ")) + ") -> " + visit(expr.returnType());
    }

    private static String build(Consumer<StringBuilder> consumer) {
        StringBuilder builder = new StringBuilder();
        consumer.accept(builder);
        return builder.toString();
    }

    @Override
    public String visitErrorExpr(Expr.ErrorExpr expr) {
        return "<EXPRERR>";
    }

    @Override
    public String visitErrorStmt(Stmt.ErrorStmt stmt) {
        return "<STMTERR>";
    }
}