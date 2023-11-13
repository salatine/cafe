package tcc.tests;

import org.junit.Assert;
import org.junit.Test;
import tcc.*;
import tcc.exceptions.ParserException;
import tcc.exceptions.SemanticAnalyzerException;
import tcc.nodes.ProgramNode;

public class JavaGeneratorTest {

    @Test
    public void test() throws ParserException, SemanticAnalyzerException {
        String input = "inteiro a = 1! \n inteiro b = 2! \n inteiro c = a + b! \n imprimir(c)!";

        InputStream inputStream = new InputStream(input);
        TokenStream tokenStream = new TokenStream(inputStream);
        Parser parser = new Parser(tokenStream);
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        ProgramNode programNode = parser.parseTopLevel();
        semanticAnalyzer.analyze(programNode);

        // generate java code
        JavaGenerator javaGenerator = new JavaGenerator();
        String output = javaGenerator.generate(programNode, "Main");

        Assert.assertEquals("""
            public class Main {
            \tpublic static void main(String[] args) {
            \t\tint a = 1;
            \t\tint b = 2;
            \t\tint c = (a + b);
            \t\tSystem.out.println(c);
            \t}
            }
            """, output);
    }
}