package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import org.neo4j.driver.types.Node;

public interface NodeHandler<T> {

  /**
   * Returns the one label that this handler handles
   *
   * @return The label
   */
  String getLabel();

  T handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper);
}
