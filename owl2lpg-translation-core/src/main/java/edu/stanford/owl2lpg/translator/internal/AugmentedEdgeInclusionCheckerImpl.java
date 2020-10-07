package edu.stanford.owl2lpg.translator.internal;

import edu.stanford.owl2lpg.model.AugmentedEdgeInclusionChecker;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

import javax.inject.Inject;

import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.AXIOM_SUBJECT;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY_SIGNATURE_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.HAS_DOMAIN;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.HAS_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.INVERSE_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.RELATED_TO;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SAME_INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_ANNOTATION_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_CLASS_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_DATA_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_OBJECT_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.TYPE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AugmentedEdgeInclusionCheckerImpl implements AugmentedEdgeInclusionChecker {

  @Inject
  public AugmentedEdgeInclusionCheckerImpl() {
  }

  @Override
  public boolean allows(EdgeLabel edgeLabel) {
    return ENTITY_SIGNATURE_OF.equals(edgeLabel)
        || AXIOM_SUBJECT.equals(edgeLabel)
        || RELATED_TO.equals(edgeLabel)
        || SUB_CLASS_OF.equals(edgeLabel)
        || SUB_OBJECT_PROPERTY_OF.equals(edgeLabel)
        || SUB_DATA_PROPERTY_OF.equals(edgeLabel)
        || SUB_ANNOTATION_PROPERTY_OF.equals(edgeLabel)
        || HAS_DOMAIN.equals(edgeLabel)
        || HAS_RANGE.equals(edgeLabel)
        || TYPE.equals(edgeLabel)
        || SAME_INDIVIDUAL.equals(edgeLabel)
        || INVERSE_OF.equals(edgeLabel);
  }
}
