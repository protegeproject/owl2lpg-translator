package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.NodeId;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface NodeIdProvider {

  NodeId getId(Object o);
}
