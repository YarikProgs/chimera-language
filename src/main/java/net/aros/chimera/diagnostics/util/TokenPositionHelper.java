package net.aros.chimera.diagnostics.util;

import net.aros.chimera.ast.SourcePos;
import net.aros.chimera.diagnostics.SourceSpan;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;

// TODO: 26.05.2026 migrate to static methods if source-dependent fields won't show up
public class TokenPositionHelper {
    public TokenPositionHelper() {
    }

    public SourceSpan start(@NotNull Token token) {
        return new SourceSpan(new SourcePos(token.getLine(), token.getCharPositionInLine() + 1));
    }

    public SourceSpan start(@NotNull ParserRuleContext ctx) {
        return start(ctx.getStart());
    }

    public SourceSpan all(@NotNull Token token) {
        return new SourceSpan(
                new SourcePos(token.getLine(), getColumn(token)),
                new SourcePos(token.getLine(), getColumn(token) + len(token))
        );
    }

    public SourceSpan all(@NotNull ParserRuleContext ctx) {
        return between(ctx.getStart(), ctx.getStop());
    }

    public SourceSpan between(@NotNull Token start, Token end) {
        return new SourceSpan(
                new SourcePos(start.getLine(), getColumn(start)),
                new SourcePos(end.getLine(), getColumn(end) + len(end))
        );
    }

    public SourceSpan end(@NotNull Token token) {
        return new SourceSpan(new SourcePos(token.getLine(), getColumn(token) + len(token) - 1));
    }

    public SourceSpan end(@NotNull ParserRuleContext ctx) {
        return end(ctx.getStop());
    }

    private static int getColumn(@NotNull Token token) {
        return token.getCharPositionInLine() + 1;
    }

    private static int len(Token token) {
        return token == null || token.getText() == null ? 0 : token.getText().length();
    }
}
