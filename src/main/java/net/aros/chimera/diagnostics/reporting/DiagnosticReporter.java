package net.aros.chimera.diagnostics.reporting;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.diagnostics.DiagnosticCollector;
import net.aros.chimera.diagnostics.util.TokenPositionHelper;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

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

//    public abstract void onNullCoalesceMissingRhs(ChimeraAntlrParser.NullCoalesceMissingRhsContext ctx);

    public abstract void onTernaryMissingThen(ChimeraAntlrParser.TernaryMissingThenContext ctx);

    public abstract void onTernaryMissingElse(ChimeraAntlrParser.TernaryMissingElseContext ctx);

    public abstract void onTernaryMissingThenAndElse(ChimeraAntlrParser.TernaryMissingThenAndElseContext ctx);
}
