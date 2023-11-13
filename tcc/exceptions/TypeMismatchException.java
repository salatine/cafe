package tcc.exceptions;

import tcc.DataType;

public class TypeMismatchException extends SemanticAnalyzerException {
    public TypeMismatchException(String value, DataType expectedType) {
        super("Tipo incompatível para o valor " + value + ". Esperado: " + expectedType);
    }
}
