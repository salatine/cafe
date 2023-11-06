package tcc.tests;

import org.junit.Assert;
import org.junit.Test;
import tcc.InputStream;
import tcc.Parser;
import tcc.SemanticAnalyzer;
import tcc.TokenStream;

public class SemanticAnalyzerTest {

    public void analyzeInput(String input) {
        InputStream inputStream = new InputStream(input);
        TokenStream tokenStream = new TokenStream(inputStream);
        Parser parser = new Parser(tokenStream);
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.analyze(parser.parseTopLevel());
    }

    @Test
    public void testDeclarationValueMismatch() {
        String input = "inteiro a = 1.0!";
        RuntimeException exception = Assert.assertThrows(RuntimeException.class, () -> analyzeInput(input));
        Assert.assertTrue(exception.getMessage().contains("Type mismatch"));
    }

    @Test
    public void testAssignmentValueMismatch() {
        String input = "inteiro a! \n a = 1.0!";
        RuntimeException exception = Assert.assertThrows(RuntimeException.class, () -> analyzeInput(input));
        Assert.assertTrue(exception.getMessage().contains("Type mismatch"));
    }

    @Test
    public void testInvalidAssignment() {
        String input = "a = 1.0!";
        RuntimeException exception = Assert.assertThrows(RuntimeException.class, () -> analyzeInput(input));
        Assert.assertTrue(exception.getMessage().contains("has not been declared"));
    }

    @Test
    public void testReadNotInitializedVariable() {
        String input = "inteiro a! \n inteiro b = a!";
        RuntimeException exception = Assert.assertThrows(RuntimeException.class, () -> analyzeInput(input));
        Assert.assertTrue(exception.getMessage().contains("has not been initialized"));
    }

    @Test
    public void testDivisionByZero() {
        String input = "inteiro a = 1 / 0!";
        RuntimeException exception = Assert.assertThrows(RuntimeException.class, () -> analyzeInput(input));
        Assert.assertTrue(exception.getMessage().contains("Division by zero"));
    }
}