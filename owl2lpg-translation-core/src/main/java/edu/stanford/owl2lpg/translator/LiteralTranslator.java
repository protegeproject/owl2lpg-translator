package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A translator to translate the OWL 2 literal.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class LiteralTranslator {

  @Nonnull
  private final OWLDataVisitorEx<Translation> visitor;

  @Inject
  public LiteralTranslator(@Nonnull OWLDataVisitorEx<Translation> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(@Nonnull OWLLiteral lt) {
    checkNotNull(lt);
    return lt.accept(visitor);
  }
}
