package net.aros.chimera.errors.diagnostic;

public enum ErrorCode {
    E0001("Syntax Error", "Check your syntax, maybe you missed a semicolon or have a typo."),
    E0002("Unexpected Token", "The parser found a token that doesn't belong here. Check the surrounding code."),
    E0003("Mismatched Input", "The input doesn't match the expected grammar rules. Ensure you are following the language specification."),
    E0004("Missing Separator", "It looks like you missed a comma or a semicolon. Check your literals or statements.");

    private final String description;
    private final String solution;

    ErrorCode(String description, String solution) {
        this.description = description;
        this.solution = solution;
    }

    public String getDescription() {
        return description;
    }

    public String getSolution() {
        return solution;
    }

    @Override
    public String toString() {
        return name();
    }
}
