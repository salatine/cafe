package tcc;

public enum Punctuation {
    OPEN_PARENTHESIS('('),
    CLOSE_PARENTHESIS(')'),
    EXCLAMATION_MARK('!');

    private final char value;


    Punctuation(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }
}
