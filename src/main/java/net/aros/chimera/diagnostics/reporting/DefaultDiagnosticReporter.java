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

//    @Override
//    public void onNullCoalesceMissingRhs(ChimeraAntlrParser.NullCoalesceMissingRhsContext ctx) {
//        diagnosticCollector.reportError(
//                posHelper.end(ctx),
//                "null coalesce requires a right-hand operand"
//        );
//    }

//    @Override
//    public void onTernaryMissingThen(ChimeraAntlrParser.@NotNull TernaryMissingThenContext ctx) {
//        diagnosticCollector.reportError(
//                posHelper.end(ctx.QuestionMark().getSymbol()),
//                "ternary requires 'then' expression"
//        );
//    }
//
//    @Override
//    public void onTernaryMissingElse(ChimeraAntlrParser.@NotNull TernaryMissingElseContext ctx) {
//        diagnosticCollector.reportError(
//                ctx.Colon() != null ? posHelper.end(ctx.Colon().getSymbol()) : posHelper.end(ctx.expr()),
//                "ternary requires 'else' expression"
//        );
//    }
//
//    @Override
//    public void onTernaryMissingThenAndElse(ChimeraAntlrParser.TernaryMissingThenAndElseContext ctx) {
//        diagnosticCollector.reportError(
//                posHelper.all(ctx),
//                "ternary requires 'then' and 'else' expressions"
//        );
//    }
}
