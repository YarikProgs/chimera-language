package net.aros.chimera.parsing.astbuilding;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.ChimeraAntlrParserBaseVisitor;
import net.aros.chimera.ast.first.Expr;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.aros.chimera.parsing.astbuilding.AstUtils.id;
import static net.aros.chimera.parsing.astbuilding.AstUtils.pos;

public class ChimeraTypeBuilder extends ChimeraAntlrParserBaseVisitor<Expr.TypeExpr> {
    @Override
    public Expr.TypeExpr visitUnionType(ChimeraAntlrParser.@NotNull UnionTypeContext ctx) {
        if (ctx.intersectionType().size() == 1) return visit(ctx.intersectionType(0));
        List<Expr.TypeExpr> types = new ArrayList<>();
        for (ChimeraAntlrParser.IntersectionTypeContext iCtx : ctx.intersectionType()) types.add(visit(iCtx));
        return new Expr.TypeExpr.UnionTypeExpr(types, pos(ctx));
    }

    @Override
    public Expr.TypeExpr visitIntersectionType(ChimeraAntlrParser.@NotNull IntersectionTypeContext ctx) {
        if (ctx.postfixType().size() == 1) return visit(ctx.postfixType(0));
        List<Expr.TypeExpr> types = new ArrayList<>();
        for (ChimeraAntlrParser.PostfixTypeContext iCtx : ctx.postfixType()) types.add(visit(iCtx));
        return new Expr.TypeExpr.IntersectionTypeExpr(types, pos(ctx));
    }

    @Override
    public Expr.TypeExpr visitPostfixType(ChimeraAntlrParser.@NotNull PostfixTypeContext ctx) {
        if (ctx.QuestionMark() == null) return visit(ctx.primaryType());
        return new Expr.TypeExpr.NullableTypeExpr(visit(ctx.primaryType()), pos(ctx));
    }

    @Override
    public Expr.TypeExpr visitIdentifierType(ChimeraAntlrParser.@NotNull IdentifierTypeContext ctx) {
        return new Expr.TypeExpr.IdentifierType(id(ctx.Identifier()), pos(ctx));
    }

    @Override
    public Expr.TypeExpr visitTupleType(ChimeraAntlrParser.@NotNull TupleTypeContext ctx) {
        List<Expr.TypeExpr> types = new ArrayList<>();
        for (ChimeraAntlrParser.TypeContext tCtx : ctx.type()) types.add(visit(tCtx));
        return new Expr.TypeExpr.TupleTypeExpr(types, pos(ctx));
    }

    @Override
    public Expr.TypeExpr visitFunctionType(ChimeraAntlrParser.@NotNull FunctionTypeContext ctx) {
        List<Expr.TypeExpr> types = new ArrayList<>();
        for (ChimeraAntlrParser.TypeContext tCtx : ctx.type()) types.add(visit(tCtx));
        int size = types.size();
        return new Expr.TypeExpr.FunctionType(size == 1 ? List.of() : types.subList(0, size - 2), types.getLast(), pos(ctx));
    }

    @Override
    public Expr.TypeExpr visitListType(ChimeraAntlrParser.@NotNull ListTypeContext ctx) {
        return new Expr.TypeExpr.ListTypeExpr(visit(ctx.type()), pos(ctx));
    }

    @Override
    public Expr.TypeExpr visitMapType(ChimeraAntlrParser.@NotNull MapTypeContext ctx) {
        return new Expr.TypeExpr.MapTypeExpr(visit(ctx.type(0)), visit(ctx.type(1)), pos(ctx));
    }
}
