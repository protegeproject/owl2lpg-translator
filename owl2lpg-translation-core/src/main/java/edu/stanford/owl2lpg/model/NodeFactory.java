package edu.stanford.owl2lpg.model;

import edu.stanford.owl2lpg.translator.vocab.NodeLabels;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

@Deprecated
public class NodeFactory {

  @Nonnull
  private final NodeIdProvider nodeIdProvider;

  @Inject
  public NodeFactory(@Nonnull NodeIdProvider nodeIdProvider) {
    this.nodeIdProvider = checkNotNull(nodeIdProvider);
  }

  @Nonnull
  public Node createNode(@Nonnull Object anyObject,
                         @Nonnull NodeLabels nodeLabels,
                         @Nonnull Properties properties) {
    checkNotNull(anyObject);
    var nodeId = nodeIdProvider.getId(anyObject);
    return Node.create(nodeId, nodeLabels, properties);
  }

  @Nonnull
  public Node createNode(@Nonnull Object object,
                         @Nonnull NodeLabels nodeLabels) {
    return createNode(object, nodeLabels, Properties.empty());
  }
}
