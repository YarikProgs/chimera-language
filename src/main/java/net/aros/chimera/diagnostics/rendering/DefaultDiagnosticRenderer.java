package net.aros.chimera.diagnostics.rendering;

import net.aros.chimera.diagnostics.Diagnostic;
import net.aros.chimera.diagnostics.SourceSpan;
import net.aros.chimera.diagnostics.util.RenderBuilder;
import net.aros.chimera.parsing.SourceFile;

import java.util.Collection;

public class DefaultDiagnosticRenderer implements DiagnosticRenderer {
    @Override
    public String renderDiagnostics(SourceFile source, Collection<Diagnostic> diagnostics) {
        RenderBuilder builder = new RenderBuilder();
        for (Diagnostic diagnostic : diagnostics) {
            renderDiagnostic(builder, source, diagnostic);
        }
        return builder.build();
    }

    private void renderDiagnostic(RenderBuilder builder, SourceFile source, Diagnostic diagnostic) {
        SourceSpan span = diagnostic.span();
        int lineStart = span.from().line();
        int lineEnd = span.to().line();

        renderHeader(builder, source, diagnostic);

        int maxLineNoWidth = Math.max(3, String.valueOf(lineEnd).length());
        String padding = " ".repeat(maxLineNoWidth);
        boolean isCollapsed = lineEnd - lineStart > 4;

        for (int line = lineStart; line <= lineEnd; line++) {

            if (shouldSkipLine(line, lineStart, lineEnd, isCollapsed)) {
                if (line == lineStart + 2) {
                    renderCollapseEllipsis(builder, padding);
                }
                continue;
            }

            String lineText = getLineTextOrEmpty(source, line);

            renderCodeLine(builder, line, maxLineNoWidth, getEdgeMarker(line, lineStart, lineEnd), lineText);

            // ^ / <--
            renderMarkersIfNeeded(builder, span, line, lineStart, lineEnd, padding, getEdgeMarker(line + 1, lineStart, lineEnd), lineText.length());
        }
    }

    private void renderHeader(RenderBuilder builder, SourceFile source, Diagnostic diagnostic) {
        builder.red("error").append(": ").append(diagnostic.message()).newline();
        builder.blue(" --> ").append(source.name()).append(":").append(diagnostic.span().from()).newline();
    }

    private boolean shouldSkipLine(int line, int lineStart, int lineEnd, boolean isCollapsed) {
        return isCollapsed && line > lineStart + 1 && line < lineEnd - 1;
    }

    private void renderCollapseEllipsis(RenderBuilder builder, String padding) {
        String dotPadding = " ".repeat(Math.max(0, padding.length() - 3));
        builder.append(dotPadding).blue("...").blue(" | ").red("|").newline();
    }

    private String getLineTextOrEmpty(SourceFile source, int line) {
        return line - 1 < source.lines().size() ? source.lines().get(line - 1) : "";
    }

    private String getEdgeMarker(int line, int lineStart, int lineEnd) {
        if (lineStart == lineEnd) return " ";
        if (line == lineStart)     return "/";
        if (line == lineEnd+1)       return "\\";
        return "|";
    }

    private void renderCodeLine(RenderBuilder builder, int line, int maxLineNoWidth, String edgeMarker, String lineText) {
        String lineNoStr = String.valueOf(line);
        String lineNoPadding = " ".repeat(maxLineNoWidth - lineNoStr.length());

        builder.append(lineNoPadding)
                .blue(lineNoStr)
                .blue(" | ")
                .red(edgeMarker)
                .append(" ")
                .append(lineText)
                .newline();
    }

    private void renderMarkersIfNeeded(RenderBuilder builder, SourceSpan span, int line, int lineStart, int lineEnd,
                                       String padding, String edgeMarker, int lineLength) {
        if (line == lineStart) {
            String selection = buildSelection(span, line, lineLength);
            if (!selection.isBlank()) {
                builder.append(padding).blue(" | ").red(edgeMarker).append(" ").red(selection).newline();
            }
        } else if (line == lineEnd) {
            int endCol = span.to().column();
            String arrow = "<" + "-".repeat(Math.max(0, endCol - 2));
            builder.append(padding).blue(" | ").red(edgeMarker).red(arrow).newline();
        }
    }

    private String buildSelection(SourceSpan span, int line, int lineLength) {
        int startLine = span.from().line();
        int endLine = span.to().line();

        int startOffset = (line == startLine) ? span.from().column() - 1 : 0;
        int endOffset = (line == endLine) ? span.to().column() - 1 : lineLength;

        if (line == endLine && endOffset <= startOffset) {
            endOffset = startOffset + 1;
        }

        startOffset = Math.clamp(startOffset, 0, lineLength);
        endOffset = Math.clamp(endOffset, startOffset, lineLength);

        return " ".repeat(startOffset) + "^".repeat(endOffset - startOffset);
    }
}
