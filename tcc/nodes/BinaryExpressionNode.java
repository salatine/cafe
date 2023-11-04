package tcc.nodes;

import tcc.Operator;

public record BinaryExpressionNode(Operator op, Node left, Node right) implements ExpressionNode {}
