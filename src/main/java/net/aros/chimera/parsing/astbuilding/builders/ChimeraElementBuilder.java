package net.aros.chimera.parsing.astbuilding.builders;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.ChimeraAntlrParserBaseVisitor;
import net.aros.chimera.ast.first.Node;
import net.aros.chimera.parsing.astbuilding.ChimeraAstBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.aros.chimera.parsing.astbuilding.AstUtils.id;
import static net.aros.chimera.parsing.astbuilding.AstUtils.pos;

public class ChimeraElementBuilder extends ChimeraAntlrParserBaseVisitor<Node> {
    private final ChimeraAstBuilder parent;

    public ChimeraElementBuilder(ChimeraAstBuilder parent) {
        this.parent = parent;
    }

    @Override
    public Node visitNamedArgument(ChimeraAntlrParser.@NotNull NamedArgumentContext ctx) {
        return new Node.Argument(Optional.of(id(ctx.Identifier())), parent.getExprBuilder().visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Node visitPositionalArgument(ChimeraAntlrParser.PositionalArgumentContext ctx) {
        return new Node.Argument(Optional.empty(), parent.getExprBuilder().visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Node visitAnnotation(ChimeraAntlrParser.@NotNull AnnotationContext ctx) {
        return new Node.Annotation(id(ctx.Identifier()), Optional.ofNullable(ctx.arguments()).map(this::buildArguments), pos(ctx));
    }

    @Override
    public Node visitParameter(ChimeraAntlrParser.@NotNull ParameterContext ctx) {
        return new Node.Parameter(
                buildAnnotations(ctx.annotations()),
                id(ctx.Identifier()),
                Optional.ofNullable(ctx.type()).map(parent.getTypeBuilder()::visit),
                Optional.ofNullable(ctx.expr()).map(parent.getExprBuilder()::visit),
                pos(ctx)
        );
    }

    public List<Node.Annotation> buildAnnotations(ChimeraAntlrParser.@NotNull AnnotationsContext ctx) {
        List<Node.Annotation> annotations = new ArrayList<>();
        for (ChimeraAntlrParser.AnnotationContext aCtx : ctx.annotation()) annotations.add((Node.Annotation) visit(aCtx));
        return annotations;
    }

    public List<Node.Argument> buildArguments(ChimeraAntlrParser.@NotNull ArgumentsContext ctx) {
        List<Node.Argument> arguments = new ArrayList<>();
        for (ChimeraAntlrParser.ArgumentContext aCtx : ctx.argument()) arguments.add((Node.Argument) visit(aCtx));
        return arguments;
    }

    public List<Node.Parameter> buildParameters(ChimeraAntlrParser.@NotNull ParametersContext ctx) {
        List<Node.Parameter> parameters = new ArrayList<>();
        for (ChimeraAntlrParser.ParameterContext pCtx : ctx.parameter()) parameters.add(((Node.Parameter) visit(pCtx)));
        return parameters;
    }
}
