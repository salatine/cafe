package tcc;

import tcc.exceptions.*;
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

    public ProgramNode parseTopLevel() throws ParserException {
        ArrayList<StatementNode> statements = new ArrayList<>();
        while (peekToken().isPresent()) {
            statements.add(parseStatement());
            skipPunctuation(Punctuation.EXCLAMATION_MARK);
        }

        return new ProgramNode(statements);
    }

    private Token nextTokenOrThrow() throws ParserException {
        try {
            return tokenStream.next().orElseThrow(UnexpectedEOFException::new);
        } catch (InvalidCharacterException e) {
            throw new ParserException("Erro ao ler próximo token", e);
        }
    }

    private StatementNode parseStatement() throws ParserException {
        if (isDataType(DataType.INT) || isDataType(DataType.DOUBLE)) {
            return parseDeclaration();
        } else if (isKeyword(Keyword.PRINT)) {
            return parsePrint();
        } else {
            return parseExpression();
        }
    }

    private PrintCallNode parsePrint() throws ParserException {
        skipKeyword(Keyword.PRINT);
        skipPunctuation(Punctuation.OPEN_PARENTHESIS);
        ExpressionNode expression = parseExpression();
        skipPunctuation(Punctuation.CLOSE_PARENTHESIS);

        return new PrintCallNode(expression);
    }

    private DeclarationNode parseDeclaration() throws ParserException {
        DataType type = parseDataType();
        IdentifierNode identifier = parseIdentifier();
        Optional<ExpressionNode> expression = Optional.empty();
        if (!isPunctuation(Punctuation.EXCLAMATION_MARK)) {
            skipOperator(Operator.EQUAL_SIGN);
            expression = Optional.of(parseExpression());
        }

        return new DeclarationNode(type, identifier, expression);
    }

    private DataType parseDataType() throws ParserException {
        Keyword word = switch (nextTokenOrThrow()) {
            case KeywordToken kt -> kt.value();
            default -> throw new UnexpectedTokenException(KeywordToken.class);
        };

        return switch (word) {
            case INT -> DataType.INT;
            case DOUBLE -> DataType.DOUBLE;
            default -> throw new UnexpectedTokenException("esperado inteiro ou real");
        };
    }

    private IdentifierNode parseIdentifier() throws ParserException {
        String name = switch (nextTokenOrThrow()) {
            case IdentifierToken it -> it.value();
            default -> throw new UnexpectedTokenException(IdentifierToken.class);
        };

        return new IdentifierNode(name);
    }

    private ExpressionNode parseExpression() throws ParserException {
        return maybeBinary(parseAtom(), 0);
    }

    private Optional<Token> peekToken() throws ParserException {
        try {
            return tokenStream.peek();
        } catch (InvalidCharacterException e) {
            throw new ParserException("Erro ao ler próximo token", e);
        }
    }

    private ExpressionNode maybeBinary(ExpressionNode left, int myPrecedence) throws ParserException {
        Optional<Token> optToken = peekToken();
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
                nextTokenOrThrow();
                ExpressionNode right = maybeBinary(parseAtom(), theirPrecedence);
                ExpressionNode expression;

                if (op.equals(Operator.EQUAL_SIGN)) {
                    if (!(left instanceof IdentifierNode identifierNode)) {
                        throw new InvalidAssigneeException();
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

    private ExpressionNode parseAtom() throws ParserException {
        if (isPunctuation(Punctuation.OPEN_PARENTHESIS)) {
            skipPunctuation(Punctuation.OPEN_PARENTHESIS);
            ExpressionNode expression = parseExpression();
            skipPunctuation(Punctuation.CLOSE_PARENTHESIS);
            return expression;
        }

        return switch(nextTokenOrThrow()) {
            case IntToken it -> new IntNode(it.value());
            case DoubleToken dt -> new DoubleNode(dt.value());
            case IdentifierToken it -> new IdentifierNode(it.value());
            default -> throw new UnexpectedTokenException("esperado número ou identificador");
        };
    }

    private boolean isPunctuation(Punctuation punc) throws ParserException {
        return peekToken()
            .map((token) -> token.equals(new PuncToken(punc)))
            .orElse(false);
    }

    private boolean isKeyword(Keyword word) throws ParserException {
         return peekToken()
            .map((token) -> token.equals(new KeywordToken(word)))
            .orElse(false);
    }

    private boolean isDataType(DataType type) throws ParserException {
        Keyword keyword = switch (type) {
            case INT -> Keyword.INT;
            case DOUBLE -> Keyword.DOUBLE;
        };
        return isKeyword(keyword);
    }

    private void skipPunctuation(Punctuation punc) throws ParserException {
        if (!isPunctuation(punc)) {
            throw new UnexpectedTokenException(new PuncToken(punc));
        }

        nextTokenOrThrow();
    }

    private void skipKeyword(Keyword word) throws ParserException {
        if (!isKeyword(word)) {
            throw new UnexpectedTokenException(new KeywordToken(word));
        }

        nextTokenOrThrow();
    }

    private boolean isOperator(Operator op) throws ParserException {
        return peekToken()
            .map((token) -> token.equals(new OperatorToken(op)))
            .orElse(false);
    }
    private void skipOperator(Operator op) throws ParserException {
        if (!isOperator(op)) {
            throw new UnexpectedTokenException(new OperatorToken(op));
        }

        nextTokenOrThrow();
    }
}
