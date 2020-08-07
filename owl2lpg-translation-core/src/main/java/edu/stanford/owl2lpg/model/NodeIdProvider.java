package edu.stanford.owl2lpg.model;

import edu.stanford.owl2lpg.model.NodeId;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface NodeIdProvider {

  NodeId getId(Object o);
}
