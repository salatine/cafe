package tcc.tokens;

public sealed interface Token permits DoubleToken, IdentifierToken, IntToken, KeywordToken, OperatorToken, PuncToken {
    Object value();
}