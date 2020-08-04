package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.EdgeId;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface EdgeIdProvider {

  EdgeId get(Node startId, Node endNode, EdgeLabel edgeLabel);
}
