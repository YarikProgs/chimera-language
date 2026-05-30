package net.aros.chimera;

import net.aros.chimera.diagnostics.rendering.DefaultDiagnosticRenderer;
import net.aros.chimera.parsing.ChimeraParser;
import net.aros.chimera.parsing.SourceFile;
import net.aros.chimera.parsing.parsing.ParseResult;
import net.aros.chimera.parsing.test.ChimeraPseudocodeBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class ChimeraMain {
    public static void main(String[] args) throws URISyntaxException, IOException {
        SourceFile sourceFile = SourceFile.from(Path.of(ChimeraMain.class.getResource("/syntax.chi").toURI()));
        ParseResult result = new ChimeraParser(sourceFile).parse();

        String rendered = new DefaultDiagnosticRenderer().renderDiagnostics(sourceFile, result.diagnostics());

        System.out.println(rendered);
        if (!result.hasErrors()) {
            System.out.println(result.result());

            System.out.println(new ChimeraPseudocodeBuilder().visit(result.result()));
        }
    }
}
