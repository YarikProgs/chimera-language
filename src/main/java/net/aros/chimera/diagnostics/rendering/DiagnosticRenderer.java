package net.aros.chimera.diagnostics.rendering;

import net.aros.chimera.diagnostics.Diagnostic;
import net.aros.chimera.parsing.SourceFile;

import java.util.Collection;

public interface DiagnosticRenderer {
    String renderDiagnostics(SourceFile source, Collection<Diagnostic> diagnostics);
}
