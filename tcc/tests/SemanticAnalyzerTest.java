package tcc.tests;

import org.junit.Assert;
import org.junit.Test;
import tcc.InputStream;
import tcc.Parser;
import tcc.SemanticAnalyzer;
import tcc.TokenStream;
import tcc.exceptions.*;

public class SemanticAnalyzerTest {

    public void analyzeInput(String input) throws ParserException, SemanticAnalyzerException {
        InputStream inputStream = new InputStream(input);
        TokenStream tokenStream = new TokenStream(inputStream);
        Parser parser = new Parser(tokenStream);
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.analyze(parser.parseTopLevel());
    }

    @Test
    public void testDeclarationValueMismatch() {
        String input = "inteiro a = 1.0!";
        Assert.assertThrows(TypeMismatchException.class, () -> analyzeInput(input));
    }

    @Test
    public void testAssignmentValueMismatch() {
        String input = "inteiro a! \n a = 1.0!";
        Assert.assertThrows(TypeMismatchException.class, () -> analyzeInput(input));
    }

    @Test
    public void testInvalidAssignment() {
        String input = "a = 1.0!";
        Assert.assertThrows(UndeclaredIdentifierException.class, () -> analyzeInput(input));
    }

    @Test
    public void testReadNotInitializedVariable() {
        String input = "inteiro a! \n inteiro b = a!";
        Assert.assertThrows(UnitializedIdentifierException.class, () -> analyzeInput(input));
    }

    @Test
    public void testDivisionByZero() {
        String input = "inteiro a = 1 / 0!";
        Assert.assertThrows(DivisionByZeroException.class, () -> analyzeInput(input));
    }

    @Test
    public void testRedeclaration() {
        String input = "inteiro a! \n inteiro a!";
        Assert.assertThrows(RedeclarationException.class, () -> analyzeInput(input));
    }
}