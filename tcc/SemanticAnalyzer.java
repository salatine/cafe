package tcc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import tcc.exceptions.*;
import tcc.nodes.*;

// Analisador semântico
public class SemanticAnalyzer {

    private record Variable(DataType dataType, Optional<ExpressionValue> value) {}

    private record DoubleValue(Double value) implements ExpressionValue {

        @Override
        public DataType dataType() {
            return DataType.DOUBLE;
        }
    }
    private record IntegerValue(Integer value) implements ExpressionValue {

        @Override
        public DataType dataType() {
            return DataType.INT;
        }
    }
    private sealed interface ExpressionValue permits DoubleValue, IntegerValue {
        Number value();
        DataType dataType();
    }

    private final Map<String, Variable> symbolTable = new HashMap<>();

    public void analyze(ProgramNode programNode) throws SemanticAnalyzerException {
        for (StatementNode statement : programNode.statements()) {
            visitStatement(statement);
        }
    }

    private void visitStatement(StatementNode statement) throws SemanticAnalyzerException {
        switch (statement) {
            case DeclarationNode declarationNode -> visitDeclaration(declarationNode);
            case ExpressionNode expressionNode -> evaluateExpression(expressionNode);
            case PrintCallNode printCallNode -> evaluateExpression(printCallNode.parameter());
        }
    }

    private void visitDeclaration(DeclarationNode declarationNode) throws SemanticAnalyzerException {
        String varName = declarationNode.identifier().name();
        DataType type = declarationNode.type();
        Optional<ExpressionValue> varValue = Optional.empty();
        if (declarationNode.expression().isPresent()) {
            varValue = Optional.of(evaluateExpression(declarationNode.expression().get()));
        }

        if (symbolTable.containsKey(varName)) {
            throw new RedeclarationException(varName);
        }

        if (varValue.isPresent() && varValue.get().dataType() != type) {
            throw new TypeMismatchException(varValue.get().value().toString(), type);
        }

        symbolTable.put(varName, new Variable(type, varValue));
    }

    private ExpressionValue readIdentifier(IdentifierNode identifierNode) throws SemanticAnalyzerException {
        String varName = identifierNode.name();

        if (!symbolTable.containsKey(varName)) {
            throw new UndeclaredIdentifierException(varName);
        }

        Variable variable = symbolTable.get(varName);
        Optional<ExpressionValue> value = variable.value();
        return value.orElseThrow(() -> new UnitializedIdentifierException(varName));
    }

    private ExpressionValue evaluateExpression(ExpressionNode expression) throws SemanticAnalyzerException {
        return switch (expression) {
            case IntNode in -> new IntegerValue(in.value());
            case DoubleNode db -> new DoubleValue(db.value());
            case IdentifierNode id -> readIdentifier(id);
            case BinaryExpressionNode bin -> evaluateBinaryExpression(bin);
            case AssignmentNode assignmentNode -> evaluateAssignment(assignmentNode);
        };
    }

    private ExpressionValue evaluateAssignment(AssignmentNode assignmentNode) throws SemanticAnalyzerException {
        String varName = assignmentNode.identifier().name();
        if (!symbolTable.containsKey(varName)) {
            throw new UndeclaredIdentifierException(varName);
        }

        ExpressionValue expressionValue = evaluateExpression(assignmentNode.expression());
        Variable variable = symbolTable.get(varName);
        DataType type = variable.dataType();

        if (expressionValue.dataType() != type) {
            throw new TypeMismatchException(expressionValue.value().toString(), type);
        }

        symbolTable.put(varName, new Variable(type, Optional.of(expressionValue)));
        return expressionValue;
    }

    private ExpressionValue evaluateBinaryExpression(BinaryExpressionNode binaryExpressionNode) throws SemanticAnalyzerException {
        Operator op = binaryExpressionNode.op();
        ExpressionValue leftOperand = evaluateExpression(binaryExpressionNode.left());
        ExpressionValue rightOperand = evaluateExpression(binaryExpressionNode.right());

        double leftValue = leftOperand.value().doubleValue();
        double rightValue = rightOperand.value().doubleValue();

        Double value = switch (op) {
            case PLUS_SIGN -> leftValue + rightValue;
            case MINUS_SIGN -> leftValue - rightValue;
            case MULTIPLICATION_SIGN -> leftValue * rightValue;
            case DIVISION_SIGN -> {
                if (rightValue == 0) throw new DivisionByZeroException();
                yield leftValue / rightValue;
            }

            // nunca acontece
            case EQUAL_SIGN -> throw new RuntimeException("Operador de atribuição não deve ser avaliado");
        };

        if (leftOperand.dataType() == DataType.INT && rightOperand.dataType() == DataType.INT) {
            return new IntegerValue(value.intValue());
        } else {
            return new DoubleValue(value);
        }
    }
}
