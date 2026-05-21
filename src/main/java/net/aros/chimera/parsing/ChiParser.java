package net.aros.chimera.parsing;

import net.aros.chimera.ast.first.Program;
import net.aros.chimera.errors.ChimeraErrorListener;
import net.aros.chimera.errors.diagnostic.ChimeraDiagnosticCollector;
import net.aros.chimera.errors.diagnostic.DiagnosticsRenderer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class ChiParser {
    public Program parse(SourceFile sourceFile) {
        ChimeraDiagnosticCollector diagnostics = new ChimeraDiagnosticCollector();
        ChimeraErrorListener errorListener = new ChimeraErrorListener(sourceFile, diagnostics);

        CharStream input = CharStreams.fromString(sourceFile.text());
        net.aros.chimera.ChimeraLexer lexer = new net.aros.chimera.ChimeraLexer(input);
        net.aros.chimera.ChimeraParser parser = new net.aros.chimera.ChimeraParser(new CommonTokenStream(lexer));

        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        parser.setErrorHandler(new net.aros.chimera.errors.ChimeraErrorStrategy());


        ParseTree tree = parser.program();

        if (diagnostics.hasErrors()) {
            diagnostics.diagnostics().forEach(DiagnosticsRenderer::render);
            System.exit(1);
        }

        Antlr2ChiVisitor visitor = new Antlr2ChiVisitor();
        return (Program) visitor.visit(tree);
    }
}
