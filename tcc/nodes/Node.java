package tcc.nodes;

public sealed interface Node permits DeclarationNode, ExpressionNode, PrintCallNode, ProgramNode {}