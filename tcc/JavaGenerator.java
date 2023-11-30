package tcc;

import tcc.nodes.*;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Optional;

public class JavaGenerator {
    public String generate(ProgramNode programNode, String className) {
        StringBuilder main = new StringBuilder();

        main.append(String.format("public class %s {\n", className));
        main.append("\tpublic static void main(String[] args) {\n");
        for (StatementNode statement : programNode.statements()) {
            String line = switch (statement) {
                case DeclarationNode declarationNode -> generateDeclaration(declarationNode);
                case PrintCallNode printCallNode -> generatePrintCall(printCallNode);
                case ExpressionNode expressionNode -> generateExpression(expressionNode);
            };

            main.append("\t\t").append(line).append(";\n");
        }
        main.append("\t}\n");
        main.append("}\n");

        return main.toString();
    }



    private String generateDeclaration(DeclarationNode declarationNode) {
        String varName = generateIdentifier(declarationNode.identifier());
        String type = generateDataType(declarationNode.type());
        Optional<ExpressionNode> expression = declarationNode.expression();

        if (expression.isPresent()) {
            return String.format("%s %s = %s", type, varName, generateExpression(expression.get()));
        }

        return String.format("%s %s", type, varName);
    }

    private String generatePrintCall(PrintCallNode printCallNode) {
        return String.format("System.out.println(%s)", generateExpression(printCallNode.parameter()));
    }

    private String generateExpression(ExpressionNode expressionNode) {
        return switch (expressionNode) {
            case BinaryExpressionNode binaryExpressionNode -> generateBinaryExpression(binaryExpressionNode);
            case IdentifierNode identifierNode -> generateIdentifier(identifierNode);
            case AssignmentNode assignmentNode -> generateAssignment(assignmentNode);
            case IntNode intNode -> generateInt(intNode);
            case DoubleNode doubleNode -> generateDouble(doubleNode);
        };
    }

    private String generateDataType(DataType dataType) {
        return switch (dataType) {
            case INT -> "int";
            case DOUBLE -> "double";
        };
    }

    private String generateBinaryExpression(BinaryExpressionNode binaryExpressionNode) {
        String left = generateExpression(binaryExpressionNode.left());
        String right = generateExpression(binaryExpressionNode.right());
        char op = binaryExpressionNode.op().getValue();

        return String.format("(%s %s %s)", left, op, right);
    }

    private String generateIdentifier(IdentifierNode identifierNode) {
        return removeAccents(identifierNode.name());
    }

    private String generateAssignment(AssignmentNode assignmentNode) {
        String identifier = generateIdentifier(assignmentNode.identifier());
        String expression = generateExpression(assignmentNode.expression());

        return String.format("%s = %s", identifier, expression);
    }

    private String generateInt(IntNode intNode) {
        return String.valueOf(intNode.value());
    }

    private String generateDouble(DoubleNode doubleNode) {
        return String.valueOf(doubleNode.value());
    }

    public static String removeAccents(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}