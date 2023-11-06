package tcc.nodes;

import tcc.Operator;

public record BinaryExpressionNode(Operator op, ExpressionNode left, ExpressionNode right) implements ExpressionNode {}
