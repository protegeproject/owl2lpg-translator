package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.translator.Translation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class AnnotationSubjectVisitor extends VisitorBase
    implements OWLAnnotationSubjectVisitorEx<Translation> {

  @Nonnull
  private final OWLIndividualVisitorEx<Translation> individualVisitor;

  @Inject
  public AnnotationSubjectVisitor(@Nonnull OWLIndividualVisitorEx<Translation> individualVisitor) {
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
}
