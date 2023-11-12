package tcc;

import tcc.exceptions.InvalidCharacterException;
import tcc.tokens.*;
import java.util.*;

// Analisador léxico
public class TokenStream {
    private final InputStream inputStream;
    private Optional<Optional<Token>> current = Optional.empty();

    public TokenStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Optional<Token> peek() throws InvalidCharacterException {
        if (current.isEmpty()) {
            current = Optional.ofNullable(readNext());
        }

        return current.get();
    }

    public Optional<Token> next() throws InvalidCharacterException {
        Optional<Token> token;

        if (current.isPresent()) {
            token = current.get();
            current = Optional.empty();
        } else {
            token = readNext();
        }

        return token;
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
        return " \t\r\n".indexOf(ch) >= 0;
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
            Optional<Character> optCh = inputStream.peek();
            if (optCh.isEmpty()) break;
            ch = optCh.get();
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

    private PuncToken readPunc(char ch) throws InvalidCharacterException {
        for (Punctuation punc : Punctuation.values()) {
            if (punc.getValue() == ch) {
                inputStream.next();
                return new PuncToken(punc);
            }
        }

        throw new InvalidCharacterException(ch, inputStream.getLine());
    }

    private OperatorToken readOperator(char ch) throws InvalidCharacterException {
        for (Operator op: Operator.values()) {
            if (op.getValue() == ch) {
                inputStream.next();
                return new OperatorToken(op);
            }
        }

        throw new InvalidCharacterException(ch, inputStream.getLine());
    }

    private void skipComment() {
        inputStream.readWhile(ch -> ch != '\n');
    }

    private boolean isComment(char ch) {
        return ch == '#';
    }

    private Optional<Token> readComment() throws InvalidCharacterException {
        skipComment();
        return readNext();
    }

    private Optional<Token> readNext() throws InvalidCharacterException {
        inputStream.readWhile(this::isWhitespace);
        Optional<Character> och = inputStream.peek();
        if (och.isEmpty()) return Optional.empty();
        char ch = och.get();
        if (isComment(ch)) return readComment();
        if (isIdStart(ch)) return Optional.of(readIdent());

        if (isPunc(ch)) return Optional.of(readPunc(ch));
        if (isDigit(ch)) return Optional.of(readNumber(ch));
        if (isOpChar(ch)) return Optional.of(readOperator(ch));

        throw new InvalidCharacterException(ch, inputStream.getLine());
    }
}
