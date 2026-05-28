package net.aros.chimera.parsing.astbuilding;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.ast.first.Program;
import net.aros.chimera.ast.first.Stmt;
import net.aros.chimera.diagnostics.reporting.DiagnosticReporter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChimeraAstBuilder {
    private final ChimeraTypeBuilder typeBuilder = new ChimeraTypeBuilder();
    private final ChimeraExprBuilder exprBuilder = new ChimeraExprBuilder(this);
    private final ChimeraStmtBuilder stmtBuilder = new ChimeraStmtBuilder(this);

    private final DiagnosticReporter reporter;

    public ChimeraAstBuilder(DiagnosticReporter reporter) {
        this.reporter = reporter;
    }

    public Program build(ChimeraAntlrParser.@NotNull ProgramContext ctx) {
        List<Stmt> stmts = new ArrayList<>();
        for (ChimeraAntlrParser.StmtContext sCtx : ctx.stmt()) {
            stmts.add(stmtBuilder.visit(sCtx));
        }
        return new Program(stmts);
    }

    public ChimeraTypeBuilder getTypeBuilder() {
        return typeBuilder;
    }

    public ChimeraExprBuilder getExprBuilder() {
        return exprBuilder;
    }

    public ChimeraStmtBuilder getStmtBuilder() {
        return stmtBuilder;
    }

    public DiagnosticReporter getReporter() {
        return reporter;
    }
}
