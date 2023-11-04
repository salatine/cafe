package tcc.tokens;

import tcc.Operator;

public record OperatorToken(Operator value) implements Token {}
