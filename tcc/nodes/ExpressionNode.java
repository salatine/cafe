package tcc.nodes;

public sealed interface ExpressionNode extends StatementNode permits IntNode, DoubleNode, IdentifierNode, BinaryExpressionNode, AssignmentNode {}