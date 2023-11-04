package tcc;

public enum Operator {
    PLUS_SIGN('+'),
    MINUS_SIGN('-'),
    MULTIPLICATION_SIGN('*'),
    DIVISION_SIGN('/'),
    EQUAL_SIGN('=');

    private final char value;

    Operator(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }
}
