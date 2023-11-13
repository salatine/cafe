package tcc.exceptions;

public class InvalidAssigneeException extends ParserException {
    public InvalidAssigneeException() {
        super("Atribuição inválida");
    }
}
