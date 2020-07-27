package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableMap;
import edu.stanford.owl2lpg.client.read.axiom.handlers.NodeHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NodeHandlerRegistryImpl implements NodeHandlerRegistry {

  @Nonnull
  private final ImmutableMap<String, NodeHandler<?>> nodeHandlerMap;

  @Inject
  public NodeHandlerRegistryImpl(@Nonnull Set<NodeHandler<?>> nodeHandlers) {
    this.nodeHandlerMap = nodeHandlers.stream()
        .collect(ImmutableMap.toImmutableMap(
            NodeHandler::getLabel, handler -> handler
        ));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> NodeHandler<T> getHandler(String nodeLabel) {
    return (NodeHandler<T>) nodeHandlerMap.get(nodeLabel);
  }
}
