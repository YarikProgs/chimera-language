package net.aros.chimera.parsing;

import net.aros.chimera.ChimeraParser;
import net.aros.chimera.ChimeraParserBaseVisitor;
import net.aros.chimera.ast.Either;
import net.aros.chimera.ast.SourcePos;
import net.aros.chimera.ast.Modifier;
import net.aros.chimera.ast.first.Expr;
import net.aros.chimera.ast.first.Node;
import net.aros.chimera.ast.first.Program;
import net.aros.chimera.ast.first.Stmt;
import net.aros.chimera.ast.ops.AssignmentOp;
import net.aros.chimera.ast.ops.BinaryOp;
import net.aros.chimera.ast.ops.UnaryOp;
import net.aros.chimera.ast.ops.NullAccessMode;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.Normalizer;
import java.util.*;
import java.util.function.IntFunction;

import static com.ibm.icu.impl.Utility.unescape;

public class Antlr2ChiVisitor extends ChimeraParserBaseVisitor<Node> {
    @Override
    public Node visitProgram(ChimeraParser.ProgramContext ctx) {
        return new Program(ctx.stmt().stream().map(s -> (Stmt) visit(s)).toList());
    }

    @Override
    public Node visitStmt(ChimeraParser.StmtContext ctx) {
        if (ctx.ifStmt() != null) return visit(ctx.ifStmt());
        if (ctx.doWhileStmt() != null) return visit(ctx.doWhileStmt());
        if (ctx.whileStmt() != null) return visit(ctx.whileStmt());
        if (ctx.forStmt() != null) return visit(ctx.forStmt());
        if (ctx.exprStmt() != null) return visit(ctx.exprStmt());
        if (ctx.fnStmt() != null) return visit(ctx.fnStmt());
        if (ctx.returnStmt() != null) return visit(ctx.returnStmt());
        throw new IllegalArgumentException("Unknown stmt");
    }

    @Override
    public Node visitFnStmt(ChimeraParser.FnStmtContext ctx) {
        SourcePos pos = pos(ctx);
        // TODO: 21.05.2026 reattach annotations

        return new Stmt.ExprStmt(
                annotations(ctx.annotations()),
                new Expr.AssignExpr(
                        ctx.modifier().stream().map(this::mod).toList(),
                        new Expr.VarExpr(identifier(ctx.Identifier()), pos),
                        Optional.of(new Expr.TypeExpr.FunctionType(
                                ctx.parameters().parameter().stream().map(p -> ((Expr.ParameterExpr) visit(p)).type().orElse(new Expr.TypeExpr.IdentifierType("any", pos(p)))).toList(),
                                ctx.type() == null ? new Expr.TypeExpr.IdentifierType("any", pos(ctx.Colon().getSymbol())) : (Expr.TypeExpr) visit(ctx.type()),
                                pos(ctx)
                        )),
                        new Expr.LambdaExpr(
                                ctx.parameters().parameter().stream().map(p -> (Expr.ParameterExpr) visit(p)).toList(),
                                visitNullable(ctx.type(), Expr.TypeExpr.class),
                                ctx.block() == null ? Either.right((Expr) visit(ctx.expr())) : Either.left((Stmt.BlockStmt) visit(ctx.block())),
                                pos
                        ),
                        pos
                ), pos);
    }

    @Override
    public Node visitParameter(ChimeraParser.ParameterContext ctx) {
        return new Expr.ParameterExpr(annotations(ctx.annotations()), identifier(ctx.Identifier()), visitNullable(ctx.type(), Expr.TypeExpr.class), visitNullable(ctx.expr(), Expr.class), pos(ctx));
    }

    @Override
    public Node visitReturnStmt(ChimeraParser.ReturnStmtContext ctx) {
        return new Stmt.ReturnStmt(annotations(ctx.annotations()), visitNullable(ctx.expr(), Expr.class), pos(ctx));
    }

    @Override
    public Node visitExprStmt(ChimeraParser.ExprStmtContext ctx) {
        return new Stmt.ExprStmt(annotations(ctx.annotations()), (Expr) visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Node visitIfStmt(ChimeraParser.IfStmtContext ctx) {
        if (ctx.parenIfStmt() != null) return visit(ctx.parenIfStmt());
        if (ctx.parenlessIfStmt() != null) return visit(ctx.parenlessIfStmt());

        throw new IllegalArgumentException("Unknown if stmt");
    }

    @Override
    public Node visitParenIfStmt(ChimeraParser.ParenIfStmtContext ctx) {
        return new Stmt.IfStmt(annotations(ctx.annotations()), (Expr) visit(ctx.expr()), (Stmt) visit(ctx.blockOrStmt(0)), visitNullable(ctx.blockOrStmt(1), Stmt.class), pos(ctx));
    }

    @Override
    public Node visitParenlessIfStmt(ChimeraParser.ParenlessIfStmtContext ctx) {
        return new Stmt.IfStmt(annotations(ctx.annotations()), (Expr) visit(ctx.expr()), (Stmt) visit(ctx.blockOrStmt(0)), visitNullable(ctx.blockOrStmt(1), Stmt.class), pos(ctx));
    }

    @Override
    public Node visitForStmt(ChimeraParser.ForStmtContext ctx) {
        if (ctx.parenForStmt() != null) return visit(ctx.parenForStmt());
        if (ctx.parenlessForStmt() != null) return visit(ctx.parenlessForStmt());

        throw new IllegalArgumentException("Unknown for stmt");
    }

    @Override
    public Node visitParenForStmt(ChimeraParser.ParenForStmtContext ctx) {
        return new Stmt.ForStmt(
                annotations(ctx.annotations()),
                ctx.Identifier().stream().map(TerminalNode::getText).toList(),
                (Expr) visit(ctx.expr()),
                (Stmt) visit(ctx.blockOrStmt()),
                pos(ctx)
        );
    }

    @Override
    public Node visitParenlessForStmt(ChimeraParser.ParenlessForStmtContext ctx) {
        return new Stmt.ForStmt(
                annotations(ctx.annotations()),
                ctx.Identifier().stream().map(TerminalNode::getText).toList(),
                (Expr) visit(ctx.expr()),
                (Stmt) visit(ctx.blockOrStmt()),
                pos(ctx)
        );
    }

    @Override
    public Node visitDoWhileStmt(ChimeraParser.DoWhileStmtContext ctx) {
        if (ctx.parenDoWhileStmt() != null) return visit(ctx.parenDoWhileStmt());
        if (ctx.parenlessDoWhileStmt() != null) return visit(ctx.parenlessDoWhileStmt());

        throw new IllegalArgumentException("Unknown while stmt");
    }

    @Override
    public Node visitParenDoWhileStmt(ChimeraParser.ParenDoWhileStmtContext ctx) {
        return new Stmt.DoWhileStmt(annotations(ctx.annotations()), (Stmt.BlockStmt) visit(ctx.block()), (Expr) visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Node visitParenlessDoWhileStmt(ChimeraParser.ParenlessDoWhileStmtContext ctx) {
        return new Stmt.DoWhileStmt(annotations(ctx.annotations()), (Stmt.BlockStmt) visit(ctx.block()), (Expr) visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Node visitWhileStmt(ChimeraParser.WhileStmtContext ctx) {
        if (ctx.parenWhileStmt() != null) return visit(ctx.parenWhileStmt());
        if (ctx.parenlessWhileStmt() != null) return visit(ctx.parenlessWhileStmt());

        throw new IllegalArgumentException("Unknown while stmt");
    }

    @Override
    public Node visitParenWhileStmt(ChimeraParser.ParenWhileStmtContext ctx) {
        return new Stmt.WhileStmt(annotations(ctx.annotations()), (Expr) visit(ctx.expr()), (Stmt.BlockStmt) visit(ctx.block()), pos(ctx));
    }

    @Override
    public Node visitParenlessWhileStmt(ChimeraParser.ParenlessWhileStmtContext ctx) {
        return new Stmt.WhileStmt(annotations(ctx.annotations()), (Expr) visit(ctx.expr()), (Stmt.BlockStmt) visit(ctx.block()), pos(ctx));
    }

    @Override
    public Node visitBlock(ChimeraParser.BlockContext ctx) {
        return new Stmt.BlockStmt(ctx.stmt().stream().map(s -> (Stmt) visit(s)).toList(), pos(ctx));
    }

    @Override
    public Node visitLambda(ChimeraParser.LambdaContext ctx) {
        return new Expr.LambdaExpr(
                ctx.parameters().parameter().stream().map(p -> (Expr.ParameterExpr) visit(p)).toList(),
                visitNullable(ctx.type(), Expr.TypeExpr.class),
                ctx.block() == null ? Either.right((Expr) visit(ctx.expr())) : Either.left((Stmt.BlockStmt) visit(ctx.block())), pos(ctx)
        );
    }

    @Override
    public Node visitAssignment(ChimeraParser.AssignmentContext ctx) {
        if (ctx.nullCoalesce() != null) return visit(ctx.nullCoalesce());

        Expr target = buildPostfixExpr((Expr) visit(ctx.primary()), ctx.postfix());
        Expr initializer = (Expr) visit(ctx.assignment());
        BinaryOp op = AssignmentOp.opByValue(ctx.assignmentOperator().getText());
        if (op != null) {
            initializer = new Expr.BinaryExpr(target, op, initializer, pos(ctx));
        }

        return new Expr.AssignExpr(
                ctx.modifier().stream().map(this::mod).toList(),
                target,
                visitNullable(ctx.type(), Expr.TypeExpr.class),
                initializer,
                pos(ctx)
        );
    }

    @Override
    public Node visitNullCoalesce(ChimeraParser.NullCoalesceContext ctx) {
        Expr expr = (Expr) visit(ctx.ternary(0));
        for (int i = 1; i < ctx.ternary().size(); i++) {
            expr = new Expr.NullCoalesceExpr(expr, (Expr) visit(ctx.ternary(i)), pos(ctx));
        }
        return expr;
    }

    private Expr buildPostfixExpr(Expr expr, List<ChimeraParser.PostfixContext> postfixes) {
        for (ChimeraParser.PostfixContext postfix : postfixes) {
            if (postfix.argumentsPostfix() != null)
                expr = new Expr.CallExpr(expr, postfix.argumentsPostfix().arguments().argument().stream().map(arg -> (Expr.ArgumentExpr) visit(arg)).toList(), pos(postfix));
            else if (postfix.memberAccessPostfix() != null)
                expr = new Expr.MemberAccessExpr(expr, postfix.memberAccessPostfix().QuestionMark() == null ? NullAccessMode.REQUIRE_NONNULL : NullAccessMode.PROPAGATE_NULL, identifier(postfix.memberAccessPostfix().Identifier()), pos(postfix));
            else if (postfix.strictUnwrapPostfix() != null)
                expr = new Expr.UnwrapExpr(expr, NullAccessMode.REQUIRE_NONNULL, pos(postfix));
            else if (postfix.unwrapPostfix() != null)
                expr = new Expr.UnwrapExpr(expr, NullAccessMode.PROPAGATE_NULL, pos(postfix));
        }
        return expr;
    }

    private Modifier mod(ChimeraParser.ModifierContext mod) {
        if (mod.Const() != null) return Modifier.CONST;
        if (mod.At() != null) return Modifier.STATIC;
        throw new IllegalArgumentException("Unknown mod");
    }

    @Override
    public Node visitTernary(ChimeraParser.TernaryContext ctx) {
        Expr condition = (Expr) visit(ctx.logicalOr());
        if (ctx.QuestionMark() == null) return condition;

        Expr thenBranch = (Expr) visit(ctx.expr());
        Expr elseBranch = (Expr) visit(ctx.ternary());

        return new Expr.TernaryExpr(condition, thenBranch, elseBranch, pos(ctx));
    }

    @Override
    public Node visitNamedArgument(ChimeraParser.NamedArgumentContext ctx) {
        return new Expr.ArgumentExpr(Optional.of(identifier(ctx.Identifier())), (Expr) visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Node visitPositionalArgument(ChimeraParser.PositionalArgumentContext ctx) {
        return new Expr.ArgumentExpr(Optional.empty(), (Expr) visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Node visitLogicalOr(ChimeraParser.LogicalOrContext ctx) {
        return leftAssociative(ctx, ctx.logicalXor().size(), ctx::logicalXor);
    }

    @Override
    public Node visitLogicalXor(ChimeraParser.LogicalXorContext ctx) {
        return leftAssociative(ctx, ctx.logicalAnd().size(), ctx::logicalAnd);
    }

    @Override
    public Node visitLogicalAnd(ChimeraParser.LogicalAndContext ctx) {
        return leftAssociative(ctx, ctx.bitwiseOr().size(), ctx::bitwiseOr);
    }

    @Override
    public Node visitBitwiseOr(ChimeraParser.BitwiseOrContext ctx) {
        return leftAssociative(ctx, ctx.bitwiseXor().size(), ctx::bitwiseXor);
    }

    @Override
    public Node visitBitwiseXor(ChimeraParser.BitwiseXorContext ctx) {
        return leftAssociative(ctx, ctx.bitwiseAnd().size(), ctx::bitwiseAnd);
    }

    @Override
    public Node visitBitwiseAnd(ChimeraParser.BitwiseAndContext ctx) {
        return leftAssociative(ctx, ctx.equality().size(), ctx::equality);
    }

    @Override
    public Node visitEquality(ChimeraParser.EqualityContext ctx) {
        return leftAssociative(ctx, ctx.comparison().size(), ctx::comparison);
    }

    @Override
    public Node visitComparison(ChimeraParser.ComparisonContext ctx) {
        return leftAssociative(ctx, ctx.shift().size(), ctx::shift);
    }

    @Override
    public Node visitShift(ChimeraParser.ShiftContext ctx) {
        return leftAssociative(ctx, ctx.term().size(), ctx::term);
    }

    @Override
    public Node visitTerm(ChimeraParser.TermContext ctx) {
        return leftAssociative(ctx, ctx.factor().size(), ctx::factor);
    }

    @Override
    public Node visitFactor(ChimeraParser.FactorContext ctx) {
        return leftAssociative(ctx, ctx.unary().size(), ctx::unary);
    }

    @Override
    public Node visitUnary(ChimeraParser.UnaryContext ctx) {
        if (ctx.call() != null) return visit(ctx.call());
        if (ctx.shortTry() != null) return visit(ctx.shortTry());
        var op = UnaryOp.byValue(ctx.getChild(0).getText());
        Expr right = (Expr) visit(ctx.unary());
        return new Expr.UnaryExpr(op.orElseThrow(), right, pos(ctx));
    }

    @Override
    public Node visitShortTry(ChimeraParser.ShortTryContext ctx) {
        return new Expr.ShortTryExpr((Expr) visit(ctx.unary()), pos(ctx));
    }

    @Override
    public Node visitCall(ChimeraParser.CallContext ctx) {
        return buildPostfixExpr((Expr) visit(ctx.primary()), ctx.postfix());
    }

    @Override
    public Node visitListLiteral(ChimeraParser.ListLiteralContext ctx) {
        return new Expr.LiteralExpr(ctx.expr().stream().map(this::visit).toList(), pos(ctx));
    }

    @Override
    public Node visitMapLiteral(ChimeraParser.MapLiteralContext ctx) {
        Map<Node, Node> map = new HashMap<>();
        for (int i = 0; i < ctx.Colon().size(); i++) {
            map.put(visit(ctx.expr(i)), visit(ctx.expr(i + 1)));
        }
        return new Expr.LiteralExpr(Map.copyOf(map), pos(ctx));
    }

    @Override
    public Node visitPrimary(ChimeraParser.PrimaryContext ctx) {
        if (ctx.IntLiteral() != null) return new Expr.LiteralExpr(new BigInteger(ctx.IntLiteral().getText()), pos(ctx));
        if (ctx.FloatLiteral() != null)
            return new Expr.LiteralExpr(new BigDecimal(ctx.FloatLiteral().getText()), pos(ctx));
        if (ctx.StringLiteral() != null) {
            String raw = ctx.getText();
            return new Expr.LiteralExpr(unescape(raw.substring(1, raw.length() - 1)), pos(ctx));
        }
        if (ctx.listLiteral() != null) return visit(ctx.listLiteral());
        if (ctx.mapLiteral() != null) return visit(ctx.mapLiteral());
        if (ctx.True() != null) return new Expr.LiteralExpr(true, pos(ctx));
        if (ctx.False() != null) return new Expr.LiteralExpr(false, pos(ctx));
        if (ctx.Null() != null) return new Expr.LiteralExpr(null, pos(ctx));
        if (ctx.Identifier() != null) return new Expr.VarExpr(
                identifier(ctx.Identifier()),
                pos(ctx)
        );
        if (ctx.expr() != null) return visit(ctx.expr());

        throw new IllegalArgumentException("Unknown primary");
    }

    @Override
    public Node visitArgument(ChimeraParser.ArgumentContext ctx) {
        if (ctx.positionalArgument() != null) return visit(ctx.positionalArgument());
        if (ctx.namedArgument() != null) return visit(ctx.namedArgument());
        throw new IllegalArgumentException("Unknown argument");
    }

    @Override
    public Node visitExpr(ChimeraParser.ExprContext ctx) {
        if (ctx.assignment() != null) return visit(ctx.assignment());
        if (ctx.lambda() != null) return visit(ctx.lambda());

        throw new IllegalArgumentException("Unknown expr");
    }

    @Override
    public Node visitBlockOrStmt(ChimeraParser.BlockOrStmtContext ctx) {
        if (ctx.block() != null) return visit(ctx.block());
        return visit(ctx.stmt());
    }

    private static SourcePos pos(ParserRuleContext ctx) {
        return pos(ctx.getStart());
    }

    private static SourcePos pos(Token symbol) {
        return new SourcePos(symbol.getLine(), symbol.getCharPositionInLine());
    }

    private Expr leftAssociative(ParserRuleContext ctx, int size, IntFunction<ParseTree> exprGetter) {
        Expr expr = (Expr) visit(exprGetter.apply(0));
        for (int i = 1; i < size; i++) {
            var op = BinaryOp.byValue(ctx.getChild(2 * i - 1).getText()).orElseThrow();
            Expr right = (Expr) visit(exprGetter.apply(i));
            expr = new Expr.BinaryExpr(expr, op, right, pos(ctx));
        }
        return expr;
    }

    private <T extends Node> Optional<T> visitNullable(ParseTree parseTree, Class<T> type) {
        if (parseTree == null) return Optional.empty();
        return Optional.of(type.cast(visit(parseTree)));
    }

    // TYPES

    @Override
    public Node visitType(ChimeraParser.TypeContext ctx) {
        return visit(ctx.unionType());
    }

    @Override
    public Node visitUnionType(ChimeraParser.UnionTypeContext ctx) {
        if (ctx.intersectionType().size() == 1)
            return visit(ctx.intersectionType(0));
        return new Expr.TypeExpr.UnionTypeExpr(ctx.intersectionType().stream().map(intersection -> (Expr.TypeExpr) visit(intersection)).toList(), pos(ctx));
    }

    @Override
    public Node visitIntersectionType(ChimeraParser.IntersectionTypeContext ctx) {
        if (ctx.postfixType().size() == 1)
            return visit(ctx.postfixType(0));
        return new Expr.TypeExpr.IntersectionTypeExpr(ctx.postfixType().stream().map(intersection -> (Expr.TypeExpr) visit(intersection)).toList(), pos(ctx));
    }

    @Override
    public Node visitPostfixType(ChimeraParser.PostfixTypeContext ctx) {
        Expr.TypeExpr type = (Expr.TypeExpr) visit(ctx.primaryType());
        return ctx.QuestionMark() != null ? new Expr.TypeExpr.NullableTypeExpr(type, pos(ctx)) : type;
    }

    @Override
    public Node visitPrimaryType(ChimeraParser.PrimaryTypeContext ctx) {
        if (ctx.Identifier() != null) return new Expr.TypeExpr.IdentifierType(identifier(ctx.Identifier()), pos(ctx));
        if (ctx.tupleType() != null) return visit(ctx.tupleType());
        if (ctx.listType() != null) return visit(ctx.listType());
        if (ctx.mapType() != null) return visit(ctx.mapType());
        if (ctx.functionType() != null) return visit(ctx.functionType());
        return visit(ctx.type());
    }

    @Override
    public Node visitTupleType(ChimeraParser.TupleTypeContext ctx) {
        return new Expr.TypeExpr.TupleTypeExpr(ctx.type().stream().map(t -> (Expr.TypeExpr) visit(t)).toList(), pos(ctx));
    }

    @Override
    public Node visitListType(ChimeraParser.ListTypeContext ctx) {
        return new Expr.TypeExpr.ListTypeExpr((Expr.TypeExpr) visit(ctx.type()), pos(ctx));
    }

    @Override
    public Node visitMapType(ChimeraParser.MapTypeContext ctx) {
        return new Expr.TypeExpr.MapTypeExpr((Expr.TypeExpr) visit(ctx.type(0)), (Expr.TypeExpr) visit(ctx.type(1)), pos(ctx));
    }

    @Override
    public Node visitFunctionType(ChimeraParser.FunctionTypeContext ctx) {
        int size = ctx.type().size();
        List<Expr.TypeExpr> types = ctx.type().stream().map(t -> (Expr.TypeExpr) visit(t)).toList();
        return new Expr.TypeExpr.FunctionType(
                size == 1 ? List.of() : types.subList(0, size - 2),
                types.getLast(),
                pos(ctx)
        );
    }

    @Override
    public Node visitAnnotationExpr(ChimeraParser.AnnotationExprContext ctx) {
        return new Expr.AnnotationExpr(
                identifier(ctx.Identifier()),
                ctx.arguments() == null ? Optional.empty() : Optional.of(ctx.arguments().argument().stream().map(a -> (Expr.ArgumentExpr) visit(a)).toList()),
                pos(ctx)
        );
    }

    private List<Expr.AnnotationExpr> annotations(ChimeraParser.AnnotationsContext ctx) {
        return ctx.annotation().stream().map(a -> (Expr.AnnotationExpr) visit(a.annotationExpr())).toList();
    }

    private String identifier(TerminalNode node) {
        return Normalizer.normalize(node.getText(), Normalizer.Form.NFC);
    }
}
