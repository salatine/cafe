package tcc.exceptions;

public class RedeclarationException extends SemanticAnalyzerException {
    public RedeclarationException(String varName) {
        super("Váriavel" + varName + " já foi declarada");
    }
}
