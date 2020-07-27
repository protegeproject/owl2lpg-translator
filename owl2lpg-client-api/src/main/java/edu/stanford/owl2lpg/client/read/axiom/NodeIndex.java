package edu.stanford.owl2lpg.client.read.axiom;

import org.neo4j.driver.types.Node;

import java.util.Collection;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface NodeIndex {

  Collection<Node> getNodes(String label);
  
  Node getEndNode(Node startNode, String relLabel);

  Collection<Node> getEndNodes(Node startNode, String relLabel);
}
