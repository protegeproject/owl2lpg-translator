package edu.stanford.owl2lpg.model;

import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class EdgeFactory {

  @Nonnull
  private final EdgeIdProvider edgeIdProvider;

  @Inject
  public EdgeFactory(@Nonnull EdgeIdProvider edgeIdProvider) {
    this.edgeIdProvider = checkNotNull(edgeIdProvider);
  }

  public Edge createEdge(@Nonnull Node startNode,
                         @Nonnull Node endNode,
                         @Nonnull EdgeLabel edgeLabel,
                         @Nonnull Properties properties) {
    var edgeId = edgeIdProvider.get(startNode, endNode, edgeLabel);
    return Edge.create(edgeId, startNode, endNode, edgeLabel, properties);
  }

  public Edge createEdge(@Nonnull Node startNode,
                         @Nonnull Node endNode,
                         @Nonnull EdgeLabel edgeLabel) {
    return createEdge(startNode, endNode, edgeLabel, Properties.empty());
  }
}
