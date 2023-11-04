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

    public ProgramNode createProgram(Node... nodes) {
        return new ProgramNode(List.of(nodes));
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
}
