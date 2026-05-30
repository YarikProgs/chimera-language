package net.aros.chimera.diagnostics.reporting;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.diagnostics.DiagnosticCollector;
import net.aros.chimera.diagnostics.util.TokenPositionHelper;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.jetbrains.annotations.NotNull;

import static net.aros.chimera.ChimeraAntlrLexer.*;

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
    public void onIllegalFunctionStmtBody(ChimeraAntlrParser.IllegalStmtBodyContext ctx) {
        diagnosticCollector.reportError(
                posHelper.start(ctx.stmt()),
                "statement cannot go after ':'. replace it with expression or block"
        );
    }

    @Override
    public void onLambdaMissingParameters(ChimeraAntlrParser.LambdaExprMissingParametersContext ctx) {
        diagnosticCollector.reportError(
                posHelper.start(ctx.functionBody()),
                "lambda misses parameters"
        );
    }

    @Override
    public void onLambdaMissingReturnType(ChimeraAntlrParser.LambdaExprMissingReturnTypeContext ctx) {
        diagnosticCollector.reportError(
                posHelper.end(ctx.RArrow().getSymbol()),
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

    @Override
    public void onMissingToken(Vocabulary vocabulary, Token previousToken, Token token, int expectedType) {
        String message = switch (expectedType) {
            // TODO: more entries on noticing
            case Comma, Semicolon, Dot, Colon, PlusAssign, MinusAssign, MultiplyAssign, DivideAssign, ModuloAssign,
                 BitAndAssign, BitOrAssign, BitXorAssign, ShiftLeftAssign, ShiftRightAssign, ShiftRightUnsignedAssign,
                 LogicAndAssign, LogicOrAssign, LogicXorAssign, Assign, RArrow, RBrace, RBracket, RParen ->
                    "Missing " + getReadableTokenName(vocabulary, expectedType) + " after '" + previousToken.getText() + "'";
            default ->
                    "Missing " + getReadableTokenName(vocabulary, expectedType) + " before '" + token.getText() + "'";
        };

        diagnosticCollector.reportError(
                posHelper.start(token),
                message
        );
    }

    @Override
    public void onUnwantedToken(Vocabulary vocabulary, Token currentToken, int expectedType) {
        String tokenName = getReadableTokenName(vocabulary, currentToken.getType());
        String message;
        if (expectedType != Token.INVALID_TYPE) {
            message = "Unexpected " + tokenName + " '" + currentToken.getText() + "'. Expected " + getReadableTokenName(vocabulary, expectedType) + " instead";
        } else {
            message = "Extraneous " + tokenName + " '" + currentToken.getText() + "' needs to be removed";
        }

        diagnosticCollector.reportError(
                posHelper.all(currentToken),
                message
        );
    }

    @Override
    public void onInputMismatch(Vocabulary vocabulary, Token offendingToken, IntervalSet expectedTokens) {
        diagnosticCollector.reportError(
                posHelper.all(offendingToken),
                "Mismatched syntax. Found " + getReadableTokenName(vocabulary, offendingToken.getType()) +
                        " '" + offendingToken.getText() + "', but expected " + buildSeq(vocabulary, expectedTokens)
        );
    }

    @Override
    public void onFailedPredicate(Parser recognizer, Token currentToken, String ruleName) {
        diagnosticCollector.reportError(
                posHelper.all(currentToken),
                "Semantic validation failed in rule '" + ruleName + "' near token '" + currentToken.getText() + "'"
        );
    }

    @Override
    public void onNoViableAlternative(Parser recognizer, Token offendingToken) {
        String message;

        if (offendingToken.getType() == Token.EOF) {
            message = "Unexpected end of file. The expression or block is incomplete";
        } else {
            String tokenName = getReadableTokenName(recognizer.getVocabulary(), offendingToken.getType());
            message = "Unrecognized syntax near " + tokenName + " '" + offendingToken.getText() + "'";
        }

        diagnosticCollector.reportError(posHelper.all(offendingToken), message);
    }

    private static @NotNull String buildSeq(Vocabulary vocabulary, @NotNull IntervalSet expectedTokens) {
        if (expectedTokens.size() == 0) return "nothing";

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < expectedTokens.size() - 1; i++) {
            builder.append(getReadableTokenName(vocabulary, expectedTokens.get(i))).append(", ");
        }
        if (expectedTokens.size() > 1)
            builder.append("or ");
        builder.append(getReadableTokenName(vocabulary, expectedTokens.get(expectedTokens.size() - 1)));
        return builder.toString();
    }

    private static @NotNull String getReadableTokenName(Vocabulary vocabulary, int tokenType) {
        if (tokenType == Token.EOF) return "end of file";

        String literalName = vocabulary.getLiteralName(tokenType);
        if (literalName != null) return literalName.replaceAll("^'|'$", "");

        String symbolicName = vocabulary.getSymbolicName(tokenType);
        if (symbolicName != null) return symbolicName;

        return "token";
    }
}
