package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Translation;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A translator to translate the OWL 2 individuals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class IndividualTranslator {

  @Nonnull
  private final Provider<OWLIndividualVisitorEx<Translation>> visitor;

  @Inject
  public IndividualTranslator(@Nonnull Provider<OWLIndividualVisitorEx<Translation>> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(OWLIndividual a) {
    checkNotNull(a);
    return a.accept(visitor.get());
  }

}
