package tcc.tokens;

import tcc.Keyword;

public record KeywordToken(Keyword value) implements Token {}