package net.aros.chimera.parsing.astbuilding.builders;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.ChimeraAntlrParserBaseVisitor;
import net.aros.chimera.ast.first.Type;
import net.aros.chimera.parsing.astbuilding.ChimeraAstBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.aros.chimera.parsing.astbuilding.AstUtils.id;
import static net.aros.chimera.parsing.astbuilding.AstUtils.pos;

public class ChimeraTypeBuilder extends ChimeraAntlrParserBaseVisitor<Type> {
    private final ChimeraAstBuilder parent;

    public ChimeraTypeBuilder(ChimeraAstBuilder parent) {
        this.parent = parent;
    }

    @Override
    public Type visitNullableType(ChimeraAntlrParser.@NotNull NullableTypeContext ctx) {
        return new Type.NullableType(visit(ctx.type()), pos(ctx));
    }

    @Override
    public Type visitIntersectionTypeMissingRhs(ChimeraAntlrParser.IntersectionTypeMissingRhsContext ctx) {
        parent.getReporter().onIntersectionTypeMissingRhs(ctx);
        return new Type.IntersectionType(visit(ctx.left), new Type.ErrorType(pos(ctx.BitAnd().getSymbol())), pos(ctx));
    }

    @Override
    public Type visitIntersectionTypeMissingLhs(ChimeraAntlrParser.IntersectionTypeMissingLhsContext ctx) {
        parent.getReporter().onIntersectionTypeMissingLhs(ctx);
        return new Type.IntersectionType(new Type.ErrorType(pos(ctx.BitAnd().getSymbol())), visit(ctx.right), pos(ctx));
    }

    @Override
    public Type visitIntersectionType(ChimeraAntlrParser.@NotNull IntersectionTypeContext ctx) {
        return new Type.IntersectionType(visit(ctx.left), visit(ctx.right), pos(ctx));
    }

    @Override
    public Type visitUnionTypeMissingRhs(ChimeraAntlrParser.UnionTypeMissingRhsContext ctx) {
        parent.getReporter().onUnionTypeMissingRhs(ctx);
        return new Type.UnionType(visit(ctx.left), new Type.ErrorType(pos(ctx.BitOr().getSymbol())), pos(ctx));
    }

    @Override
    public Type visitUnionTypeMissingLhs(ChimeraAntlrParser.UnionTypeMissingLhsContext ctx) {
        parent.getReporter().onUnionTypeMissingLhs(ctx);
        return new Type.UnionType(new Type.ErrorType(pos(ctx.BitOr().getSymbol())), visit(ctx.right), pos(ctx));
    }

    @Override
    public Type visitUnionType(ChimeraAntlrParser.@NotNull UnionTypeContext ctx) {
        return new Type.UnionType(visit(ctx.left), visit(ctx.right), pos(ctx));
    }

    @Override
    public Type visitTupleTypeMissingClosure(ChimeraAntlrParser.TupleTypeMissingClosureContext ctx) {
        parent.getReporter().onTupleTypeMissingClosure(ctx);
        List<Type> types = new ArrayList<>();
        for (ChimeraAntlrParser.TypeContext tCtx : ctx.type()) types.add(visit(tCtx));
        return new Type.TupleType(types, pos(ctx));
    }

    @Override
    public Type visitTupleType(ChimeraAntlrParser.@NotNull TupleTypeContext ctx) {
        List<Type> types = new ArrayList<>();
        for (ChimeraAntlrParser.TypeContext tCtx : ctx.type()) types.add(visit(tCtx));
        return new Type.TupleType(types, pos(ctx));
    }

    @Override
    public Type visitFunctionTypeMissingReturn(ChimeraAntlrParser.FunctionTypeMissingReturnContext ctx) {
        List<Type> args = new ArrayList<>();
        for (ChimeraAntlrParser.TypeContext tCtx : ctx.args) args.add(visit(tCtx));
        return new Type.FunctionType(args, new Type.ErrorType(pos(ctx.RArrow().getSymbol())), pos(ctx));
    }

    @Override
    public Type visitFunctionType(ChimeraAntlrParser.@NotNull FunctionTypeContext ctx) {
        List<Type> args = new ArrayList<>();
        for (ChimeraAntlrParser.TypeContext tCtx : ctx.args) args.add(visit(tCtx));
        return new Type.FunctionType(args, visit(ctx.ret), pos(ctx));
    }

    @Override
    public Type visitListTypeMissingClosure(ChimeraAntlrParser.ListTypeMissingClosureContext ctx) {
        parent.getReporter().onListTypeMissingClosure(ctx);
        return new Type.ListType(visit(ctx.type()), pos(ctx));
    }

    @Override
    public Type visitListType(ChimeraAntlrParser.@NotNull ListTypeContext ctx) {
        return new Type.ListType(visit(ctx.type()), pos(ctx));
    }

    @Override
    public Type visitMapTypeMissingKey(ChimeraAntlrParser.MapTypeMissingKeyContext ctx) {
        parent.getReporter().onMapTypeMissingKey(ctx);
        return new Type.MapType(new Type.ErrorType(pos(ctx.Less().getSymbol())), visit(ctx.value), pos(ctx));
    }

    @Override
    public Type visitMapTypeMissingValue(ChimeraAntlrParser.MapTypeMissingValueContext ctx) {
        parent.getReporter().onMapTypeMissingValue(ctx);
        return new Type.MapType(visit(ctx.key), new Type.ErrorType(pos(ctx.Comma().getSymbol())), pos(ctx));
    }

    @Override
    public Type visitMapTypeMissingClosure(ChimeraAntlrParser.MapTypeMissingClosureContext ctx) {
        parent.getReporter().onMapTypeMissingClosure(ctx);
        return new Type.MapType(visit(ctx.key), visit(ctx.value), pos(ctx));
    }

    @Override
    public Type visitMapType(ChimeraAntlrParser.@NotNull MapTypeContext ctx) {
        return new Type.MapType(visit(ctx.key), visit(ctx.value), pos(ctx));
    }

    @Override
    public Type visitIdentifierType(ChimeraAntlrParser.@NotNull IdentifierTypeContext ctx) {
        return new Type.IdentifierType(id(ctx.Identifier()), pos(ctx));
    }

    @Override
    public Type visitParenTypeMissingClosure(ChimeraAntlrParser.ParenTypeMissingClosureContext ctx) {
        parent.getReporter().onParenTypeMissingClosure(ctx);
        return visit(ctx.type());
    }

    @Override
    public Type visitParenType(ChimeraAntlrParser.ParenTypeContext ctx) {
        return visit(ctx.type());
    }
}
