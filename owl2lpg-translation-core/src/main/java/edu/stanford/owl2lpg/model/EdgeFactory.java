package edu.stanford.owl2lpg.model;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;

public class EdgeFactory {

  public Edge createEdge(@Nonnull Node fromNode,
                         @Nonnull Node toNode,
                         @Nonnull EdgeLabel edgeLabel,
                         @Nonnull Properties properties) {
    return Edge.create(fromNode, toNode, edgeLabel, properties);
  }

  public Edge createEdge(@Nonnull Node fromNode,
                         @Nonnull Node toNode,
                         @Nonnull EdgeLabel edgeLabel) {
    return Edge.create(fromNode, toNode, edgeLabel);
  }

  public Collection<Edge> createEdges(@Nonnull Node fromNode,
                                      @Nonnull Set<Node> toNodes,
                                      @Nonnull EdgeLabel edgeLabel) {
    return toNodes.stream()
        .map(node -> createEdge(fromNode, node, edgeLabel))
        .collect(ImmutableList.toImmutableList());
  }
}
