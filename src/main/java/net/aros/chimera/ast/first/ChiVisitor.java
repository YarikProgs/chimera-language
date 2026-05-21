package net.aros.chimera.ast.first;

public interface ChiVisitor<T> {
    T visitProgram(Program program);
    T visitExprStmt(Stmt.ExprStmt stmt);
    T visitIfStmt(Stmt.IfStmt stmt);
    T visitBlockStmt(Stmt.BlockStmt stmt);
    T visitWhileStmt(Stmt.WhileStmt stmt);
    T visitDoWhileStmt(Stmt.DoWhileStmt stmt);
    T visitForStmt(Stmt.ForStmt stmt);
    T visitReturnStmt(Stmt.ReturnStmt stmt);
    T visitLambdaExpr(Expr.LambdaExpr expr);
    T visitAssignExpr(Expr.AssignExpr expr);
    T visitLiteralExpr(Expr.LiteralExpr expr);
    T visitBinaryExpr(Expr.BinaryExpr expr);
    T visitUnaryExpr(Expr.UnaryExpr expr);
    T visitCallExpr(Expr.CallExpr expr);
    T visitMemberAccessExpr(Expr.MemberAccessExpr expr);
    T visitVarExpr(Expr.VarExpr expr);
    T visitParameterExpr(Expr.ParameterExpr expr);
    T visitArgumentExpr(Expr.ArgumentExpr expr);
    T visitTernaryExpr(Expr.TernaryExpr expr);
    T visitNullCoalesceExpr(Expr.NullCoalesceExpr expr);
    T visitShortTryExpr(Expr.ShortTryExpr expr);
    T visitUnwrapExpr(Expr.UnwrapExpr expr);
    T visitAnnotationExpr(Expr.AnnotationExpr expr);
    T visitIdentifierType(Expr.TypeExpr.IdentifierType expr);
    T visitUnionTypeExpr(Expr.TypeExpr.UnionTypeExpr expr);
    T visitIntersectionTypeExpr(Expr.TypeExpr.IntersectionTypeExpr expr);
    T visitNullableTypeExpr(Expr.TypeExpr.NullableTypeExpr expr);
    T visitTupleTypeExpr(Expr.TypeExpr.TupleTypeExpr expr);
    T visitListTypeExpr(Expr.TypeExpr.ListTypeExpr expr);
    T visitMapTypeExpr(Expr.TypeExpr.MapTypeExpr expr);
    T visitFunctionType(Expr.TypeExpr.FunctionType expr);

    default T visit(Node node) {
        return switch (node) {
            case Expr.LambdaExpr expr -> visitLambdaExpr(expr);
            case Expr.AssignExpr expr -> visitAssignExpr(expr);
            case Expr.LiteralExpr expr -> visitLiteralExpr(expr);
            case Expr.BinaryExpr expr -> visitBinaryExpr(expr);
            case Expr.UnaryExpr expr -> visitUnaryExpr(expr);
            case Expr.CallExpr expr -> visitCallExpr(expr);
            case Expr.MemberAccessExpr expr -> visitMemberAccessExpr(expr);
            case Expr.VarExpr expr -> visitVarExpr(expr);
            case Expr.ParameterExpr expr -> visitParameterExpr(expr);
            case Expr.ArgumentExpr expr -> visitArgumentExpr(expr);
            case Expr.TypeExpr.IdentifierType expr -> visitIdentifierType(expr);
            case Expr.TypeExpr.UnionTypeExpr expr -> visitUnionTypeExpr(expr);
            case Expr.TypeExpr.IntersectionTypeExpr expr -> visitIntersectionTypeExpr(expr);
            case Expr.TypeExpr.NullableTypeExpr expr -> visitNullableTypeExpr(expr);
            case Expr.TypeExpr.TupleTypeExpr expr -> visitTupleTypeExpr(expr);
            case Expr.TypeExpr.ListTypeExpr expr -> visitListTypeExpr(expr);
            case Expr.TypeExpr.MapTypeExpr expr -> visitMapTypeExpr(expr);
            case Expr.TypeExpr.FunctionType expr -> visitFunctionType(expr);
            case Expr.TernaryExpr expr -> visitTernaryExpr(expr);
            case Expr.NullCoalesceExpr expr -> visitNullCoalesceExpr(expr);
            case Expr.ShortTryExpr expr -> visitShortTryExpr(expr);
            case Expr.UnwrapExpr expr -> visitUnwrapExpr(expr);
            case Expr.AnnotationExpr expr -> visitAnnotationExpr(expr);
            case Stmt.ExprStmt stmt -> visitExprStmt(stmt);
            case Stmt.IfStmt stmt -> visitIfStmt(stmt);
            case Stmt.BlockStmt stmt -> visitBlockStmt(stmt);
            case Stmt.WhileStmt stmt -> visitWhileStmt(stmt);
            case Stmt.DoWhileStmt stmt -> visitDoWhileStmt(stmt);
            case Stmt.ForStmt stmt -> visitForStmt(stmt);
            case Stmt.ReturnStmt stmt -> visitReturnStmt(stmt);
            case Program program -> visitProgram(program);
        };
    }
}
