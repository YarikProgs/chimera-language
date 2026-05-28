package net.aros.chimera.parsing.astbuilding;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.ChimeraAntlrParserBaseVisitor;
import net.aros.chimera.ast.first.Expr;
import net.aros.chimera.ast.first.Stmt;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.aros.chimera.parsing.astbuilding.AstUtils.id;
import static net.aros.chimera.parsing.astbuilding.AstUtils.pos;

public class ChimeraStmtBuilder extends ChimeraAntlrParserBaseVisitor<Stmt> {
    private final ChimeraAstBuilder parent;

    public ChimeraStmtBuilder(ChimeraAstBuilder parent) {
        this.parent = parent;
    }

    @Override
    public Stmt visitIfStmt(ChimeraAntlrParser.IfStmtContext ctx) {
        return new Stmt.IfStmt(annotations(ctx.annotations()), parent.getExprBuilder().visit(ctx.condition()), visit(ctx.stmt(0)), Optional.ofNullable(ctx.stmt(1)).map(this::visit), pos(ctx));
    }

    @Override
    public Stmt visitFnStmt(ChimeraAntlrParser.FnStmtContext ctx) {
        return super.visitFnStmt(ctx);
    }

    @Override
    public Stmt visitDoWhileStmt(ChimeraAntlrParser.DoWhileStmtContext ctx) {
        return new Stmt.DoWhileStmt(annotations(ctx.annotations()), (Stmt.BlockStmt) visit(ctx.blockStmt()), parent.getExprBuilder().visit(ctx.condition()), pos(ctx));
    }

    @Override
    public Stmt visitWhileStmt(ChimeraAntlrParser.WhileStmtContext ctx) {
        return new Stmt.WhileStmt(annotations(ctx.annotations()), parent.getExprBuilder().visit(ctx.condition()), (Stmt.BlockStmt) visit(ctx.blockStmt()), pos(ctx));
    }

    @Override
    public Stmt visitForStmt(ChimeraAntlrParser.ForStmtContext ctx) {
        List<Expr.VarExpr> variables = new ArrayList<>();
        for (TerminalNode identifier : ctx.forHeader().Identifier())
            variables.add(new Expr.VarExpr(id(identifier), pos(identifier.getSymbol())));
        return new Stmt.ForStmt(
                annotations(ctx.annotations()),
                variables,
                parent.getExprBuilder().visit(ctx.forHeader().expr()),
                visit(ctx.stmt()),
                pos(ctx)
        );
    }

    @Override
    public Stmt visitReturnStmt(ChimeraAntlrParser.ReturnStmtContext ctx) {
        return new Stmt.ReturnStmt(annotations(ctx.annotations()), Optional.ofNullable(ctx.expr()).map(parent.getExprBuilder()::visit), pos(ctx));
    }

    @Override
    public Stmt visitExprStmt(ChimeraAntlrParser.ExprStmtContext ctx) {
        return new Stmt.ExprStmt(annotations(ctx.annotations()), parent.getExprBuilder().visit(ctx.expr()), pos(ctx));
    }

    @Override
    public Stmt visitBlockStmt(ChimeraAntlrParser.BlockStmtContext ctx) {
        List<Stmt> stmts = new ArrayList<>();
        for (ChimeraAntlrParser.StmtContext sCtx : ctx.stmt()) stmts.add(visit(sCtx));
        return new Stmt.BlockStmt(stmts, pos(ctx));
    }

    private List<Expr.AnnotationExpr> annotations(ChimeraAntlrParser.AnnotationsContext ctx) {
        List<Expr.AnnotationExpr> annotations = new ArrayList<>();
        for (ChimeraAntlrParser.AnnotationContext aCtx : ctx.annotation())
            annotations.add((Expr.AnnotationExpr) parent.getExprBuilder().visit(aCtx));
        return annotations;
    }
}
