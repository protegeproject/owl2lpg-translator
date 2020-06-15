package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AugmentedEdgeInclusionCheckerImpl implements AugmentedEdgeInclusionChecker {

  @Override
  public boolean allows(EdgeLabel edgeLabel) {
    return AXIOM_SUBJECT.equals(edgeLabel)
        || RELATED_TO.equals(edgeLabel)
        || SUB_CLASS_OF.equals(edgeLabel)
        || SUB_OBJECT_PROPERTY_OF.equals(edgeLabel)
        || SUB_DATA_PROPERTY_OF.equals(edgeLabel)
        || SUB_ANNOTATION_PROPERTY_OF.equals(edgeLabel)
        || DOMAIN.equals(edgeLabel)
        || RANGE.equals(edgeLabel)
        || TYPE.equals(edgeLabel)
        || SAME_INDIVIDUAL.equals(edgeLabel)
        || INVERSE_OF.equals(edgeLabel);
  }
}
