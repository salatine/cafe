package tcc.nodes;

public record AssignmentNode(Node left, Node right) implements ExpressionNode {}