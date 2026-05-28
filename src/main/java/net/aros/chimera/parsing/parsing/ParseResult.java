package net.aros.chimera.parsing.parsing;

import net.aros.chimera.ast.first.Program;
import net.aros.chimera.diagnostics.Diagnostic;
import net.aros.chimera.diagnostics.Severity;

import java.util.List;

public record ParseResult(Program result, List<Diagnostic> diagnostics) {
    public boolean hasErrors() {
        return diagnostics.stream().anyMatch(d -> d.severity() == Severity.ERROR);
    }
}
