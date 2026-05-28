package net.aros.chimera.parsing.astbuilding.builders;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.ChimeraAntlrParserBaseVisitor;
import net.aros.chimera.ast.first.Type;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.aros.chimera.parsing.astbuilding.AstUtils.id;
import static net.aros.chimera.parsing.astbuilding.AstUtils.pos;

public class ChimeraTypeBuilder extends ChimeraAntlrParserBaseVisitor<Type> {
    @Override
    public Type visitNullableType(ChimeraAntlrParser.@NotNull NullableTypeContext ctx) {
        return new Type.NullableType(visit(ctx.type()), pos(ctx));
    }

    @Override
    public Type visitIntersectionType(ChimeraAntlrParser.@NotNull IntersectionTypeContext ctx) {
        return new Type.IntersectionType(visit(ctx.left), visit(ctx.right), pos(ctx));
    }

    @Override
    public Type visitUnionType(ChimeraAntlrParser.@NotNull UnionTypeContext ctx) {
        return new Type.UnionType(visit(ctx.left), visit(ctx.right), pos(ctx));
    }

    @Override
    public Type visitTupleType(ChimeraAntlrParser.@NotNull TupleTypeContext ctx) {
        List<Type> types = new ArrayList<>();
        for (ChimeraAntlrParser.TypeContext tCtx : ctx.type()) types.add(visit(tCtx));
        return new Type.TupleType(types, pos(ctx));
    }

    @Override
    public Type visitFunctionType(ChimeraAntlrParser.@NotNull FunctionTypeContext ctx) {
        List<Type> args = new ArrayList<>();
        for (ChimeraAntlrParser.TypeContext tCtx : ctx.args) args.add(visit(tCtx));
        return new Type.FunctionType(args, visit(ctx.ret), pos(ctx));
    }

    @Override
    public Type visitListType(ChimeraAntlrParser.@NotNull ListTypeContext ctx) {
        return new Type.ListType(visit(ctx.type()), pos(ctx));
    }

    @Override
    public Type visitMapType(ChimeraAntlrParser.@NotNull MapTypeContext ctx) {
        return new Type.MapType(visit(ctx.key), visit(ctx.value), pos(ctx));
    }

    @Override
    public Type visitIdentifierType(ChimeraAntlrParser.@NotNull IdentifierTypeContext ctx) {
        return new Type.IdentifierType(id(ctx.Identifier()), pos(ctx));
    }
}
