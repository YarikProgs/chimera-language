package net.aros.chimera.parsing;

import net.aros.chimera.ChimeraAntlrLexer;
import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.ast.first.Program;
import net.aros.chimera.diagnostics.DiagnosticCollector;
import net.aros.chimera.diagnostics.reporting.DefaultDiagnosticReporter;
import net.aros.chimera.diagnostics.reporting.DiagnosticReporter;
import net.aros.chimera.diagnostics.util.TokenPositionHelper;
import net.aros.chimera.parsing.astbuilding.ChimeraAstBuilder;
import net.aros.chimera.parsing.parsing.ParseResult;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;

public class ChimeraParser {
    private final SourceFile sourceFile;
    private final DiagnosticReporter diagnosticReporter;
    private final DiagnosticCollector diagnosticCollector;

    public ChimeraParser(SourceFile sourceFile, @NotNull BiFunction<DiagnosticCollector, TokenPositionHelper, DiagnosticReporter> factory) {
        this.sourceFile = sourceFile;
        this.diagnosticCollector = new DiagnosticCollector(sourceFile);
        this.diagnosticReporter = Objects.requireNonNull(factory.apply(diagnosticCollector, new TokenPositionHelper()));
    }

    public ChimeraParser(SourceFile sourceFile) {
        this(sourceFile, DefaultDiagnosticReporter::new);
    }

    public ParseResult parse() {
        CharStream input = CharStreams.fromString(sourceFile.text());

        // 1. Lexing
        CommonTokenStream tokens = lexTokens(input);
        if (tokens == null)
            return new ParseResult(null, diagnosticCollector.getReportedDiagnostics());

        // 2. Parsing
        var tree = parseTokens(tokens);

        // 3. Building AST
        Program program = buildAst(tree);
        return new ParseResult(program, diagnosticCollector.getReportedDiagnostics());
    }

    private @Nullable CommonTokenStream lexTokens(CharStream stream) {
        ChimeraAntlrLexer lexer = new ChimeraAntlrLexer(stream);
        // TODO: Configure lexer
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
        commonTokenStream.fill();

        reportErrorTokens(commonTokenStream);
        if (diagnosticCollector.hasErrors()) return null;

        return commonTokenStream;
    }

    private @NotNull ChimeraAntlrParser.ProgramContext parseTokens(CommonTokenStream stream) {
        ChimeraAntlrParser parser = new ChimeraAntlrParser(stream);
        // TODO: Configure parser
        return parser.program();
    }

    private Program buildAst(ChimeraAntlrParser.ProgramContext tree) {
        var visitor = new ChimeraAstBuilder(diagnosticReporter);
        return visitor.build(tree);
    }

    private void reportErrorTokens(@NotNull CommonTokenStream stream) {
        for (Token token : stream.getTokens()) {
            switch (token.getType()) {
                case ChimeraAntlrLexer.ERROR_UNTERMINATED_STRING -> diagnosticReporter.onUnterminatedString(token);
                case ChimeraAntlrLexer.ERROR_INVALID_ESCAPE -> diagnosticReporter.onInvalidEscape(token);
                case ChimeraAntlrLexer.ERROR_UNTERMINATED_COMMENT -> diagnosticReporter.onUnterminatedComment(token);
                case ChimeraAntlrLexer.ERROR_UNMATCHED_COMMENT_CLOSURE ->
                        diagnosticReporter.onUnmatchedCommentClosure(token);
                case ChimeraAntlrLexer.ERROR_INVALID_NUMBER -> diagnosticReporter.onInvalidNumber(token);
                case ChimeraAntlrLexer.ERROR_INVALID_CHARACTER -> diagnosticReporter.onInvalidCharacter(token);
            }
        }
    }
}
