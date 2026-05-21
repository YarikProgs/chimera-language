package net.aros.chimera.errors;

import net.aros.chimera.ChimeraParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.IntervalSet;

public class ChimeraErrorStrategy extends DefaultErrorStrategy {

    private int lastErrorIndex = -1;

    @Override
    public void reportError(Parser recognizer, RecognitionException e) {
        if (inErrorRecoveryMode(recognizer)) {
            return;
        }

        int currentIndex = recognizer.getInputStream().index();
        if (currentIndex < lastErrorIndex) {
            return;
        }
        lastErrorIndex = currentIndex;

        beginErrorCondition(recognizer);

        if (e instanceof NoViableAltException) {
            reportNoViableAlternative(recognizer, (NoViableAltException) e);
        } else if (e instanceof InputMismatchException) {
            reportInputMismatch(recognizer, (InputMismatchException) e);
        } else {
            super.reportError(recognizer, e);
        }
    }

    @Override
    protected void reportNoViableAlternative(Parser recognizer, NoViableAltException e) {
        Token startToken = e.getStartToken();
        Token t = e.getOffendingToken();

        if (startToken != null) {
            if (startToken.getType() == ChimeraParser.LBrace || startToken.getType() == ChimeraParser.LBracket) {
                boolean isMap = startToken.getType() == ChimeraParser.LBrace;
                String msg = "expected " + (isMap ? "',' or '}'" : "',' or ']'") + " here";
                recognizer.notifyErrorListeners(t, msg, e);
                // Пропускаем до точки с запятой или конца блока
                IntervalSet set = new IntervalSet();
                set.add(ChimeraParser.Semicolon);
                set.add(ChimeraParser.RBrace);
                set.add(ChimeraParser.EOF);
                consumeUntil(recognizer, set);
                return;
            }
        }

        if (isInside(recognizer, ChimeraParser.RULE_mapLiteral)) {
            if (t.getType() != ChimeraParser.RBrace && t.getType() != ChimeraParser.Comma && t.getType() != ChimeraParser.Colon) {
                String msg = "expected ',' or '}' here";
                recognizer.notifyErrorListeners(t, msg, e);
                return;
            }
        } else if (isInside(recognizer, ChimeraParser.RULE_listLiteral)) {
            if (t.getType() != ChimeraParser.RBracket && t.getType() != ChimeraParser.Comma) {
                String msg = "expected ',' or ']' here";
                recognizer.notifyErrorListeners(t, msg, e);
                return;
            }
        }
        super.reportNoViableAlternative(recognizer, e);
    }

    @Override
    protected void reportInputMismatch(Parser recognizer, InputMismatchException e) {
        Token t = e.getOffendingToken();
        if (isInside(recognizer, ChimeraParser.RULE_mapLiteral)) {
            if (e.getExpectedTokens().contains(ChimeraParser.Comma)) {
                String msg = "expected ',' here";
                recognizer.notifyErrorListeners(t, msg, e);
                return;
            }
        }
        super.reportInputMismatch(recognizer, e);
    }

    private boolean isInside(Parser recognizer, int ruleIndex) {
        RuleContext ctx = recognizer.getContext();
        while (ctx != null) {
            if (ctx.getRuleIndex() == ruleIndex) {
                return true;
            }
            ctx = ctx.parent;
        }
        return false;
    }
}
