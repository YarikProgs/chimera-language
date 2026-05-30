package net.aros.chimera.parsing.astbuilding;

import net.aros.chimera.ChimeraAntlrLexer;
import net.aros.chimera.ChimeraAntlrParser;
import net.aros.chimera.ast.Modifier;
import net.aros.chimera.ast.SourcePos;
import net.aros.chimera.ast.first.Expr;
import net.aros.chimera.ast.first.Stmt;
import net.aros.chimera.ast.ops.BinaryOp;
import net.aros.chimera.ast.ops.UnaryOp;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;
import java.util.List;

public class AstUtils {
    public static Modifier modifier(ChimeraAntlrParser.@NotNull ModifierContext mod) {
        if (mod.At() != null) return Modifier.STATIC;
        if (mod.Const() != null) return Modifier.CONST;
        throw new AstBuildException("Unknown modifier: " + mod.getText());
    }

    public static BinaryOp getBinaryOperator(@NotNull Token token) {
        return switch (token.getType()) {
            case ChimeraAntlrLexer.BitOr -> BinaryOp.BITWISE_OR;
            case ChimeraAntlrLexer.BitAnd -> BinaryOp.BITWISE_AND;
            case ChimeraAntlrLexer.BitXor -> BinaryOp.BITWISE_XOR;
            case ChimeraAntlrLexer.LogicAnd -> BinaryOp.LOGICAL_AND;
            case ChimeraAntlrLexer.LogicOr -> BinaryOp.LOGICAL_OR;
            case ChimeraAntlrLexer.LogicXor -> BinaryOp.LOGICAL_XOR;
            case ChimeraAntlrLexer.ShiftLeft -> BinaryOp.SHIFT_LEFT;
            case ChimeraAntlrLexer.ShiftRight -> BinaryOp.SHIFT_RIGHT;
            case ChimeraAntlrLexer.ShiftRightUnsigned -> BinaryOp.SHIFT_RIGHT_UNSIGNED;
            case ChimeraAntlrLexer.Multiply -> BinaryOp.MULTIPLY;
            case ChimeraAntlrLexer.Divide -> BinaryOp.DIVIDE;
            case ChimeraAntlrLexer.Modulo -> BinaryOp.MODULO;
            case ChimeraAntlrLexer.Plus -> BinaryOp.PLUS;
            case ChimeraAntlrLexer.Minus -> BinaryOp.MINUS;
            case ChimeraAntlrLexer.Less -> BinaryOp.LESS;
            case ChimeraAntlrLexer.LessEqual -> BinaryOp.LESS_EQUAL;
            case ChimeraAntlrLexer.Greater -> BinaryOp.GREATER;
            case ChimeraAntlrLexer.GreaterEqual -> BinaryOp.GREATER_EQUAL;
            case ChimeraAntlrLexer.Equals -> BinaryOp.EQUALS;
            case ChimeraAntlrLexer.AddressEquals -> BinaryOp.ADDRESS_EQUALS;
            case ChimeraAntlrLexer.NotEquals -> BinaryOp.NOT_EQUALS;
            case ChimeraAntlrLexer.AddressNotEquals -> BinaryOp.ADDRESS_NOT_EQUALS;
            default -> throw new AstBuildException("Unknown binary operator: " + token.getText());
        };
    }

    public static @Nullable BinaryOp getBinaryOperatorFromAssignment(@NotNull Token token) {
        return switch (token.getType()) {
            case ChimeraAntlrLexer.PlusAssign -> BinaryOp.PLUS;
            case ChimeraAntlrLexer.MinusAssign -> BinaryOp.MINUS;
            case ChimeraAntlrLexer.MultiplyAssign -> BinaryOp.MULTIPLY;
            case ChimeraAntlrLexer.DivideAssign -> BinaryOp.DIVIDE;
            case ChimeraAntlrLexer.ModuloAssign -> BinaryOp.MODULO;
            case ChimeraAntlrLexer.BitAndAssign -> BinaryOp.BITWISE_AND;
            case ChimeraAntlrLexer.BitOrAssign -> BinaryOp.BITWISE_OR;
            case ChimeraAntlrLexer.BitXorAssign -> BinaryOp.BITWISE_XOR;
            case ChimeraAntlrLexer.ShiftLeftAssign -> BinaryOp.SHIFT_LEFT;
            case ChimeraAntlrLexer.ShiftRightAssign -> BinaryOp.SHIFT_RIGHT;
            case ChimeraAntlrLexer.ShiftRightUnsignedAssign -> BinaryOp.SHIFT_RIGHT_UNSIGNED;
            case ChimeraAntlrLexer.LogicAndAssign -> BinaryOp.LOGICAL_AND;
            case ChimeraAntlrLexer.LogicOrAssign -> BinaryOp.LOGICAL_OR;
            case ChimeraAntlrLexer.LogicXorAssign -> BinaryOp.LOGICAL_XOR;
            case ChimeraAntlrLexer.Assign -> null;
            default -> throw new AstBuildException("Unknown binary+assignment operator: " + token.getText());
        };
    }

    public static UnaryOp getUnaryOperator(@NotNull Token token) {
        return switch (token.getType()) {
            case ChimeraAntlrLexer.ExclamationMark -> UnaryOp.LOGIC_NOT;
            case ChimeraAntlrLexer.BitNot -> UnaryOp.BIT_NOT;
            case ChimeraAntlrLexer.Minus -> UnaryOp.MINUS;
            case ChimeraAntlrLexer.Plus -> UnaryOp.PLUS;
            default -> throw new AstBuildException("Unknown unary operator: " + token.getText());
        };
    }

    @Contract("_ -> new")
    public static @NotNull SourcePos pos(@NotNull ParserRuleContext tree) {
        return pos(tree.getStart());
    }

    @Contract("_ -> new")
    public static @NotNull SourcePos pos(@NotNull Token token) {
        return new SourcePos(token.getLine(), token.getCharPositionInLine() + 1);
    }

    public static String id(@NotNull TerminalNode node) {
        return Normalizer.normalize(node.getText(), Normalizer.Form.NFC);
    }

    @Contract("_ -> new")
    public static Stmt.@NotNull ExprStmt syntheticExprStatement(Expr expr) {
        return new Stmt.ExprStmt(List.of(), expr, expr.pos());
    }
}
