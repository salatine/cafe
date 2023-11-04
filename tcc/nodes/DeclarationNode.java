package tcc.nodes;

import tcc.DataType;

import java.util.Optional;

public record DeclarationNode(DataType type, IdentifierNode identifier, Optional<ExpressionNode> expression) implements Node {}