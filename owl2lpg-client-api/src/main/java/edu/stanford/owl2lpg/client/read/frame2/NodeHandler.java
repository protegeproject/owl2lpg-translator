package edu.stanford.owl2lpg.client.read.frame2;

import edu.stanford.owl2lpg.model.Node;

public interface NodeHandler<T> {

  /**
   * Returns the one label that this handler handles
   *
   * @return The label
   */
  String getLabel();

  T handle(Node node, NodeIndex nodeIndex);
}
