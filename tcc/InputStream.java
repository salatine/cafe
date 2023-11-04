package tcc;

import java.util.Optional;

public class InputStream {
    private int position = 0;
    private int line = 1;
    private int column = 0;
    private final String input;

    public InputStream(String input) {
        this.input = input;
    }

    public char next() {
        char c = input.charAt(position++);
        if (c == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }

        return c;
    }


    public String readWhile(CharacterPredicate predicate) {
        StringBuilder str = new StringBuilder();
        Optional<Character> ch = peek();
        while (ch.isPresent() && predicate.predicate(ch.get())) {
            str.append(ch.get());
            next();
            ch = peek();
        }
        return str.toString();
    }

    public Optional<Character> peek() {
        if (position >= input.length()) {
            return Optional.empty();
        }
        return Optional.of(input.charAt(position));
    }

    public boolean eof() {
        return peek().isEmpty();
    }

    public RuntimeException croak(String msg) {
        return new RuntimeException(msg + " (" + line + ":" + column + ")");
    }

    public interface CharacterPredicate {
        boolean predicate(char ch);
    }
}
