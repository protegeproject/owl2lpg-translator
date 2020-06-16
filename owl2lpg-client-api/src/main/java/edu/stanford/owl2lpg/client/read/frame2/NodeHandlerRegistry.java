package edu.stanford.owl2lpg.client.read.frame2;

public interface NodeHandlerRegistry {

  <T> NodeHandler<T> getHandler(String nodeLabel);
}
