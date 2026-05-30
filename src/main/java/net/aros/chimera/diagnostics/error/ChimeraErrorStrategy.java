package net.aros.chimera.diagnostics.error;

import net.aros.chimera.diagnostics.reporting.DiagnosticReporter;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.IntervalSet;

public class ChimeraErrorStrategy extends DefaultErrorStrategy {
    private final DiagnosticReporter reporter;

    public ChimeraErrorStrategy(DiagnosticReporter reporter) {
        this.reporter = reporter;
    }

    @Override
    protected void reportMissingToken(Parser recognizer) {
        if (inErrorRecoveryMode(recognizer)) return;
        beginErrorCondition(recognizer);

        Token currentToken = recognizer.getCurrentToken();
        int expectedType = getExpectedTokens(recognizer).getMinElement();

        reporter.onMissingToken(recognizer.getVocabulary(), recognizer.getTokenStream().LT(-1), currentToken, expectedType);
    }

    @Override
    protected void reportUnwantedToken(Parser recognizer) {
        if (inErrorRecoveryMode(recognizer)) return;
        beginErrorCondition(recognizer);

        Token currentToken = recognizer.getCurrentToken();
        int expectedType = getExpectedTokens(recognizer).getMinElement();

        reporter.onUnwantedToken(recognizer.getVocabulary(), currentToken, expectedType);
    }

    @Override
    protected void reportFailedPredicate(Parser recognizer, FailedPredicateException e) {
        if (inErrorRecoveryMode(recognizer)) return;
        beginErrorCondition(recognizer);

        Token currentToken = recognizer.getCurrentToken();
        String ruleName = recognizer.getRuleNames()[recognizer.getContext().getRuleIndex()];

        reporter.onFailedPredicate(recognizer, currentToken, ruleName);
    }

    @Override
    protected void reportInputMismatch(Parser recognizer, InputMismatchException e) {
        if (inErrorRecoveryMode(recognizer)) return;
        beginErrorCondition(recognizer);

        Token offendingToken = e.getOffendingToken();
        IntervalSet expectedTokens = e.getExpectedTokens();

        reporter.onInputMismatch(recognizer.getVocabulary(), offendingToken, expectedTokens);
    }

    @Override
    protected void reportNoViableAlternative(Parser recognizer, NoViableAltException e) {
        if (inErrorRecoveryMode(recognizer)) return;
        beginErrorCondition(recognizer);

        Token offendingToken = e.getOffendingToken();
        reporter.onNoViableAlternative(recognizer, offendingToken);
    }
}
