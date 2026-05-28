package net.aros.chimera.diagnostics.reporting;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.diagnostics.DiagnosticCollector;
import net.aros.chimera.diagnostics.util.TokenPositionHelper;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;

public class DefaultDiagnosticReporter extends DiagnosticReporter {
    public DefaultDiagnosticReporter(DiagnosticCollector diagnosticCollector, TokenPositionHelper posHelper) {
        super(diagnosticCollector, posHelper);
    }

    @Override
    public void onUnterminatedString(Token token) {
        diagnosticCollector.reportError(
                posHelper.start(token),
                "unterminated string"
        );
    }

    @Override
    public void onUnterminatedComment(Token token) {
        diagnosticCollector.reportError(
                posHelper.all(token),
                "unclosed comment"
        );
    }

    @Override
    public void onUnmatchedCommentClosure(Token token) {
        diagnosticCollector.reportError(
                posHelper.all(token),
                "unmatched comment closure"
        );
    }

    @Override
    public void onInvalidEscape(Token token) {
        diagnosticCollector.reportError(
                posHelper.all(token),
                "invalid escape"
        );
    }

    @Override
    public void onInvalidNumber(Token token) {
        diagnosticCollector.reportError(
                posHelper.all(token),
                "invalid number"
        );
    }

    @Override
    public void onInvalidCharacter(Token token) {
        diagnosticCollector.reportError(
                posHelper.all(token),
                "unknown character"
        );
    }

    @Override
    public void onBinaryMissingRhs(ParserRuleContext ctx, String op) {
        diagnosticCollector.reportError(
                posHelper.all(ctx),
                "binary operator '" + op + "' requires a right-hand operand"
        );
    }

    @Override
    public void onBinaryMissingLhs(ParserRuleContext ctx, String op) {
        diagnosticCollector.reportError(
                posHelper.all(ctx),
                "binary operator '" + op + "' requires a left-hand operand"
        );
    }

    @Override
    public void onTernaryMissingThen(ChimeraAntlrParser.@NotNull TernaryExprMissingThenContext ctx) {
        diagnosticCollector.reportError(
                posHelper.end(ctx.QuestionMark().getSymbol()),
                "ternary requires 'then' expression"
        );
    }

    @Override
    public void onTernaryMissingElse(ChimeraAntlrParser.@NotNull TernaryExprMissingElseContext ctx) {
        diagnosticCollector.reportError(
                posHelper.end(ctx.thenExpr),
                "ternary requires 'else' expression"
        );
    }

    @Override
    public void onTernaryMissingThenAndElse(ChimeraAntlrParser.@NotNull TernaryExprMissingThenAndElseContext ctx) {
        diagnosticCollector.reportError(
                posHelper.all(ctx),
                "ternary requires 'then' and 'else' expressions"
        );
    }

    @Override
    public void onLambdaMissingParameters(ChimeraAntlrParser.LambdaExprMissingParametersContext ctx) {
        diagnosticCollector.reportError(
                ctx.blockStmt() != null ? posHelper.start(ctx.blockStmt()) : posHelper.start(ctx.expr()),
                "lambda misses parameters"
        );
    }

    @Override
    public void onLambdaMissingReturnType(ChimeraAntlrParser.LambdaExprMissingReturnTypeContext ctx) {
        diagnosticCollector.reportError(
                posHelper.start(ctx.Colon().getSymbol()),
                "lambda misses return type"
        );
    }

    @Override
    public void onLambdaMissingBody(ChimeraAntlrParser.LambdaExprMissingBodyContext ctx) {
        diagnosticCollector.reportError(
                posHelper.end(ctx.parameters()),
                "lambda misses body"
        );
    }

    @Override
    public void onAssignmentMissingType(ChimeraAntlrParser.AssignmentExprMissingTypeContext ctx) {
        diagnosticCollector.reportError(
                posHelper.end(ctx.Colon().getSymbol()),
                "assignment misses type after ':'"
        );
    }

    @Override
    public void onAssignmentMissingRhs(ChimeraAntlrParser.AssignmentExprMissingRhsContext ctx) {
        diagnosticCollector.reportError(
                posHelper.end(ctx.assignmentOperator()),
                "assignment requires rvalue"
        );
    }

    @Override
    public void onAssignmentMissingLhs(ChimeraAntlrParser.AssignmentExprMissingLhsContext ctx) {
        diagnosticCollector.reportError(
                posHelper.start(ctx.assignmentOperator()),
                "assignment requires lvalue"
        );
    }

    @Override
    public void onListPrimaryUnclosed(ChimeraAntlrParser.ListPrimaryUnclosedContext ctx) {
        diagnosticCollector.reportError(
                ctx.expr().isEmpty() ? posHelper.end(ctx.LBracket().getSymbol()) : posHelper.end(ctx.expr().getLast()),
                "unclosed list literal"
        );
    }

    @Override
    public void onMapPrimaryUnclosed(ChimeraAntlrParser.MapPrimaryUnclosedContext ctx) {
        diagnosticCollector.reportError(
                ctx.mapPair().isEmpty() ? posHelper.end(ctx.LBrace().getSymbol()) : posHelper.end(ctx.mapPair().getLast()),
                "unclosed map literal"
        );
    }

    @Override
    public void onUnclosedPrimary(ChimeraAntlrParser.ExprParenPrimaryUnclosedContext ctx) {
        diagnosticCollector.reportError(
                posHelper.end(ctx.expr()),
                "unclosed primary expression"
        );
    }

    @Override
    public void onPairMissingKey(ChimeraAntlrParser.MapPairWithoutKeyContext withoutKeyCtx) {
        diagnosticCollector.reportError(
                posHelper.start(withoutKeyCtx.Colon().getSymbol()),
                "map pair is missing key"
        );
    }

    @Override
    public void onPairMissingValue(ChimeraAntlrParser.MapPairWithoutValueContext withoutValueCtx) {
        diagnosticCollector.reportError(
                posHelper.end(withoutValueCtx.Colon().getSymbol()),
                "map pair is missing value"
        );
    }

    @Override
    public void onPairMissingAll(ChimeraAntlrParser.MapPairWithoutAllContext withoutAllCtx) {
        diagnosticCollector.reportError(
                posHelper.all(withoutAllCtx.Colon().getSymbol()),
                "map pair is missing key and value"
        );
    }

    @Override
    public void onIntersectionTypeMissingRhs(ChimeraAntlrParser.IntersectionTypeMissingRhsContext ctx) {
        diagnosticCollector.reportError(
                posHelper.end(ctx.BitAnd().getSymbol()),
                "intersection type requires a right-hand type"
        );
    }

    @Override
    public void onIntersectionTypeMissingLhs(ChimeraAntlrParser.IntersectionTypeMissingLhsContext ctx) {
        diagnosticCollector.reportError(
                posHelper.start(ctx.BitAnd().getSymbol()),
                "intersection type requires a left-hand type"
        );
    }

    @Override
    public void onUnionTypeMissingRhs(ChimeraAntlrParser.UnionTypeMissingRhsContext ctx) {
        diagnosticCollector.reportError(
                posHelper.end(ctx.BitOr().getSymbol()),
                "union type requires a right-hand type"
        );
    }

    @Override
    public void onUnionTypeMissingLhs(ChimeraAntlrParser.UnionTypeMissingLhsContext ctx) {
        diagnosticCollector.reportError(
                posHelper.start(ctx.BitOr().getSymbol()),
                "union type requires a left-hand type"
        );
    }

    @Override
    public void onTupleTypeMissingClosure(ChimeraAntlrParser.TupleTypeMissingClosureContext ctx) {
        diagnosticCollector.reportError(
                ctx.type().isEmpty() ? posHelper.end(ctx.Less().getSymbol()) : posHelper.end(ctx.type().getLast()),
                "expected '>' to close tuple type"
        );
    }

    @Override
    public void onListTypeMissingClosure(ChimeraAntlrParser.ListTypeMissingClosureContext ctx) {
        diagnosticCollector.reportError(
                ctx.type() == null ? posHelper.end(ctx.Less().getSymbol()) : posHelper.end(ctx.type()),
                "expected '>' to close list type"
        );
    }

    @Override
    public void onMapTypeMissingKey(ChimeraAntlrParser.MapTypeMissingKeyContext ctx) {
        diagnosticCollector.reportError(
                posHelper.start(ctx.Less().getSymbol()),
                "map type requires a key type specification"
        );
    }

    @Override
    public void onMapTypeMissingValue(ChimeraAntlrParser.MapTypeMissingValueContext ctx) {
        diagnosticCollector.reportError(
                posHelper.end(ctx.Comma().getSymbol()),
                "map type requires a value type specification after ','"
        );
    }

    @Override
    public void onMapTypeMissingClosure(ChimeraAntlrParser.MapTypeMissingClosureContext ctx) {
        diagnosticCollector.reportError(
                ctx.value == null ? posHelper.end(ctx.Less().getSymbol()) : posHelper.end(ctx.value),
                "expected '>' to close map type"
        );
    }

    @Override
    public void onParenTypeMissingClosure(ChimeraAntlrParser.ParenTypeMissingClosureContext ctx) {
        diagnosticCollector.reportError(
                posHelper.end(ctx.type()),
                "expected ')' to close parenthesized type"
        );
    }
}
