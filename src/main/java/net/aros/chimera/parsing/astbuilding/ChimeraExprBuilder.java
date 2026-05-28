package net.aros.chimera.parsing.astbuilding;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.ChimeraAntlrParserBaseVisitor;
import net.aros.chimera.ast.Modifier;
import net.aros.chimera.ast.SourcePos;
import net.aros.chimera.ast.first.Expr;
import net.aros.chimera.ast.first.Stmt;
import net.aros.chimera.ast.ops.BinaryOp;
import net.aros.chimera.ast.ops.NullAccessMode;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.ibm.icu.impl.Utility.unescape;
import static net.aros.chimera.parsing.astbuilding.AstUtils.*;

public class ChimeraExprBuilder extends ChimeraAntlrParserBaseVisitor<Expr> {
    private final ChimeraAstBuilder parent;

    public ChimeraExprBuilder(ChimeraAstBuilder parent) {
        this.parent = parent;
    }

    @Override
    public Expr visitLambdaExpr(ChimeraAntlrParser.@NotNull LambdaExprContext ctx) {
        List<Expr.ParameterExpr> params = new ArrayList<>();
        for (ChimeraAntlrParser.ParameterContext pCtx : ctx.parameters().parameter())
            params.add((Expr.ParameterExpr) visit(pCtx));
        return new Expr.LambdaExpr(
                params,
                Optional.ofNullable(ctx.type()).map(parent.getTypeBuilder()::visit),
                ctx.blockStmt() != null
                        ? (Stmt.BlockStmt) parent.getStmtBuilder().visit(ctx.blockStmt())
                        : syntheticBlock(visit(ctx.expr())),
                pos(ctx)
        );
    }

    @Override
    public Expr visitAssignmentExpr(ChimeraAntlrParser.@NotNull AssignmentExprContext ctx) {
        List<Modifier> modifiers = new ArrayList<>();
        for (ChimeraAntlrParser.ModifierContext mCtx : ctx.modifier()) modifiers.add(modifier(mCtx));

        Expr target = buildPostfixExpr(visit(ctx.primary()), ctx.postfix());
        Expr initializer = visit(ctx.assignment());
        BinaryOp op = getBinaryOperatorFromAssignment(((TerminalNode) ctx.assignmentOperator().getChild(0)).getSymbol());
        if (op != null) {
            initializer = new Expr.BinaryExpr(target, op, initializer, pos(ctx));
        }

        return new Expr.AssignExpr(
                modifiers,
                visit(ctx.primary()),
                Optional.ofNullable(ctx.type()).map(parent.getTypeBuilder()::visit),
                initializer,
                pos(ctx)
        );
    }

    @Override
    public Expr visitNullCoalesceExpr(ChimeraAntlrParser.@NotNull NullCoalesceExprContext ctx) {
        Expr expr = visit(ctx.ternary(0));
        for (int i = 1; i < ctx.ternary().size(); i++) {
            expr = new Expr.NullCoalesceExpr(expr, visit(ctx.ternary(i)), pos(ctx));
        }
        return expr;
    }

//    @Override
//    public Expr visitNullCoalesceMissingRhs(ChimeraAntlrParser.NullCoalesceMissingRhsContext ctx) {
//        parent.getReporter().onNullCoalesceMissingRhs(ctx);
//        return new Expr.NullCoalesceExpr(visit(ctx.ternary()), new Expr.ErrorExpr(pos(ctx.getStop())), pos(ctx));
//    }

    @Override
    public Expr visitTernaryExpr(ChimeraAntlrParser.TernaryExprContext ctx) {
        Expr condition = visit(ctx.logicalOr());
        if (ctx.QuestionMark() == null) return condition;

        Expr thenBranch = visit(ctx.expr());
        Expr elseBranch = visit(ctx.ternary());

        return new Expr.TernaryExpr(condition, thenBranch, elseBranch, pos(ctx));
    }

    @Override
    public Expr visitTernaryMissingThen(ChimeraAntlrParser.TernaryMissingThenContext ctx) {
        parent.getReporter().onTernaryMissingThen(ctx);
        return new Expr.TernaryExpr(visit(ctx.logicalOr()), new Expr.ErrorExpr(pos(ctx.QuestionMark().getSymbol())), visit(ctx.ternary()), pos(ctx));
    }

    @Override
    public Expr visitTernaryMissingElse(ChimeraAntlrParser.TernaryMissingElseContext ctx) {
        parent.getReporter().onTernaryMissingElse(ctx);
        SourcePos errorPos = ctx.Colon() == null ? pos(ctx.expr().getStop()) : pos(ctx.Colon().getSymbol());
        return new Expr.TernaryExpr(visit(ctx.logicalOr()), visit(ctx.expr()), new Expr.ErrorExpr(errorPos), pos(ctx));
    }

    @Override
    public Expr visitTernaryMissingThenAndElse(ChimeraAntlrParser.TernaryMissingThenAndElseContext ctx) {
        parent.getReporter().onTernaryMissingThenAndElse(ctx);
        SourcePos thenErrorPos = pos(ctx.QuestionMark().getSymbol());
        SourcePos elseErrorPos = ctx.Colon() == null ? thenErrorPos : pos(ctx.Colon().getSymbol());
        return new Expr.TernaryExpr(visit(ctx.logicalOr()), new Expr.ErrorExpr(thenErrorPos), new Expr.ErrorExpr(elseErrorPos), pos(ctx));
    }

    @Override
    public Expr visitLogicalOrExpr(ChimeraAntlrParser.LogicalOrExprContext ctx) {
        Expr expr = visit(ctx.logicalXor(0));
        for (int i = 1; i < ctx.logicalXor().size(); i++) {
            expr = new Expr.BinaryExpr(expr, BinaryOp.LOGICAL_OR, visit(ctx.logicalXor(i)), pos(ctx));
        }
        return expr;
    }

//    @Override
//    public Expr visitLogicalOrMissingRhs(ChimeraAntlrParser.LogicalOrMissingRhsContext ctx) {
//        parent.getReporter().onBinaryMissingRhs(ctx, ctx.op.getText());
//        return new Expr.BinaryExpr(visit(ctx.logicalXor()), BinaryOp.LOGICAL_OR, new Expr.ErrorExpr(pos(ctx.getStop())), pos(ctx));
//    }

    @Override
    public Expr visitLogicalXorExpr(ChimeraAntlrParser.LogicalXorExprContext ctx) {
        Expr expr = visit(ctx.logicalAnd(0));
        for (int i = 1; i < ctx.logicalAnd().size(); i++) {
            expr = new Expr.BinaryExpr(expr, BinaryOp.LOGICAL_XOR, visit(ctx.logicalAnd(i)), pos(ctx));
        }
        return expr;
    }

//    @Override
//    public Expr visitLogicalXorMissingRhs(ChimeraAntlrParser.LogicalXorMissingRhsContext ctx) {
//        parent.getReporter().onBinaryMissingRhs(ctx, ctx.op.getText());
//        return new Expr.BinaryExpr(visit(ctx.logicalAnd()), BinaryOp.LOGICAL_XOR, new Expr.ErrorExpr(pos(ctx.getStop())), pos(ctx));
//    }

    @Override
    public Expr visitLogicalAndExpr(ChimeraAntlrParser.LogicalAndExprContext ctx) {
        Expr expr = visit(ctx.bitwiseOr(0));
        for (int i = 1; i < ctx.bitwiseOr().size(); i++) {
            expr = new Expr.BinaryExpr(expr, BinaryOp.LOGICAL_AND, visit(ctx.bitwiseOr(i)), pos(ctx));
        }
        return expr;
    }

//    @Override
//    public Expr visitLogicalAndMissingRhs(ChimeraAntlrParser.LogicalAndMissingRhsContext ctx) {
//        parent.getReporter().onBinaryMissingRhs(ctx, ctx.op.getText());
//        return new Expr.BinaryExpr(visit(ctx.bitwiseOr()), BinaryOp.LOGICAL_AND, new Expr.ErrorExpr(pos(ctx.getStop())), pos(ctx));
//    }

    @Override
    public Expr visitBitwiseOrExpr(ChimeraAntlrParser.BitwiseOrExprContext ctx) {
        Expr expr = visit(ctx.bitwiseXor(0));
        for (int i = 1; i < ctx.bitwiseXor().size(); i++) {
            expr = new Expr.BinaryExpr(expr, BinaryOp.BITWISE_OR, visit(ctx.bitwiseXor(i)), pos(ctx));
        }
        return expr;
    }

//    @Override
//    public Expr visitBitwiseOrMissingRhs(ChimeraAntlrParser.BitwiseOrMissingRhsContext ctx) {
//        parent.getReporter().onBinaryMissingRhs(ctx, ctx.op.getText());
//        return new Expr.BinaryExpr(visit(ctx.bitwiseXor()), BinaryOp.BITWISE_OR, new Expr.ErrorExpr(pos(ctx.getStop())), pos(ctx));
//    }

    @Override
    public Expr visitBitwiseXorExpr(ChimeraAntlrParser.BitwiseXorExprContext ctx) {
        Expr expr = visit(ctx.bitwiseAnd(0));
        for (int i = 1; i < ctx.bitwiseAnd().size(); i++) {
            expr = new Expr.BinaryExpr(expr, BinaryOp.BITWISE_XOR, visit(ctx.bitwiseAnd(i)), pos(ctx));
        }
        return expr;
    }

//    @Override
//    public Expr visitBitwiseXorMissingRhs(ChimeraAntlrParser.BitwiseXorMissingRhsContext ctx) {
//        parent.getReporter().onBinaryMissingRhs(ctx, ctx.op.getText());
//        return new Expr.BinaryExpr(visit(ctx.bitwiseAnd()), BinaryOp.BITWISE_XOR, new Expr.ErrorExpr(pos(ctx.getStop())), pos(ctx));
//    }

    @Override
    public Expr visitBitwiseAndExpr(ChimeraAntlrParser.BitwiseAndExprContext ctx) {
        Expr expr = visit(ctx.equality(0));
        for (int i = 1; i < ctx.equality().size(); i++) {
            expr = new Expr.BinaryExpr(expr, BinaryOp.BITWISE_AND, visit(ctx.equality(i)), pos(ctx));
        }
        return expr;
    }

//    @Override
//    public Expr visitBitwiseAndMissingRhs(ChimeraAntlrParser.BitwiseAndMissingRhsContext ctx) {
//        parent.getReporter().onBinaryMissingRhs(ctx, ctx.op.getText());
//        return new Expr.BinaryExpr(visit(ctx.equality()), BinaryOp.BITWISE_AND, new Expr.ErrorExpr(pos(ctx.getStop())), pos(ctx));
//    }

    @Override
    public Expr visitEqualityExpr(ChimeraAntlrParser.EqualityExprContext ctx) {
        Expr expr = visit(ctx.comparison(0));
        int j = 0;
        for (int i = 1; i < ctx.comparison().size(); i++) {
            expr = new Expr.BinaryExpr(expr, getBinaryOperator(ctx.op.get(j++)), visit(ctx.comparison(i)), pos(ctx));
        }
        return expr;
    }

//    @Override
//    public Expr visitEqualityMissingRhs(ChimeraAntlrParser.EqualityMissingRhsContext ctx) {
//        parent.getReporter().onBinaryMissingRhs(ctx, ctx.op.getText());
//        return new Expr.BinaryExpr(visit(ctx.comparison()), getBinaryOperator(ctx.op), new Expr.ErrorExpr(pos(ctx.getStop())), pos(ctx));
//    }

    @Override
    public Expr visitComparisonExpr(ChimeraAntlrParser.ComparisonExprContext ctx) {
        Expr expr = visit(ctx.shift(0));
        int j = 0;
        for (int i = 1; i < ctx.shift().size(); i++) {
            expr = new Expr.BinaryExpr(expr, getBinaryOperator(ctx.op.get(j++)), visit(ctx.shift(i)), pos(ctx));
        }
        return expr;
    }

//    @Override
//    public Expr visitComparisonMissingRhs(ChimeraAntlrParser.ComparisonMissingRhsContext ctx) {
//        parent.getReporter().onBinaryMissingRhs(ctx, ctx.op.getText());
//        return new Expr.BinaryExpr(visit(ctx.shift()), getBinaryOperator(ctx.op), new Expr.ErrorExpr(pos(ctx.getStop())), pos(ctx));
//    }

    @Override
    public Expr visitShiftExpr(ChimeraAntlrParser.ShiftExprContext ctx) {
        Expr expr = visit(ctx.term(0));
        int j = 0;
        for (int i = 1; i < ctx.term().size(); i++) {
            expr = new Expr.BinaryExpr(expr, getBinaryOperator(ctx.op.get(j++)), visit(ctx.term(i)), pos(ctx));
        }
        return expr;
    }

//    @Override
//    public Expr visitShiftMissingRhs(ChimeraAntlrParser.ShiftMissingRhsContext ctx) {
//        parent.getReporter().onBinaryMissingRhs(ctx, ctx.op.getText());
//        return new Expr.BinaryExpr(visit(ctx.term()), getBinaryOperator(ctx.op), new Expr.ErrorExpr(pos(ctx.getStop())), pos(ctx));
//    }

    @Override
    public Expr visitTermExpr(ChimeraAntlrParser.TermExprContext ctx) {
        Expr expr = visit(ctx.factor(0));
        int j = 0;
        for (int i = 1; i < ctx.factor().size(); i++) {
            expr = new Expr.BinaryExpr(expr, getBinaryOperator(ctx.op.get(j++)), visit(ctx.factor(i)), pos(ctx));
        }
        return expr;
    }

//    @Override
//    public Expr visitTermMissingRhs(ChimeraAntlrParser.TermMissingRhsContext ctx) {
//        parent.getReporter().onBinaryMissingRhs(ctx, ctx.op.getText());
//        return new Expr.BinaryExpr(visit(ctx.factor()), getBinaryOperator(ctx.op), new Expr.ErrorExpr(pos(ctx)), pos(ctx));
//    }

    @Override
    public Expr visitFactorExpr(ChimeraAntlrParser.FactorExprContext ctx) {
        Expr expr = visit(ctx.unary(0));
        int j = 0;
        for (int i = 1; i < ctx.unary().size(); i++) {
            expr = new Expr.BinaryExpr(expr, getBinaryOperator(ctx.op.get(j++)), visit(ctx.unary(i)), pos(ctx));
        }
        return expr;
    }

//    @Override
//    public Expr visitFactorMissingRhs(ChimeraAntlrParser.FactorMissingRhsContext ctx) {
//        parent.getReporter().onBinaryMissingRhs(ctx, ctx.op.getText());
//        return new Expr.BinaryExpr(visit(ctx.unary()), getBinaryOperator(ctx.op), new Expr.ErrorExpr(pos(ctx.getStop())), pos(ctx));
//    }

    @Override
    public Expr visitUnaryUnaryExpr(ChimeraAntlrParser.UnaryUnaryExprContext ctx) {
        return new Expr.UnaryExpr(getUnaryOperator(ctx.op), visit(ctx.unary()), pos(ctx));
    }

    @Override
    public Expr visitShortTryUnaryExpr(ChimeraAntlrParser.ShortTryUnaryExprContext ctx) {
        return new Expr.ShortTryExpr(visit(ctx.unary()), pos(ctx));
    }

    @Override
    public Expr visitCallUnaryExpr(ChimeraAntlrParser.CallUnaryExprContext ctx) {
        return buildPostfixExpr(visit(ctx.primary()), ctx.postfix());
    }

    @Override
    public Expr visitNamedArgument(ChimeraAntlrParser.NamedArgumentContext ctx) {
        return new Expr.ArgumentExpr(Optional.of(id(ctx.Identifier())), visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Expr visitPositionalArgument(ChimeraAntlrParser.PositionalArgumentContext ctx) {
        return new Expr.ArgumentExpr(Optional.empty(), visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Expr visitParameter(ChimeraAntlrParser.ParameterContext ctx) {
        List<Expr.AnnotationExpr> annotations = new ArrayList<>();
        for (ChimeraAntlrParser.AnnotationContext aCtx : ctx.annotations().annotation())
            annotations.add((Expr.AnnotationExpr) visit(aCtx));
        return new Expr.ParameterExpr(
                annotations,
                id(ctx.Identifier()),
                Optional.ofNullable(ctx.type()).map(parent.getTypeBuilder()::visit),
                Optional.ofNullable(ctx.expr()).map(this::visit),
                pos(ctx)
        );
    }

    @Override
    public Expr visitIntLiteralPrimary(ChimeraAntlrParser.IntLiteralPrimaryContext ctx) {
        return new Expr.LiteralExpr.Int(new BigInteger(ctx.IntLiteral().getText()), pos(ctx));
    }

    @Override
    public Expr visitFloatLiteralPrimary(ChimeraAntlrParser.FloatLiteralPrimaryContext ctx) {
        return new Expr.LiteralExpr.Float(new BigDecimal(ctx.FloatLiteral().getText()), pos(ctx));
    }

    @Override
    public Expr visitStringLiteralPrimary(ChimeraAntlrParser.StringLiteralPrimaryContext ctx) {
        String raw = ctx.StringLiteral().getText();
        return new Expr.LiteralExpr.String(unescape(raw.substring(1, raw.length() - 1)), pos(ctx));
    }

    @Override
    public Expr visitListPrimary(ChimeraAntlrParser.ListPrimaryContext ctx) {
        List<Expr> exprs = new ArrayList<>();
        for (ChimeraAntlrParser.ExprContext eCtx : ctx.expr()) exprs.add(visit(eCtx));
        return new Expr.LiteralExpr.List(exprs, pos(ctx));
    }

    @Override
    public Expr visitMapPrimary(ChimeraAntlrParser.MapPrimaryContext ctx) {
        Map<Expr, Expr> exprPerExpr = new HashMap<>();
        List<ChimeraAntlrParser.ExprContext> expr = ctx.expr();
        for (int i = 0; i < expr.size(); i += 2) exprPerExpr.put(visit(expr.get(i)), visit(expr.get(i+1)));
        return new Expr.LiteralExpr.Map(exprPerExpr, pos(ctx));
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
    public Expr visitIdentifierPrimary(ChimeraAntlrParser.IdentifierPrimaryContext ctx) {
        return new Expr.VarExpr(id(ctx.Identifier()), pos(ctx));
    }

    @Override
    public Expr visitExprParenPrimary(ChimeraAntlrParser.ExprParenPrimaryContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Expr visitAnnotationExpr(ChimeraAntlrParser.AnnotationExprContext ctx) {
        return new Expr.AnnotationExpr(id(ctx.Identifier()), Optional.ofNullable(ctx.arguments()).map(arguments -> {
            List<Expr> args = new ArrayList<>();
            for (ChimeraAntlrParser.ArgumentContext aCtx : arguments.argument()) args.add(visit(aCtx));
            return args;
        }), pos(ctx));
    }

    private Expr buildPostfixExpr(Expr expr, List<ChimeraAntlrParser.PostfixContext> postfixes) {
        for (ChimeraAntlrParser.PostfixContext postfix : postfixes) {
            expr = applyPostfix(expr, postfix);
        }
        return expr;
    }

    private Expr applyPostfix(Expr expr, ChimeraAntlrParser.PostfixContext postfix) {
        return switch (postfix) {
            case ChimeraAntlrParser.ArgumentsPostfixContext ctx -> applyArgumentsPostfix(expr, ctx);
            case ChimeraAntlrParser.MemberAccessPostfixContext ctx -> applyMemberAccessPostfix(expr, ctx);
            case ChimeraAntlrParser.StrictUnwrapPostfixContext ctx -> applyUnwrapPostfix(expr, ctx, NullAccessMode.REQUIRE_NONNULL);
            case ChimeraAntlrParser.UnwrapPostfixContext ctx -> applyUnwrapPostfix(expr, ctx, NullAccessMode.PROPAGATE_NULL);
            default -> throw new AstBuildException("Unknown postfix type");
        };
    }

    @Contract("_, _ -> new")
    private @NotNull Expr applyArgumentsPostfix(Expr expr, ChimeraAntlrParser.@NotNull ArgumentsPostfixContext ctx) {
        List<Expr> arguments = new ArrayList<>();
        for (ChimeraAntlrParser.ArgumentContext aCtx : ctx.arguments().argument())
            arguments.add(visit(aCtx));
        return new Expr.CallExpr(expr, arguments, pos(ctx));
    }

    @Contract("_, _ -> new")
    private @NotNull Expr applyMemberAccessPostfix(Expr expr, ChimeraAntlrParser.@NotNull MemberAccessPostfixContext ctx) {
        return new Expr.MemberAccessExpr(expr, ctx.QuestionMark() == null ? NullAccessMode.REQUIRE_NONNULL : NullAccessMode.PROPAGATE_NULL, id(ctx.Identifier()), pos(ctx));
    }

    @Contract("_, _, _ -> new")
    private @NotNull Expr applyUnwrapPostfix(Expr expr, ParserRuleContext ctx, NullAccessMode mode) {
        return new Expr.UnwrapExpr(expr, mode, pos(ctx));
    }
}
