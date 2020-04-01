package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * A translator to translate the OWL 2 data range expressions.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataRangeTranslator {

  @Nonnull
  private final OWLDataVisitorEx<Translation> visitor;

  @Inject
  public DataRangeTranslator(OWLDataVisitorEx<Translation> visitor) {
    this.visitor = visitor;
  }

  @Nonnull
  public Translation visit(OWLDataRange dr) {
    return dr.accept(visitor);
  }
}
