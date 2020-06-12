package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AugmentedEdgeInclusionChecker {

  boolean allows(EdgeLabel edgeLabel);
}
