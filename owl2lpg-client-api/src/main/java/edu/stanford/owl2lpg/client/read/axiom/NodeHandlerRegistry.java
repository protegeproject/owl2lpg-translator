package edu.stanford.owl2lpg.client.read.axiom;

public interface NodeHandlerRegistry {

  <T> NodeHandler<T> getHandler(String nodeLabel);
}
