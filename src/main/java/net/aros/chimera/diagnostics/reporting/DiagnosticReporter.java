package net.aros.chimera.diagnostics.reporting;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.diagnostics.DiagnosticCollector;
import net.aros.chimera.diagnostics.util.TokenPositionHelper;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;

public abstract class DiagnosticReporter {
    protected final DiagnosticCollector diagnosticCollector;
    protected final TokenPositionHelper posHelper;

    public DiagnosticReporter(DiagnosticCollector diagnosticCollector, TokenPositionHelper posHelper) {
        this.diagnosticCollector = diagnosticCollector;
        this.posHelper = posHelper;
    }

    public abstract void onUnterminatedString(Token token);

    public abstract void onUnterminatedComment(Token token);

    public abstract void onUnmatchedCommentClosure(Token token);

    public abstract void onInvalidEscape(Token token);

    public abstract void onInvalidNumber(Token token);

    public abstract void onInvalidCharacter(Token token);

    public abstract void onBinaryMissingRhs(ParserRuleContext ctx, String op);

    public abstract void onBinaryMissingLhs(ParserRuleContext ctx, String text);

    public abstract void onTernaryMissingThen(ChimeraAntlrParser.@NotNull TernaryExprMissingThenContext ctx);

    public abstract void onTernaryMissingElse(ChimeraAntlrParser.@NotNull TernaryExprMissingElseContext ctx);

    public abstract void onTernaryMissingThenAndElse(ChimeraAntlrParser.@NotNull TernaryExprMissingThenAndElseContext ctx);

    public abstract void onLambdaMissingParameters(ChimeraAntlrParser.LambdaExprMissingParametersContext ctx);

    public abstract void onLambdaMissingReturnType(ChimeraAntlrParser.LambdaExprMissingReturnTypeContext ctx);

    public abstract void onLambdaMissingBody(ChimeraAntlrParser.LambdaExprMissingBodyContext ctx);

    public abstract void onAssignmentMissingType(ChimeraAntlrParser.AssignmentExprMissingTypeContext ctx);

    public abstract void onAssignmentMissingRhs(ChimeraAntlrParser.AssignmentExprMissingRhsContext ctx);

    public abstract void onAssignmentMissingLhs(ChimeraAntlrParser.AssignmentExprMissingLhsContext ctx);

    public abstract void onListPrimaryUnclosed(ChimeraAntlrParser.ListPrimaryUnclosedContext ctx);

    public abstract void onMapPrimaryUnclosed(ChimeraAntlrParser.MapPrimaryUnclosedContext ctx);

    public abstract void onUnclosedPrimary(ChimeraAntlrParser.ExprParenPrimaryUnclosedContext ctx);

    public abstract void onPairMissingKey(ChimeraAntlrParser.MapPairWithoutKeyContext withoutKeyCtx);

    public abstract void onPairMissingValue(ChimeraAntlrParser.MapPairWithoutValueContext withoutValueCtx);

    public abstract void onPairMissingAll(ChimeraAntlrParser.MapPairWithoutAllContext withoutAllCtx);

    public abstract void onIntersectionTypeMissingRhs(ChimeraAntlrParser.IntersectionTypeMissingRhsContext ctx);

    public abstract void onIntersectionTypeMissingLhs(ChimeraAntlrParser.IntersectionTypeMissingLhsContext ctx);

    public abstract void onUnionTypeMissingRhs(ChimeraAntlrParser.UnionTypeMissingRhsContext ctx);

    public abstract void onUnionTypeMissingLhs(ChimeraAntlrParser.UnionTypeMissingLhsContext ctx);

    public abstract void onTupleTypeMissingClosure(ChimeraAntlrParser.TupleTypeMissingClosureContext ctx);

    public abstract void onListTypeMissingClosure(ChimeraAntlrParser.ListTypeMissingClosureContext ctx);

    public abstract void onMapTypeMissingKey(ChimeraAntlrParser.MapTypeMissingKeyContext ctx);

    public abstract void onMapTypeMissingValue(ChimeraAntlrParser.MapTypeMissingValueContext ctx);

    public abstract void onMapTypeMissingClosure(ChimeraAntlrParser.MapTypeMissingClosureContext ctx);

    public abstract void onParenTypeMissingClosure(ChimeraAntlrParser.ParenTypeMissingClosureContext ctx);
}
