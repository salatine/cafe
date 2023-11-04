package tcc;

import tcc.tokens.*;
import java.util.*;

// Analisador léxico
public class TokenStream {
    private final InputStream inputStream;
    private Optional<Token> current = Optional.empty();

    public TokenStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Token peek() {
        if (current.isEmpty()) {
            current = Optional.ofNullable(readNext());
        }

        return current.orElse(null);
    }

    public Token next() {
        Token token = current.orElseGet(this::readNext);
        current = Optional.empty();
        return token;
    }

    public boolean eof() {
        return peek() == null;
    }

    public RuntimeException croak(String msg) {
        return inputStream.croak(msg);
    }

    private boolean isKeyword(String word) {
        for (Keyword keyword : Keyword.values()) {
            if (Objects.equals(keyword.getValue(), word)) {
                return true;
            }
        }

        return false;
    }

    private boolean isDigit(char ch) {
        return "0123456789".indexOf(ch) >= 0;
    }

    private boolean isIdStart(char ch) {
        return Character.isLetter(ch);
    }

    private boolean isId(char ch) {
        return isIdStart(ch) || isDigit(ch);
    }

    private boolean isOpChar(char ch) {
        for (Operator op : Operator.values()) {
            if (op.getValue() == ch) {
                return true;
            }
        }

        return false;
    }

    private boolean isPunc(char ch) {
        for (Punctuation punc : Punctuation.values()) {
            if (punc.getValue() == ch) {
                return true;
            }
        }

        return false;
    }

    private boolean isWhitespace(char ch) {
        return " \t\n".indexOf(ch) >= 0;
    }

    private Token readNumber(char ch) {
        boolean hasDot = false;
        StringBuilder number = new StringBuilder();

        while (!inputStream.eof()) {
            if (ch == '.') {
                if (hasDot) break;
                hasDot = true;
            }
            if (isDigit(ch) || ch == '.') {
                number.append(ch);
            } else {
                break;
            }
            inputStream.next();
            ch = inputStream.peek().orElseThrow();
        }

        if (hasDot) {
            return new DoubleToken(Double.parseDouble(number.toString()));
        }

        return new IntToken(Integer.parseInt(number.toString()));
    }

    private Token readIdent() {
        String id = inputStream.readWhile(this::isId);
        if (isKeyword(id)) {
            for (Keyword keyword : Keyword.values()) {
                if (Objects.equals(keyword.getValue(), id)) {
                    return new KeywordToken(keyword);
                }
            }
        }

        return new IdentifierToken(id);
    }

    private PuncToken readPunc(char ch) {
        for (Punctuation punc : Punctuation.values()) {
            if (punc.getValue() == ch) {
                inputStream.next();
                return new PuncToken(punc);
            }
        }

        return null;
    }

    private OperatorToken readOperator(char ch) {
        for (Operator op: Operator.values()) {
            if (op.getValue() == ch) {
                inputStream.next();
                return new OperatorToken(op);
            }
        }

        return null;
    }

    private void skipComment() {
        inputStream.readWhile(ch -> ch != '\n');
    }

    private boolean isComment(char ch) {
        return ch == '#';
    }

    private Token readComment() {
        skipComment();
        return readNext();
    }

    private Token readNext() {
        inputStream.readWhile(this::isWhitespace);
        Optional<Character> och = inputStream.peek();
        if (och.isEmpty()) return null;
        char ch = och.get();
        if (isComment(ch)) return readComment();
        if (isIdStart(ch)) return readIdent();

        if (isPunc(ch)) return readPunc(ch);
        if (isDigit(ch)) return readNumber(ch);
        if (isOpChar(ch)) return readOperator(ch);

        throw inputStream.croak("Invalid character: " + ch);
    }
}
