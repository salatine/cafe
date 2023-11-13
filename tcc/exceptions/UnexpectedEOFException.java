package tcc.exceptions;

public class UnexpectedEOFException extends ParserException {
    public UnexpectedEOFException() {
        super("Fim de arquivo inesperado");
    }
}
