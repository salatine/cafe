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
        ArrayList<StatementNode> statements = new ArrayList<>();
        while (!tokenStream.eof()) {
            statements.add(parseStatement());
;           if (!tokenStream.eof()) {
                skipPunctuation(Punctuation.EXCLAMATION_MARK);
            }
        }

        return new ProgramNode(statements);
    }

    private StatementNode parseStatement() {
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
        Optional<Token> optToken = tokenStream.peek();
        if (optToken.isEmpty()) {
            return left;
        }
        Token token = optToken.get();

        if (token instanceof OperatorToken ot) {
            Operator op = ot.value();
            int theirPrecedence = PRECEDENCE.get(op);

            boolean isMoreImportant = theirPrecedence > myPrecedence;
            if (op.getAssociativity() == OperatorAssociativity.RIGHT) {
                isMoreImportant = theirPrecedence >= myPrecedence;
            }

            if (isMoreImportant) {
                tokenStream.next();
                ExpressionNode right = maybeBinary(parseAtom(), theirPrecedence);
                ExpressionNode expression;

                if (op.equals(Operator.EQUAL_SIGN)) {
                    if (!(left instanceof IdentifierNode identifierNode)) {
                        throw tokenStream.croak("Invalid left-hand side in assignment");
                    }

                    expression = new AssignmentNode(identifierNode, right);
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
        return tokenStream.peek()
            .map((token) -> token.equals(new PuncToken(punc)))
            .orElse(false);
    }

    private boolean isKeyword(Keyword word) {
         return tokenStream.peek()
            .map((token) -> token.equals(new KeywordToken(word)))
            .orElse(false);
    }

    private boolean isDataType(DataType type) {
        Keyword keyword = switch (type) {
            case INT -> Keyword.INT;
            case DOUBLE -> Keyword.DOUBLE;
        };
        return isKeyword(keyword);
    }

    private void skipPunctuation(Punctuation punc) {
        if (!isPunctuation(punc)) {
            throw tokenStream.croak("Expecting punctuation: \"" + punc.getValue() + "\"");
        }

        tokenStream.next();
    }

    private void skipKeyword(Keyword word) {
        if (!isKeyword(word)) {
            throw tokenStream.croak("Expecting keyword: \"" + word.getValue() + "\"");
        }

        tokenStream.next();
    }

    private boolean isOperator(Operator op) {
        return tokenStream.peek()
            .map((token) -> token.equals(new OperatorToken(op)))
            .orElse(false);
    }
    private void skipOperator(Operator op) {
        if (!isOperator(op)) {
            throw tokenStream.croak("Expecting operator: \"" + op.getValue() + "\"");
        }

        tokenStream.next();
    }
}
