package tcc;

import tcc.nodes.*;
import tcc.tokens.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class Parser {
    private final TokenStream tokenStream;

    private static final Map<Operator, Integer> PRECEDENCE = Map.of(
            Operator.EQUAL_SIGN, 1,
            Operator.PLUS_SIGN, 2,
            Operator.MINUS_SIGN, 2,
            Operator.DIVISION_SIGN, 3,
            Operator.MULTIPLICATION_SIGN, 3
    );

    public Parser(TokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    public ProgramNode parseTopLevel() {
        ArrayList<Node> statements = new ArrayList<>();
        while (!tokenStream.eof()) {
            statements.add(parseStatement());
;           if (!tokenStream.eof()) {
                skipPunctuation(Punctuation.EXCLAMATION_MARK);
            }
        }

        return new ProgramNode(statements);
    }

    private Node parseStatement() {
        if (isDataType(DataType.INT) || isDataType(DataType.DOUBLE)) {
            return parseDeclaration();
        } else if (isKeyword(Keyword.PRINT)) {
            return parsePrint();
        } else {
            return parseExpression();
        }
    }

    private PrintCallNode parsePrint() {
        skipKeyword(Keyword.PRINT);
        skipPunctuation(Punctuation.OPEN_PARENTHESIS);
        ExpressionNode expression = parseExpression();
        skipPunctuation(Punctuation.CLOSE_PARENTHESIS);

        return new PrintCallNode(expression);
    }

    private DeclarationNode parseDeclaration() {
        DataType type = parseDataType();
        IdentifierNode identifier = parseIdentifier();
        Optional<ExpressionNode> expression = Optional.empty();
        if (!isPunctuation(Punctuation.EXCLAMATION_MARK)) {
            skipOperator(Operator.EQUAL_SIGN);
            expression = Optional.of(parseExpression());
        }

        return new DeclarationNode(type, identifier, expression);
    }

    private DataType parseDataType() {
        Keyword word = switch (tokenStream.next()) {
            case KeywordToken kt -> kt.value();
            default -> throw tokenStream.croak("Expected DataType keyword");
        };

        return switch (word) {
            case INT -> DataType.INT;
            case DOUBLE -> DataType.DOUBLE;
            default -> throw tokenStream.croak("Expected DataType keyword");
        };
    }

    private IdentifierNode parseIdentifier() {
        String name = switch (tokenStream.next()) {
            case IdentifierToken it -> it.value();
            default -> throw tokenStream.croak("Expected identifier");
        };

        return new IdentifierNode(name);
    }

    private ExpressionNode parseExpression() {
        return maybeBinary(parseAtom(), 0);
    }

    private ExpressionNode maybeBinary(ExpressionNode left, int myPrecedence) {
        Token token = tokenStream.peek();
        if (token instanceof OperatorToken ot) {
            Operator op = ot.value();
            int theirPrecedence = PRECEDENCE.get(op);
            if (theirPrecedence > myPrecedence) {
                tokenStream.next();
                ExpressionNode right = maybeBinary(parseAtom(), theirPrecedence);
                ExpressionNode expression;

                if (op.equals(Operator.EQUAL_SIGN)) {
                    expression = new AssignmentNode(left, right);
                } else {
                    expression = new BinaryExpressionNode(op, left, right);
                }

                return maybeBinary(expression, myPrecedence);
            }
        }

        return left;
    }

    private ExpressionNode parseAtom() {
        if (isPunctuation(Punctuation.OPEN_PARENTHESIS)) {
            skipPunctuation(Punctuation.OPEN_PARENTHESIS);
            ExpressionNode expression = parseExpression();
            skipPunctuation(Punctuation.CLOSE_PARENTHESIS);
            return expression;
        }

        return switch(tokenStream.next()) {
            case IntToken it -> new IntNode(it.value());
            case DoubleToken dt -> new DoubleNode(dt.value());
            case IdentifierToken it -> new IdentifierNode(it.value());
            default -> throw tokenStream.croak("Expected expression");
        };
    }

    private boolean isPunctuation(Punctuation punc) {
        Token token = tokenStream.peek();
        return token instanceof PuncToken pt && pt.value() == punc;
    }

    private boolean isKeyword(Keyword word) {
        Token token = tokenStream.peek();
        return token instanceof KeywordToken kt && kt.value() == word;
    }

    private boolean isDataType(DataType type) {
        Keyword keyword = switch (type) {
            case INT -> Keyword.INT;
            case DOUBLE -> Keyword.DOUBLE;
        };
        Token token = tokenStream.peek();
        return token instanceof KeywordToken kt && kt.value() == keyword;
    }

    private void skipPunctuation(Punctuation punc) {
        if (!isPunctuation(punc)) {
            throw tokenStream.croak("Expecting punctuation: \"" + punc + "\"");
        }

        tokenStream.next();
    }

    private void skipKeyword(Keyword word) {
        if (!isKeyword(word)) {
            throw tokenStream.croak("Expecting keyword: \"" + word + "\"");
        }

        tokenStream.next();
    }

    private boolean isOperator(Operator op) {
        Token token = tokenStream.peek();
        return token instanceof OperatorToken ot && ot.value() == op;
    }
    private void skipOperator(Operator op) {
        if (!isOperator(op)) {
            throw tokenStream.croak("Expecting operator: \"" + op + "\"");
        }

        tokenStream.next();
    }
}
