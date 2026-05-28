package net.aros.chimera.ast.first;

public interface ChimeraVisitor<T> {
    T visitProgram(Program program);
    
    T visitExprStmt(Stmt.ExprStmt stmt);
    T visitIfStmt(Stmt.IfStmt stmt);
    T visitBlockStmt(Stmt.BlockStmt stmt);
    T visitWhileStmt(Stmt.WhileStmt stmt);
    T visitDoWhileStmt(Stmt.DoWhileStmt stmt);
    T visitForStmt(Stmt.ForStmt stmt);
    T visitReturnStmt(Stmt.ReturnStmt stmt);
    T visitErrorStmt(Stmt.ErrorStmt stmt);
    
    T visitLambdaExpr(Expr.LambdaExpr expr);
    T visitAssignExpr(Expr.AssignExpr expr);
    T visitLiteralExpr(Expr.LiteralExpr expr);
    T visitBinaryExpr(Expr.BinaryExpr expr);
    T visitUnaryExpr(Expr.UnaryExpr expr);
    T visitCallExpr(Expr.CallExpr expr);
    T visitMemberAccessExpr(Expr.MemberAccessExpr expr);
    T visitVarExpr(Expr.VarExpr expr);
    T visitTernaryExpr(Expr.TernaryExpr expr);
    T visitNullCoalesceExpr(Expr.NullCoalesceExpr expr);
    T visitShortTryExpr(Expr.ShortTryExpr expr);
    T visitUnwrapExpr(Expr.UnwrapExpr expr);
    T visitModifiedExpr(Expr.ModifiedExpr expr);
    T visitErrorExpr(Expr.ErrorExpr expr);
    
    T visitParameter(Node.Parameter node);
    T visitArgument(Node.Argument node);

    T visitAnnotation(Node.Annotation node);
    T visitIdentifierType(Type.IdentifierType type);
    T visitUnionType(Type.UnionType type);
    T visitIntersectionType(Type.IntersectionType type);
    T visitNullableType(Type.NullableType type);
    T visitTupleType(Type.TupleType type);
    T visitListType(Type.ListType type);
    T visitMapType(Type.MapType type);
    T visitFunctionType(Type.FunctionType type);
    T visitErrorType(Type.ErrorType type);

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
            case Expr.TernaryExpr expr -> visitTernaryExpr(expr);
            case Expr.NullCoalesceExpr expr -> visitNullCoalesceExpr(expr);
            case Expr.ShortTryExpr expr -> visitShortTryExpr(expr);
            case Expr.UnwrapExpr expr -> visitUnwrapExpr(expr);
            case Expr.ModifiedExpr expr -> visitModifiedExpr(expr);
            case Expr.ErrorExpr expr -> visitErrorExpr(expr);

            case Type.IdentifierType type -> visitIdentifierType(type);
            case Type.UnionType type -> visitUnionType(type);
            case Type.IntersectionType type -> visitIntersectionType(type);
            case Type.NullableType type -> visitNullableType(type);
            case Type.TupleType type -> visitTupleType(type);
            case Type.ListType type -> visitListType(type);
            case Type.MapType type -> visitMapType(type);
            case Type.FunctionType type -> visitFunctionType(type);
            case Type.ErrorType type -> visitErrorType(type);

            case Stmt.ExprStmt stmt -> visitExprStmt(stmt);
            case Stmt.IfStmt stmt -> visitIfStmt(stmt);
            case Stmt.BlockStmt stmt -> visitBlockStmt(stmt);
            case Stmt.WhileStmt stmt -> visitWhileStmt(stmt);
            case Stmt.DoWhileStmt stmt -> visitDoWhileStmt(stmt);
            case Stmt.ForStmt stmt -> visitForStmt(stmt);
            case Stmt.ReturnStmt stmt -> visitReturnStmt(stmt);
            case Stmt.ErrorStmt stmt -> visitErrorStmt(stmt);

            case Node.Annotation annotation -> visitAnnotation(annotation);
            case Node.Parameter parameter -> visitParameter(parameter);
            case Node.Argument argument -> visitArgument(argument);


            case Program program -> visitProgram(program);
            case null -> null;
        };
    }
}
