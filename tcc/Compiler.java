package tcc;

import tcc.exceptions.ParserException;
import tcc.exceptions.SemanticAnalyzerException;
import tcc.nodes.ProgramNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class Compiler {
    public static void main(String[] args) throws IOException, ParserException, SemanticAnalyzerException {
        Path inputPath = Paths.get(args[0]);
        String input = Files.readString(inputPath);

        InputStream inputStream = new InputStream(input);
        TokenStream tokenStream = new TokenStream(inputStream);
        Parser parser = new Parser(tokenStream);
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        ProgramNode programNode = parser.parseTopLevel();
        semanticAnalyzer.analyze(programNode);

        // generate java code
        JavaGenerator javaGenerator = new JavaGenerator();
        String output = javaGenerator.generate(programNode, capitalize(getFileWithoutExtension(inputPath)));
        Files.writeString(getOutputPath(inputPath), output);
    }
    private static Path getOutputPath(Path inputPath) {
        Path path = inputPath.getParent();
        if (path == null) {
            path = Paths.get("");
        }

        String fileName = capitalize(getFileWithoutExtension(inputPath)) + ".java";
        return Paths.get(path.toString(), fileName);
    }

    private static String getBasename(Path inputPath) {
        return inputPath.getFileName().toString();
    }

    private static String getFileWithoutExtension(Path inputPath) {
        return getBasename(inputPath).substring(0, getBasename(inputPath).lastIndexOf('.'));
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}