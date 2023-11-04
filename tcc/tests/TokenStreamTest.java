package tcc.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tcc.*;
import tcc.tokens.*;

public class TokenStreamTest {
    private static final String INPUT = "inteiro x = 1! # oi \n real y = 2.0 + 3.0!";
    private TokenStream tokenStream;

    @Before
    public void setup() {
        InputStream inputStream = new InputStream(INPUT);
        tokenStream = new TokenStream(inputStream);
    }

    @Test
    public void testTokens() {
        // first line
        Assert.assertEquals(new KeywordToken(Keyword.INT), tokenStream.next());
        Assert.assertEquals(new IdentifierToken("x"), tokenStream.next());
        Assert.assertEquals(new OperatorToken(Operator.EQUAL_SIGN), tokenStream.next());
        Assert.assertEquals(new IntToken(1), tokenStream.next());
        Assert.assertEquals(new PuncToken(Punctuation.EXCLAMATION_MARK), tokenStream.next());

        // second line
        Assert.assertEquals(new KeywordToken(Keyword.DOUBLE), tokenStream.next());
        Assert.assertEquals(new IdentifierToken("y"), tokenStream.next());
        Assert.assertEquals(new OperatorToken(Operator.EQUAL_SIGN), tokenStream.next());
        Assert.assertEquals(new DoubleToken(2.0), tokenStream.next());
        Assert.assertEquals(new OperatorToken(Operator.PLUS_SIGN), tokenStream.next());
        Assert.assertEquals(new DoubleToken(3.0), tokenStream.next());
        Assert.assertEquals(new PuncToken(Punctuation.EXCLAMATION_MARK), tokenStream.next());
    }

    @Test
    public void testPeekAndNext() {
        Token peekedToken = tokenStream.peek();
        Assert.assertNotNull(peekedToken);
        Assert.assertEquals(peekedToken, tokenStream.peek());
        Assert.assertEquals(peekedToken, tokenStream.next());
        Assert.assertNotEquals(peekedToken, tokenStream.peek());
    }
}
