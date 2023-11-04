package tcc.nodes;

public sealed interface ExpressionNode extends Node permits IntNode, DoubleNode, IdentifierNode, BinaryExpressionNode, AssignmentNode {}