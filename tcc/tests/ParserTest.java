package tcc.tests;

import org.junit.Assert;
import org.junit.Test;
import tcc.*;
import tcc.nodes.*;

import java.util.List;
import java.util.Optional;

public class ParserTest {

    public Parser createParser(String input) {
        InputStream inputStream = new InputStream(input);
        TokenStream tokenStream = new TokenStream(inputStream);
        return new Parser(tokenStream);
    }

    public ProgramNode createProgram(StatementNode... statements) {
        return new ProgramNode(List.of(statements));
    }

    @Test
    public void testPrintCall() {
        String input = "imprimir(6+9)";

        ProgramNode expected = createProgram(
            new PrintCallNode(
                new BinaryExpressionNode(
                    Operator.PLUS_SIGN,
                    new IntNode(6),
                    new IntNode(9)
                )
            )
        );

        Parser parser = createParser(input);
        Assert.assertEquals(expected, parser.parseTopLevel());
    }

    @Test
    public void testDeclaration() {
        String input = "real a = 1.0!";

        ProgramNode expected = createProgram(
            new DeclarationNode(
                DataType.DOUBLE,
                new IdentifierNode("a"),
                Optional.of(new DoubleNode(1.0))
            )
        );

        Parser parser = createParser(input);
        Assert.assertEquals(expected, parser.parseTopLevel());
    }

    @Test
    public void testDeclarationWithoutInitialization() {
        String input = "inteiro a!";

        ProgramNode expected = createProgram(
            new DeclarationNode(
                DataType.INT,
                new IdentifierNode("a"),
                Optional.empty()
            )
        );

        Parser parser = createParser(input);
        Assert.assertEquals(expected, parser.parseTopLevel());
    }

    @Test
    public void testDeclarationWithoutValue() {
        String input = "inteiro a = ";
        Parser parser = createParser(input);
        Assert.assertThrows(RuntimeException.class, parser::parseTopLevel);
    }

    @Test
    public void testInvalidDeclaration() {
        String input = "inteiro";
        Parser parser = createParser(input);
        RuntimeException exception = Assert.assertThrows(RuntimeException.class, parser::parseTopLevel);
        Assert.assertTrue(exception.getMessage().contains("Unexpected end of input"));
    }

    @Test
    public void testAssignment() {
        String input = "i = 1!";

        ProgramNode expected = createProgram(
            new AssignmentNode(
                new IdentifierNode("i"),
                new IntNode(1)
            )
        );

        Parser parser = createParser(input);
        Assert.assertEquals(expected, parser.parseTopLevel());
    }

    @Test
    public void testMultipleAssignments() {
        String input = "a = b = 2!";
        Parser parser = createParser(input);
        Assert.assertEquals(
            createProgram(
                new AssignmentNode(
                    new IdentifierNode("a"),
                    new AssignmentNode(
                        new IdentifierNode("b"),
                        new IntNode(2)
                    )
                )
            ),
            parser.parseTopLevel()
        );
    }

    @Test
    public void testMultipleAssignmentsWithExpression() {
        String input = "a = b = c + 1";
        Parser parser = createParser(input);
        Assert.assertEquals(
            createProgram(
                new AssignmentNode(
                    new IdentifierNode("a"),
                    new AssignmentNode(
                        new IdentifierNode("b"),
                        new BinaryExpressionNode(
                            Operator.PLUS_SIGN,
                            new IdentifierNode("c"),
                            new IntNode(1)
                        )
                    )
                )
            ),
            parser.parseTopLevel()
        );
    }

    @Test
    public void testInvalidAssignment() {
        String input = "(1 + 2) = 3 + 4!";
        Parser parser = createParser(input);
        RuntimeException exception = Assert.assertThrows(RuntimeException.class, parser::parseTopLevel);
        Assert.assertTrue(exception.getMessage().contains("Invalid left-hand side in assignment"));
    }

    @Test
    public void testExpression() {
        String input = "1 + 2 * (3 * 4 / (1))";

        ProgramNode expected = createProgram(
            new BinaryExpressionNode(
                Operator.PLUS_SIGN,
                new IntNode(1),
                new BinaryExpressionNode(
                    Operator.MULTIPLICATION_SIGN,
                    new IntNode(2),
                    new BinaryExpressionNode(
                        Operator.DIVISION_SIGN,
                        new BinaryExpressionNode(
                            Operator.MULTIPLICATION_SIGN,
                            new IntNode(3),
                            new IntNode(4)
                        ),
                        new IntNode(1)
                    )
                )
            )
        );

        Parser parser = createParser(input);
        Assert.assertEquals(expected, parser.parseTopLevel());
    }

    @Test
    public void testUnbalancedParenthesis() {
        String input = "((1 + 2) * 3";
        Parser parser = createParser(input);
        RuntimeException exception = Assert.assertThrows(RuntimeException.class, parser::parseTopLevel);
        Assert.assertTrue(exception.getMessage().contains("Expecting punctuation: \")\""));
    }

    @Test
    public void testAssociativeSumBinaryExpression() {
        String input = "1 + 2 + 3!";
        Parser parser = createParser(input);
        Assert.assertEquals(
            createProgram(
                new BinaryExpressionNode(
                    Operator.PLUS_SIGN,
                    new BinaryExpressionNode(
                        Operator.PLUS_SIGN,
                        new IntNode(1),
                        new IntNode(2)
                    ),
                    new IntNode(3)
                )
            ),
            parser.parseTopLevel()
        );
    }

    @Test
    public void testAssociativeMultiplicationBinaryExpression() {
        String input = "2 * 2 / 2!";
        Parser parser = createParser(input);
        Assert.assertEquals(
            createProgram(
                new BinaryExpressionNode(
                    Operator.DIVISION_SIGN,
                    new BinaryExpressionNode(
                        Operator.MULTIPLICATION_SIGN,
                        new IntNode(2),
                        new IntNode(2)
                    ),
                    new IntNode(2)
                )
            ),
            parser.parseTopLevel()
        );
    }
}
