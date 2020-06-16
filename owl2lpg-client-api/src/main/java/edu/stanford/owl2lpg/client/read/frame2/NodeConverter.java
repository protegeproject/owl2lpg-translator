package edu.stanford.owl2lpg.client.read.frame2;

import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkNotNull;
import static dagger.internal.codegen.extension.DaggerStreams.toImmutableSet;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NodeConverter {

  @Nonnull
  private final NodeHandlerRegistry registry;

  @Inject
  public NodeConverter(@Nonnull NodeHandlerRegistry registry) {
    this.registry = checkNotNull(registry);
  }

  public <T> T toObject(Node node, String relLabel, NodeIndex nodeIndex, Class<T> type) {
    var toNode = nodeIndex.getToNode(node, relLabel);
    if (toNode == null) {
      throw new RuntimeException("Expected " + relLabel + " relationship, but not found");
    }
    return toObject(toNode, nodeIndex, type);
  }

  public <T> Set<T> toObjects(Node node, String relLabel, NodeIndex nodeIndex, Class<T> type) {
    var nodes = nodeIndex.getToNodes(node, relLabel);
    return nodes.stream()
        .map(toNode -> toObject(toNode, nodeIndex, type))
        .collect(toImmutableSet());
  }

  private <T> T toObject(Node node, NodeIndex nodeIndex, Class<T> type) {
    var handler = getHandler(node, type);
    return handler.handle(node, nodeIndex);
  }

  @SuppressWarnings("unchecked")
  private <T> NodeHandler<T> getHandler(Node node, Class<T> type) {
    var it = node.getLabelsList();
    return (NodeHandler<T>) StreamSupport.stream(it.spliterator(), false)
        .map(label -> registry.getHandler(label))
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow();
  }
}
