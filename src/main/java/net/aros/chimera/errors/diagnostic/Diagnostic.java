package net.aros.chimera.errors.diagnostic;

import net.aros.chimera.ast.SourcePos;
import net.aros.chimera.parsing.SourceFile;

public record Diagnostic(
        DiagnosticSeverity severity,
        ErrorCode code,
        String message,
        SourceFile sourceFile,
        SourcePos pos,
        String help
) {
    public static Diagnostic error(ErrorCode code, String message, SourceFile sourceFile, SourcePos pos) {
        return new Diagnostic(DiagnosticSeverity.ERROR, code, message, sourceFile, pos, null);
    }

    public static Diagnostic error(ErrorCode code, String message, SourceFile sourceFile, SourcePos pos, String help) {
        return new Diagnostic(DiagnosticSeverity.ERROR, code, message, sourceFile, pos, help);
    }
}
