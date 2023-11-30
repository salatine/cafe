package tcc.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tcc.*;
import tcc.exceptions.InvalidCharacterException;
import tcc.tokens.*;

import java.util.Optional;

public class TokenStreamTest {
    private static final String INPUT = "inteiro x = 1! # oi \n real y = 2,0 + 3,0!";
    private TokenStream tokenStream;

    @Before
    public void setup() {
        InputStream inputStream = new InputStream(INPUT);
        tokenStream = new TokenStream(inputStream);
    }

    @Test
    public void testTokens() throws InvalidCharacterException {
        // first line
        Assert.assertEquals(new KeywordToken(Keyword.INT), tokenStream.next().get());
        Assert.assertEquals(new IdentifierToken("x"), tokenStream.next().get());
        Assert.assertEquals(new OperatorToken(Operator.EQUAL_SIGN), tokenStream.next().get());
        Assert.assertEquals(new IntToken(1), tokenStream.next().get());
        Assert.assertEquals(new PuncToken(Punctuation.EXCLAMATION_MARK), tokenStream.next().get());

        // second line
        Assert.assertEquals(new KeywordToken(Keyword.DOUBLE), tokenStream.next().get());
        Assert.assertEquals(new IdentifierToken("y"), tokenStream.next().get());
        Assert.assertEquals(new OperatorToken(Operator.EQUAL_SIGN), tokenStream.next().get());
        Assert.assertEquals(new DoubleToken(2.0), tokenStream.next().get());
        Assert.assertEquals(new OperatorToken(Operator.PLUS_SIGN), tokenStream.next().get());
        Assert.assertEquals(new DoubleToken(3.0), tokenStream.next().get());
        Assert.assertEquals(new PuncToken(Punctuation.EXCLAMATION_MARK), tokenStream.next().get());
    }

    @Test
    public void testPeekAndNext() throws InvalidCharacterException {
        Optional<Token> peekedToken = tokenStream.peek();

        Assert.assertTrue(peekedToken.isPresent());
        Assert.assertEquals(peekedToken, tokenStream.peek());
        Assert.assertEquals(peekedToken.get(), tokenStream.next().get());
        Assert.assertNotEquals(peekedToken, tokenStream.peek());
    }
}
