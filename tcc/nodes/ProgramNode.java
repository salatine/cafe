package tcc.nodes;

import java.util.List;

public record ProgramNode(List<Node> statements) implements Node {}