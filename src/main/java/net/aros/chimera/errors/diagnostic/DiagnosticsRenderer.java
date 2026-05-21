package net.aros.chimera.errors.diagnostic;

public class DiagnosticsRenderer {
    public static void render(Diagnostic diagnostic) {
        String severityColor = switch (diagnostic.severity()) {
            case ERROR -> "\u001B[31m";
            case WARNING -> "\u001B[33m";
            case HINT -> "\u001B[36m";
        };
        String resetColor = "\u001B[0m";
        String bold = "\u001B[1m";

        System.err.printf("%s%s%s[%s]: %s%s%n", bold, severityColor, diagnostic.severity(), diagnostic.code(), resetColor, diagnostic.message());
        System.err.printf("  --> %s:%d:%d%n", diagnostic.sourceFile().name(), diagnostic.pos().line(), diagnostic.pos().column());

        int lineNum = diagnostic.pos().line();
        if (lineNum > 0 && lineNum <= diagnostic.sourceFile().lines().size()) {
            String line = diagnostic.sourceFile().lines().get(lineNum - 1);
            System.err.printf("   |%n");
            System.err.printf("%-3d| %s%n", lineNum, line);
            System.err.print("   | ");
            for (int i = 0; i < diagnostic.pos().column() - 1; i++) {
                System.err.print(" ");
            }
            System.err.printf("%s^ %s%s%n", severityColor, diagnostic.message(), resetColor);
            System.err.printf("   |%n");
        }

        if (diagnostic.help() != null) {
            System.err.printf("   = help: %s%n", diagnostic.help());
        }

        System.err.printf("   = note: %s%n", diagnostic.code().getSolution());

        System.err.println();
    }

    public static void renderErrorTable() {
        System.err.println("Error Codes Reference Table:");
        System.err.println("--------------------------------------------------------------------------------");
        System.err.printf("| %-6s | %-20s | %-45s |%n", "Code", "Description", "Note (Solution)");
        System.err.println("--------------------------------------------------------------------------------");
        for (ErrorCode code : ErrorCode.values()) {
            System.err.printf("| %-6s | %-20s | %-45s |%n", code.name(), code.getDescription(), code.getSolution());
        }
        System.err.println("--------------------------------------------------------------------------------");
    }
}
