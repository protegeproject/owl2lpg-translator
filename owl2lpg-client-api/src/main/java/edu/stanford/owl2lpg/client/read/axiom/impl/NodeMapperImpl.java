package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.axiom.NodeHandlerRegistry;
import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.client.read.axiom.handlers.NodeHandler;
import org.neo4j.driver.types.Node;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NodeMapperImpl implements NodeMapper {

  @Nonnull
  private final NodeHandlerRegistry registry;

  @Inject
  public NodeMapperImpl(@Nonnull NodeHandlerRegistry registry) {
    this.registry = checkNotNull(registry);
  }

  @Override
  public <T> T toObject(Node node, NodeIndex nodeIndex, Class<T> type) {
    var handler = getHandler(node, type);
    return handler.handle(node, nodeIndex, this);
  }

  @Override
  public <T> Set<T> toObjects(Collection<Node> nodes, NodeIndex nodeIndex, Class<T> type) {
    return nodes.stream()
        .map(toNode -> toObject(toNode, nodeIndex, type))
        .collect(ImmutableSet.toImmutableSet());
  }

  @SuppressWarnings("unchecked")
  private <T> NodeHandler<T> getHandler(Node node, Class<T> type) {
    var it = node.labels();
    return (NodeHandler<T>) StreamSupport.stream(it.spliterator(), false)
        .map(registry::getHandler)
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("No handler for " + it + " present."));
  }
}
