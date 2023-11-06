package tcc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public void analyze(ProgramNode programNode) {
        for (StatementNode statement : programNode.statements()) {
            visitStatement(statement);
        }
    }

    private void visitStatement(StatementNode statement) {
        switch (statement) {
            case DeclarationNode declarationNode -> visitDeclaration(declarationNode);
            case ExpressionNode expressionNode -> evaluateExpression(expressionNode);
            case PrintCallNode printCallNode -> evaluateExpression(printCallNode.parameter());
        }
    }

    private void visitDeclaration(DeclarationNode declarationNode) {
        String varName = declarationNode.identifier().name();
        DataType type = declarationNode.type();
        Optional<ExpressionValue> varValue = declarationNode.expression().map(this::evaluateExpression);
        varValue.ifPresent(value -> {
            if (value.dataType() != type) {
                throw new RuntimeException("Type mismatch: cannot assign " + value + " to " + type + " variable.");
            }
        });

        symbolTable.put(varName, new Variable(type, varValue));
    }

    private ExpressionValue readIdentifier(IdentifierNode identifierNode) {
        String varName = identifierNode.name();

        if (!symbolTable.containsKey(varName)) {
            throw new RuntimeException("Identifier '" + varName + "' has not been declared.");
        }

        Variable variable = symbolTable.get(varName);
        Optional<ExpressionValue> value = variable.value();
        return value.orElseThrow(() -> new RuntimeException("Identifier '" + varName + "' has not been initialized."));
    }

    private ExpressionValue evaluateExpression(ExpressionNode expression) {
        return switch (expression) {
            case IntNode in -> new IntegerValue(in.value());
            case DoubleNode db -> new DoubleValue(db.value());
            case IdentifierNode id -> readIdentifier(id);
            case BinaryExpressionNode bin -> evaluateBinaryExpression(bin);
            case AssignmentNode assignmentNode -> evaluateAssignment(assignmentNode);
        };
    }

    private ExpressionValue evaluateAssignment(AssignmentNode assignmentNode) {
        String varName = assignmentNode.identifier().name();
        if (!symbolTable.containsKey(varName)) {
            throw new RuntimeException("Identifier '" + varName + "' has not been declared.");
        }

        ExpressionValue expressionValue = evaluateExpression(assignmentNode.expression());
        Variable variable = symbolTable.get(varName);
        DataType type = variable.dataType();

        if (expressionValue.dataType() != type) {
            throw new RuntimeException("Type mismatch: cannot assign " + expressionValue + " to " + type + " variable.");
        }

        symbolTable.put(varName, new Variable(type, Optional.of(expressionValue)));
        return expressionValue;
    }

    private ExpressionValue evaluateBinaryExpression(BinaryExpressionNode binaryExpressionNode) {
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
                if (rightValue == 0) throw new RuntimeException("Division by zero.");
                yield leftValue / rightValue;
            }
            case EQUAL_SIGN -> throw new RuntimeException("Unexpected assignment operator in binary expression.");
        };

        if (leftOperand.dataType() == DataType.INT && rightOperand.dataType() == DataType.INT) {
            return new IntegerValue(value.intValue());
        } else {
            return new DoubleValue(value);
        }
    }
}
