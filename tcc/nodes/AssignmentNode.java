package tcc.nodes;

public record AssignmentNode(IdentifierNode identifier, ExpressionNode expression) implements ExpressionNode {}