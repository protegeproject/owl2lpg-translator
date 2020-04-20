package edu.stanford.owl2lpg.exporter.csv.beans;

import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NodeIdProvider {

  @Nonnull
  private final Node node;

  public NodeIdProvider(@Nonnull Node node) {
    this.node = checkNotNull(node);
  }

  public String getId() {
    return format("NodeId_%d", node.getNodeId());
  }
}
