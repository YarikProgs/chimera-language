package net.aros.chimera.errors.diagnostic;

import java.util.ArrayList;
import java.util.List;

public class ChimeraDiagnosticCollector {
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    public void report(Diagnostic diagnostic) {
        diagnostics.add(diagnostic);
    }

    public List<Diagnostic> diagnostics() {
        return diagnostics;
    }

    public boolean hasErrors() {
        return diagnostics.stream().anyMatch(d -> d.severity() == DiagnosticSeverity.ERROR);
    }
}
