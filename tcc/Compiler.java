package tcc;

import tcc.tokens.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Compiler {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get(args[0]);
        String input = Files.readString(path);
        InputStream inputStream = new InputStream(input);
        TokenStream tokenStream = new TokenStream(inputStream);
        Parser parser = new Parser(tokenStream);
        System.out.println(parser.parseTopLevel());

    }

//    public String printToken(Token t) {
//        return switch (t) {
//            case IntToken it -> "int: " + it.getValue() * 2;
//            case OperatorToken pt -> "punc: " + pt.getValue().getValue() * 2;
//            case FloatToken ft -> null;
//        };
//    }
}
