package tcc.exceptions;

public class UndeclaredIdentifierException extends SemanticAnalyzerException {
    public UndeclaredIdentifierException(String varName) {
        super("Váriavel" + varName + " não foi declarada");
    }
}
