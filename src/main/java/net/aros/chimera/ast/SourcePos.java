package net.aros.chimera.ast;

import org.jetbrains.annotations.NotNull;

public record SourcePos(int line, int column) {
    @Override
    public @NotNull String toString() {
        return line + ":" + column;
    }
}
