package tcc.exceptions;

public class DivisionByZeroException extends SemanticAnalyzerException {
    public DivisionByZeroException() {
        super("Divisão por zero não é permitida");
    }
}
