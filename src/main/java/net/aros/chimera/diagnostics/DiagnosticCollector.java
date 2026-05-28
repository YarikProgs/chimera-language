package net.aros.chimera.diagnostics;

import net.aros.chimera.parsing.SourceFile;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticCollector {
    private final List<Diagnostic> diagnostics = new ArrayList<>();
    private final SourceFile source;

    public DiagnosticCollector(SourceFile source) {
        this.source = source;
    }

    public void report(Diagnostic diagnostic) {
        diagnostics.add(diagnostic);
    }

    public void reportError(SourceSpan span, String message, String... hints) {
        report(new Diagnostic(Severity.ERROR, span, message, List.of(hints)));
    }

    public void reportWarning(SourceSpan span, String message, String... hints) {
        report(new Diagnostic(Severity.WARN, span, message, List.of(hints)));
    }

    public List<Diagnostic> getReportedDiagnostics() {
        return diagnostics;
    }

    public SourceFile getSource() {
        return source;
    }

    public boolean hasErrors() {
        return hasAny() && diagnostics.stream().anyMatch(d -> d.severity() == Severity.ERROR);
    }

    public boolean hasAny() {
        return !diagnostics.isEmpty();
    }

    public void clearReports() {
        diagnostics.clear();
    }
}
