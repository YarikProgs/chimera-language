package net.aros.chimera.ast.first;

import net.aros.chimera.ast.SourcePos;

import java.util.List;
import java.util.Optional;

public sealed interface Stmt extends Node {
    record ExprStmt(List<Expr.AnnotationExpr> annotations, Expr expr, SourcePos pos) implements Stmt {}
    record IfStmt(List<Expr.AnnotationExpr> annotations, Expr cond, Stmt thenStmt, Optional<Stmt> elseStmt, SourcePos pos) implements Stmt {}
    record BlockStmt(List<Stmt> stmts, SourcePos pos) implements Stmt {}
    record WhileStmt(List<Expr.AnnotationExpr> annotations, Expr cond, BlockStmt thenBlock, SourcePos pos) implements Stmt {}
    record DoWhileStmt(List<Expr.AnnotationExpr> annotations, BlockStmt doBlock, Expr cond, SourcePos pos) implements Stmt {}
    record ForStmt(List<Expr.AnnotationExpr> annotations, List<Expr.VarExpr> variables, Expr iterator, Stmt body, SourcePos pos) implements Stmt {}
    record ReturnStmt(List<Expr.AnnotationExpr> annotations, Optional<Expr> expr, SourcePos pos) implements Stmt {}

    record ErrorStmt(SourcePos pos) implements Stmt {}
}
