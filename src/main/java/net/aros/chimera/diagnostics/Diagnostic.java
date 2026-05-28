package net.aros.chimera.diagnostics;

import java.util.Collection;

public record Diagnostic(Severity severity, SourceSpan span, String message, Collection<String> hints) {
}