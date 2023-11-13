package tcc.exceptions;

public class UnitializedIdentifierException extends SemanticAnalyzerException {
    public UnitializedIdentifierException(String varName) {
        super("Váriavel" + varName + " não foi inicializada");
    }
}
