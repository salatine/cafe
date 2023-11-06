package tcc.nodes;

public sealed interface StatementNode permits DeclarationNode, ExpressionNode, PrintCallNode {}