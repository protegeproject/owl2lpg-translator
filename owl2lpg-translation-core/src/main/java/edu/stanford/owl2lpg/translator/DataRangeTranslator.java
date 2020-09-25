package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.visitors.DataVisitor;
import org.semanticweb.owlapi.model.OWLDataRange;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A translator to translate the OWL 2 data range expressions.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataRangeTranslator {

  @Nonnull
  private final Provider<DataVisitor> visitor;

  @Inject
  public DataRangeTranslator(Provider<DataVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(OWLDataRange dr) {
    checkNotNull(dr);
    return dr.accept(visitor.get());
  }
}
