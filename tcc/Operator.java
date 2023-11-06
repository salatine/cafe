package tcc;

public enum Operator {
    PLUS_SIGN('+', OperatorAssociativity.LEFT),
    MINUS_SIGN('-', OperatorAssociativity.LEFT),
    MULTIPLICATION_SIGN('*', OperatorAssociativity.LEFT),
    DIVISION_SIGN('/', OperatorAssociativity.LEFT),
    EQUAL_SIGN('=', OperatorAssociativity.RIGHT);

    private final char value;
    private final OperatorAssociativity associativity;

    Operator(char value, OperatorAssociativity associativity) {
        this.value = value;
        this.associativity = associativity;
    }

    public char getValue() {
        return value;
    }

    public OperatorAssociativity getAssociativity() {
        return associativity;
    }
}
