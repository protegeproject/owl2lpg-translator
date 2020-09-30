package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.OntologyContext;
import edu.stanford.owl2lpg.model.OntologyContextVisitorEx;
import edu.stanford.owl2lpg.model.Translation;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyContextTranslator {

  @Nonnull
  private final Provider<OntologyContextVisitorEx<Translation>> visitor;

  @Inject
  public OntologyContextTranslator(@Nonnull Provider<OntologyContextVisitorEx<Translation>> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(@Nonnull OntologyContext ontologyContext) {
    checkNotNull(ontologyContext);
    return ontologyContext.accept(visitor.get());
  }
}
