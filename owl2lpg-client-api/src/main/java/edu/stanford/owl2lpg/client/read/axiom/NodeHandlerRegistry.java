package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.owl2lpg.client.read.axiom.handlers.NodeHandler;

public interface NodeHandlerRegistry {

  <T> NodeHandler<T> getHandler(String nodeLabel);
}
