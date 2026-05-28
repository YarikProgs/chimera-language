package net.aros.chimera.diagnostics;

import net.aros.chimera.ast.SourcePos;

public record SourceSpan(SourcePos from, SourcePos to) {
    public SourceSpan(SourcePos single) {
        this(single, single);
    }
}
