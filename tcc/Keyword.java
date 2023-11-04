package tcc;

public enum Keyword {
    INT("inteiro"),
    DOUBLE("real"),
    PRINT("imprimir");

    private final String value;

    Keyword(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
