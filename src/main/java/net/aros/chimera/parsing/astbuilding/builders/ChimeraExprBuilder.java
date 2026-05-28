package net.aros.chimera.parsing.astbuilding.builders;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.ChimeraAntlrParserBaseVisitor;
import net.aros.chimera.ast.Modifier;
import net.aros.chimera.ast.first.Expr;
import net.aros.chimera.ast.first.Stmt;
import net.aros.chimera.ast.ops.NullAccessMode;
import net.aros.chimera.parsing.astbuilding.ChimeraAstBuilder;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.ibm.icu.impl.Utility.unescape;
import static net.aros.chimera.ast.ops.BinaryOp.*;
import static net.aros.chimera.ast.ops.NullAccessMode.PROPAGATE_NULL;
import static net.aros.chimera.ast.ops.NullAccessMode.REQUIRE_NONNULL;
import static net.aros.chimera.parsing.astbuilding.AstUtils.*;

public class ChimeraExprBuilder extends ChimeraAntlrParserBaseVisitor<Expr> {
    private final ChimeraAstBuilder parent;

    public ChimeraExprBuilder(ChimeraAstBuilder parent) {
        this.parent = parent;
    }

    @Override
    public Expr visitArgumentsPostfix(ChimeraAntlrParser.@NotNull ArgumentsPostfixContext ctx) {
        return new Expr.CallExpr(visit(ctx.expr()), parent.getElementBuilder().buildArguments(ctx.arguments()), pos(ctx));
    }

    @Override
    public Expr visitMemberAccessPostfix(ChimeraAntlrParser.@NotNull MemberAccessPostfixContext ctx) {
        NullAccessMode mode = ctx.QuestionMark() == null ? REQUIRE_NONNULL : PROPAGATE_NULL;
        return new Expr.MemberAccessExpr(visit(ctx.expr()), mode, id(ctx.Identifier()), pos(ctx));
    }

    @Override
    public Expr visitStrictUnwrapPostfix(ChimeraAntlrParser.@NotNull StrictUnwrapPostfixContext ctx) {
        return new Expr.UnwrapExpr(visit(ctx.expr()), REQUIRE_NONNULL, pos(ctx));
    }

    @Override
    public Expr visitUnwrapPostfix(ChimeraAntlrParser.@NotNull UnwrapPostfixContext ctx) {
        return new Expr.UnwrapExpr(visit(ctx.expr()), PROPAGATE_NULL, pos(ctx));
    }

    @Override
    public Expr visitModifiedExpr(ChimeraAntlrParser.@NotNull ModifiedExprContext ctx) {
        List<Modifier> modifiers = new ArrayList<>();
        for (ChimeraAntlrParser.ModifierContext mod : ctx.modifier()) modifiers.add(modifier(mod));
        return new Expr.ModifiedExpr(modifiers, visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Expr visitShortTryExpr(ChimeraAntlrParser.@NotNull ShortTryExprContext ctx) {
        return new Expr.ShortTryExpr(visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Expr visitUnaryExpr(ChimeraAntlrParser.@NotNull UnaryExprContext ctx) {
        return new Expr.UnaryExpr(getUnaryOperator(ctx.op), visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Expr visitFactorExpr(ChimeraAntlrParser.@NotNull FactorExprContext ctx) {
        return new Expr.BinaryExpr(visit(ctx.left), getBinaryOperator(ctx.op), visit(ctx.right), pos(ctx));
    }

    @Override
    public Expr visitTermExpr(ChimeraAntlrParser.@NotNull TermExprContext ctx) {
        return new Expr.BinaryExpr(visit(ctx.left), getBinaryOperator(ctx.op), visit(ctx.right), pos(ctx));
    }

    @Override
    public Expr visitShiftExpr(ChimeraAntlrParser.@NotNull ShiftExprContext ctx) {
        return new Expr.BinaryExpr(visit(ctx.left), getBinaryOperator(ctx.op), visit(ctx.right), pos(ctx));
    }

    @Override
    public Expr visitComparisonExpr(ChimeraAntlrParser.@NotNull ComparisonExprContext ctx) {
        return new Expr.BinaryExpr(visit(ctx.left), getBinaryOperator(ctx.op), visit(ctx.right), pos(ctx));
    }

    @Override
    public Expr visitEqualityExpr(ChimeraAntlrParser.@NotNull EqualityExprContext ctx) {
        return new Expr.BinaryExpr(visit(ctx.left), getBinaryOperator(ctx.op), visit(ctx.right), pos(ctx));
    }

    @Override
    public Expr visitBitwiseAndExpr(ChimeraAntlrParser.@NotNull BitwiseAndExprContext ctx) {
        return new Expr.BinaryExpr(visit(ctx.left), BITWISE_AND, visit(ctx.right), pos(ctx));
    }

    @Override
    public Expr visitBitwiseXorExpr(ChimeraAntlrParser.@NotNull BitwiseXorExprContext ctx) {
        return new Expr.BinaryExpr(visit(ctx.left), BITWISE_XOR, visit(ctx.right), pos(ctx));
    }

    @Override
    public Expr visitBitwiseOrExpr(ChimeraAntlrParser.@NotNull BitwiseOrExprContext ctx) {
        return new Expr.BinaryExpr(visit(ctx.left), BITWISE_OR, visit(ctx.right), pos(ctx));
    }

    @Override
    public Expr visitLogicalAndExpr(ChimeraAntlrParser.@NotNull LogicalAndExprContext ctx) {
        return new Expr.BinaryExpr(visit(ctx.left), LOGICAL_AND, visit(ctx.right), pos(ctx));
    }

    @Override
    public Expr visitLogicalXorExpr(ChimeraAntlrParser.@NotNull LogicalXorExprContext ctx) {
        return new Expr.BinaryExpr(visit(ctx.left), LOGICAL_XOR, visit(ctx.right), pos(ctx));
    }

    @Override
    public Expr visitLogicalOrExpr(ChimeraAntlrParser.@NotNull LogicalOrExprContext ctx) {
        return new Expr.BinaryExpr(visit(ctx.left), LOGICAL_OR, visit(ctx.right), pos(ctx));
    }

    @Override
    public Expr visitTernaryExpr(ChimeraAntlrParser.@NotNull TernaryExprContext ctx) {
        return new Expr.TernaryExpr(visit(ctx.cond), visit(ctx.thenExpr), visit(ctx.elseExpr), pos(ctx));
    }

    @Override
    public Expr visitNullCoalesceExpr(ChimeraAntlrParser.@NotNull NullCoalesceExprContext ctx) {
        return new Expr.NullCoalesceExpr(visit(ctx.left), visit(ctx.right), pos(ctx));
    }

    @Override
    public Expr visitLambdaExpr(ChimeraAntlrParser.@NotNull LambdaExprContext ctx) {
        Stmt.BlockStmt body = ctx.blockStmt() != null
                ? (Stmt.BlockStmt) parent.getStmtBuilder().visit(ctx.blockStmt())
                : syntheticBlock(visit(ctx.expr()));

        return new Expr.LambdaExpr(
                parent.getElementBuilder().buildParameters(ctx.parameters()),
                Optional.ofNullable(ctx.type()).map(parent.getTypeBuilder()::visit),
                body,
                pos(ctx)
        );
    }

    @Override
    public Expr visitAssignmentExpr(ChimeraAntlrParser.@NotNull AssignmentExprContext ctx) {
        return new Expr.AssignExpr(visit(ctx.lvalue), Optional.ofNullable(ctx.type()).map(parent.getTypeBuilder()::visit), visit(ctx.rvalue), pos(ctx));
    }

    @Override
    public Expr visitListPrimary(ChimeraAntlrParser.@NotNull ListPrimaryContext ctx) {
        List<Expr> expressions = new ArrayList<>();
        for (ChimeraAntlrParser.ExprContext eCtx : ctx.expr()) expressions.add(visit(eCtx));
        return new Expr.LiteralExpr.List(expressions, pos(ctx));
    }

    @Override
    public Expr visitMapPrimary(ChimeraAntlrParser.@NotNull MapPrimaryContext ctx) {
        Map<Expr, Expr> map = new HashMap<>();
        for (ChimeraAntlrParser.MapPairContext pair : ctx.mapPair()) map.put(visit(pair.key), visit(pair.value));
        return new Expr.LiteralExpr.Map(map, pos(ctx));
    }

    @Override
    public Expr visitIntLiteralPrimary(ChimeraAntlrParser.@NotNull IntLiteralPrimaryContext ctx) {
        // todo literal validation
        return new Expr.LiteralExpr.Int(new BigInteger(ctx.IntLiteral().getText()), pos(ctx));
    }

    @Override
    public Expr visitFloatLiteralPrimary(ChimeraAntlrParser.@NotNull FloatLiteralPrimaryContext ctx) {
        // todo literal validation
        return new Expr.LiteralExpr.Float(new BigDecimal(ctx.FloatLiteral().getText()), pos(ctx));
    }

    @Override
    public Expr visitTruePrimary(ChimeraAntlrParser.TruePrimaryContext ctx) {
        return new Expr.LiteralExpr.Bool(true, pos(ctx));
    }

    @Override
    public Expr visitFalsePrimary(ChimeraAntlrParser.FalsePrimaryContext ctx) {
        return new Expr.LiteralExpr.Bool(false, pos(ctx));
    }

    @Override
    public Expr visitNullPrimary(ChimeraAntlrParser.NullPrimaryContext ctx) {
        return new Expr.LiteralExpr.Null(pos(ctx));
    }

    @Override
    public Expr visitStringLiteralPrimary(ChimeraAntlrParser.@NotNull StringLiteralPrimaryContext ctx) {
        String raw = ctx.StringLiteral().getText();
        return new Expr.LiteralExpr.String(unescape(raw.substring(1, raw.length() - 1)), pos(ctx));
    }

    @Override
    public Expr visitIdentifierPrimary(ChimeraAntlrParser.@NotNull IdentifierPrimaryContext ctx) {
        return new Expr.VarExpr(id(ctx.Identifier()), pos(ctx));
    }

    @Override
    public Expr visitExprParenPrimary(ChimeraAntlrParser.ExprParenPrimaryContext ctx) {
        return visit(ctx.expr());
    }
}
