package edu.stanford.owl2lpg.client.read.frame2;

import org.neo4j.driver.types.Node;

import java.util.Collection;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface NodeMapper {

  <T> T toObject(Node node, NodeIndex nodeIndex, Class<T> type);

  <T> Set<T> toObjects(Collection<Node> nodes, NodeIndex nodeIndex, Class<T> type);
}
