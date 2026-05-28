package net.aros.chimera.diagnostics.util;

public final class RenderBuilder {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private final StringBuilder builder = new StringBuilder();

    public RenderBuilder append(Object obj) {
        builder.append(obj);
        return this;
    }

    public RenderBuilder repeat(Object obj, int times) {
        builder.repeat(String.valueOf(obj), times);
        return this;
    }

    public RenderBuilder red(Object obj) {
        return append(ANSI_RED).append(obj).append(ANSI_RESET);
    }

    public RenderBuilder blue(Object obj) {
        return append(ANSI_BLUE).append(obj).append(ANSI_RESET);
    }

    public RenderBuilder newline() {
        return append("\n");
    }

    public String build() {
        return builder.toString();
    }

    @Override
    public String toString() {
        return build();
    }
}
