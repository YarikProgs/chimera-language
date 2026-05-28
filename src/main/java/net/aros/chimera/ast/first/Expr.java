package net.aros.chimera.ast.first;

import net.aros.chimera.ast.SourcePos;
import net.aros.chimera.ast.Modifier;
import net.aros.chimera.ast.ops.BinaryOp;
import net.aros.chimera.ast.ops.UnaryOp;
import net.aros.chimera.ast.ops.NullAccessMode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public sealed interface Expr extends Node {
    record LambdaExpr(List<ParameterExpr> parameters, Optional<TypeExpr> returnType, Stmt.BlockStmt body, SourcePos pos) implements Expr {}
    record AssignExpr(List<Modifier> modifiers, Expr target, Optional<TypeExpr> type, Expr initializer, SourcePos pos) implements Expr {}
    record BinaryExpr(Expr left, BinaryOp op, Expr right, SourcePos pos) implements Expr {}
    record UnaryExpr(UnaryOp op, Expr expr, SourcePos pos) implements Expr {}
    record CallExpr(Expr callee, List<Expr> args, SourcePos pos) implements Expr {}
    record MemberAccessExpr(Expr object, NullAccessMode nullAccessMode, String member, SourcePos pos) implements Expr {}
    record VarExpr(String name, SourcePos pos) implements Expr {}
    record ParameterExpr(List<AnnotationExpr> annotations, String name, Optional<TypeExpr> type, Optional<Expr> defaultValue, SourcePos pos) implements Expr {}
    record ArgumentExpr(Optional<String> name, Expr value, SourcePos pos) implements Expr {}
    record TernaryExpr(Expr cond, Expr thenExpr, Expr elseExpr, SourcePos pos) implements Expr {}
    record NullCoalesceExpr(Expr first, Expr second, SourcePos pos) implements Expr {}
    record ShortTryExpr(Expr expr, SourcePos pos) implements Expr {}
    record UnwrapExpr(Expr expr, NullAccessMode nullAccessMode, SourcePos pos) implements Expr {}
    record AnnotationExpr(String name, Optional<List<Expr>> args, SourcePos pos) implements Expr {}

    sealed interface LiteralExpr extends Expr {
        record Int(BigInteger value, SourcePos pos) implements LiteralExpr {}
        record Float(BigDecimal value, SourcePos pos) implements LiteralExpr {}
        record Bool(boolean value, SourcePos pos) implements LiteralExpr {}
        record String(java.lang.String value, SourcePos pos) implements LiteralExpr {}
        record List(java.util.List<Expr> value, SourcePos pos) implements LiteralExpr {}
        record Map(java.util.Map<Expr, Expr> value, SourcePos pos) implements LiteralExpr {}
        record Null(SourcePos pos) implements LiteralExpr {}
    }

    sealed interface TypeExpr extends Expr {
        record IdentifierType(String name, SourcePos pos) implements TypeExpr {}
        record UnionTypeExpr(List<TypeExpr> types, SourcePos pos) implements TypeExpr {}
        record IntersectionTypeExpr(List<TypeExpr> types, SourcePos pos) implements TypeExpr {}
        record NullableTypeExpr(TypeExpr type, SourcePos pos) implements TypeExpr {}
        record TupleTypeExpr(List<TypeExpr> types, SourcePos pos) implements TypeExpr {}
        record ListTypeExpr(TypeExpr type, SourcePos pos) implements TypeExpr {}
        record MapTypeExpr(TypeExpr keyType, TypeExpr valueType, SourcePos pos) implements TypeExpr {}
        record FunctionType(List<TypeExpr> params, TypeExpr returnType, SourcePos pos) implements TypeExpr {}
    }

    record ErrorExpr(SourcePos pos) implements Expr {}
}
