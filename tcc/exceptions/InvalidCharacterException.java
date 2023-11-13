package tcc.exceptions;

public class InvalidCharacterException extends TokenStreamException {
    public InvalidCharacterException(char ch, int line) {
        super(String.format("Caractere inválido '%c' na linha %d", ch, line));
    }
}