package tcc;

import tcc.nodes.ProgramNode;
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
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.analyze(parser.parseTopLevel());
    }
}