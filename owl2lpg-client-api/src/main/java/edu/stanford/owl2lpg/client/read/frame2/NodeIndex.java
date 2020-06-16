package edu.stanford.owl2lpg.client.read.frame2;

import edu.stanford.owl2lpg.model.Node;

import java.util.Collection;

public interface NodeIndex {

  Collection<Node> getStartNodes();

  Node getToNode(Node fromNode, String label);

  Collection<Node> getToNodes(Node fromNode, String label);
}
