package edu.stanford.owl2lpg.client.read;

import edu.stanford.owl2lpg.client.read.handlers.NodeHandler;

public interface NodeHandlerRegistry {

  <T> NodeHandler<T> getHandler(String nodeLabel);
}
