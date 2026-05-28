package net.aros.chimera.parsing.astbuilding.builders;

import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.ChimeraAntlrParserBaseVisitor;
import net.aros.chimera.ast.first.Expr;
import net.aros.chimera.ast.first.Stmt;
import net.aros.chimera.parsing.astbuilding.ChimeraAstBuilder;
import org.antlr.v4.runtime.tree.ParseTree;
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
        return new Stmt.IfStmt(parent.getElementBuilder().buildAnnotations(ctx.annotations()), visitExpr(ctx.expr()), visit(ctx.stmt(0)), Optional.ofNullable(ctx.stmt(1)).map(this::visit), pos(ctx));
    }

    @Override
    public Stmt visitFnStmt(ChimeraAntlrParser.FnStmtContext ctx) {
        return super.visitFnStmt(ctx);
    }

    @Override
    public Stmt visitDoWhileStmt(ChimeraAntlrParser.DoWhileStmtContext ctx) {
        return new Stmt.DoWhileStmt(parent.getElementBuilder().buildAnnotations(ctx.annotations()), (Stmt.BlockStmt) visit(ctx.blockStmt()), visitExpr(ctx.expr()), pos(ctx));
    }

    @Override
    public Stmt visitWhileStmt(ChimeraAntlrParser.WhileStmtContext ctx) {
        return new Stmt.WhileStmt(parent.getElementBuilder().buildAnnotations(ctx.annotations()), visitExpr(ctx.expr()), (Stmt.BlockStmt) visit(ctx.blockStmt()), pos(ctx));
    }

    @Override
    public Stmt visitForStmt(ChimeraAntlrParser.ForStmtContext ctx) {
        List<Expr.VarExpr> variables = new ArrayList<>();
        for (TerminalNode identifier : ctx.forHeader().Identifier())
            variables.add(new Expr.VarExpr(id(identifier), pos(identifier.getSymbol())));
        return new Stmt.ForStmt(
                parent.getElementBuilder().buildAnnotations(ctx.annotations()),
                variables,
                visitExpr(ctx.forHeader().expr()),
                visit(ctx.stmt()),
                pos(ctx)
        );
    }

    @Override
    public Stmt visitReturnStmt(ChimeraAntlrParser.ReturnStmtContext ctx) {
        return new Stmt.ReturnStmt(parent.getElementBuilder().buildAnnotations(ctx.annotations()), Optional.ofNullable(ctx.expr()).map(parent.getExprBuilder()::visit), pos(ctx));
    }

    @Override
    public Stmt visitExprStmt(ChimeraAntlrParser.ExprStmtContext ctx) {
        return new Stmt.ExprStmt(parent.getElementBuilder().buildAnnotations(ctx.annotations()), visitExpr(ctx.expr()), pos(ctx));
    }

    @Override
    public Stmt visitBlockStmt(ChimeraAntlrParser.BlockStmtContext ctx) {
        List<Stmt> stmts = new ArrayList<>();
        for (ChimeraAntlrParser.StmtContext sCtx : ctx.stmt()) stmts.add(visit(sCtx));
        return new Stmt.BlockStmt(stmts, pos(ctx));
    }

    private Expr visitExpr(ParseTree tree) {
        return parent.getExprBuilder().visit(tree);
    }
}
