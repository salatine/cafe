package tcc.exceptions;

import tcc.tokens.Token;

public class UnexpectedTokenException extends ParserException {

    private Token expectedToken;
    public UnexpectedTokenException(Class<? extends Token> expectedTokenClass) {
        super(String.format("Token inesperado, esperado %s", expectedTokenClass.getSimpleName()));
    }

    public UnexpectedTokenException(Token expectedToken) {
        super(String.format("Token inesperado, esperado %s", expectedToken));
        this.expectedToken = expectedToken;
    }

    public UnexpectedTokenException(String description) {
        super(String.format("Token inesperado, %s", description));
    }

    public Token getExpectedToken() {
        return expectedToken;
    }
}
