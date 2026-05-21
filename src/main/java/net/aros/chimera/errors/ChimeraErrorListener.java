package net.aros.chimera.errors;

import net.aros.chimera.ast.SourcePos;
import net.aros.chimera.errors.diagnostic.ChimeraDiagnosticCollector;
import net.aros.chimera.errors.diagnostic.Diagnostic;
import net.aros.chimera.errors.diagnostic.ErrorCode;
import net.aros.chimera.parsing.SourceFile;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;

public class ChimeraErrorListener implements ANTLRErrorListener {

    private final SourceFile sourceFile;
    private final ChimeraDiagnosticCollector diagnostics;
    private int maxLine = -1;
    private int maxColumn = -1;

    public ChimeraErrorListener(SourceFile sourceFile, ChimeraDiagnosticCollector diagnostics) {
        this.sourceFile = sourceFile;
        this.diagnostics = diagnostics;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object o, int line, int charPositionInLine, String msg, RecognitionException e) {
        if (line < maxLine || (line == maxLine)) {
            return;
        }
        maxLine = line;
        maxColumn = charPositionInLine;

        int reportLine = line;
        int reportColumn = charPositionInLine + 1;

        /*
            Попытка уточнить позицию для отсутствующих разделителей

            Прям точно указать позицию честно не получилось, но в текуйщей версии
            если пропустил запятую или точку с запятой он тебе укажет именно строку на которой
            это произошло
         */
        if (msg.contains("missing") || msg.contains("expected")) {
            if (recognizer instanceof Parser parser) {
                Token offending = (o instanceof Token) ? (Token) o : null;
                Token prev = null;
                if (offending != null) {
                    int index = offending.getTokenIndex();
                    if (index > 0) {
                        prev = parser.getInputStream().get(index - 1);
                    }
                }

                if (prev != null) {
                    reportLine = prev.getLine();
                    reportColumn = prev.getCharPositionInLine() + prev.getText().length() + 1;
                }
            }
        }

        ErrorCode code = ErrorCode.E0001; // По умолчанию
        if (msg.contains("comma") || msg.contains("','")) {
            code = ErrorCode.E0004;
        } else if (msg.contains("Semicolon") || msg.contains("';'")) {
            code = ErrorCode.E0004;
        } else if (msg.contains("mismatched input")) {
            code = ErrorCode.E0003;
        } else if (msg.contains("no viable alternative") || msg.contains("token recognition error")) {
            code = ErrorCode.E0002;
        }

        diagnostics.report(Diagnostic.error(
                code,
                msg,
                sourceFile,
                new SourcePos(reportLine, reportColumn)
        ));
    }

    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {

    }

    @Override
    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {

    }

    @Override
    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {

    }
}
