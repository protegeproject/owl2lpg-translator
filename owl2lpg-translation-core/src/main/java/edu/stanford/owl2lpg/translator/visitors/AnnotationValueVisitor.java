package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.translator.Translation;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class AnnotationValueVisitor extends VisitorBase
    implements OWLAnnotationValueVisitorEx<Translation> {

  @Nonnull
  private final OWLDataVisitorEx<Translation> dataVisitor;

  @Nonnull
  private final OWLIndividualVisitorEx<Translation> individualVisitor;

  @Inject
  public AnnotationValueVisitor(@Nonnull OWLDataVisitorEx<Translation> dataVisitor,
                                @Nonnull OWLIndividualVisitorEx<Translation> individualVisitor) {
    this.dataVisitor = checkNotNull(dataVisitor);
    this.individualVisitor = checkNotNull(individualVisitor);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull IRI iri) {
    return createIriTranslation(iri);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    return individualVisitor.visit(individual);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLLiteral lt) {
    return dataVisitor.visit(lt);
  }
}
