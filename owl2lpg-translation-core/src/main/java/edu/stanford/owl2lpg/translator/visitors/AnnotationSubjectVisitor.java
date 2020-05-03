package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLObject;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public class AnnotationSubjectVisitor extends VisitorBase
    implements OWLAnnotationSubjectVisitorEx<Translation> {

  private final VisitorFactory visitorFactory;

  protected AnnotationSubjectVisitor(@Nonnull VisitorFactory visitorFactory) {
    super(visitorFactory.getNodeIdMapper());
    this.visitorFactory = checkNotNull(visitorFactory);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull IRI iri) {
    checkNotNull(iri);
    return visitorFactory.createAnnotationValueVisitor().visit(iri);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    checkNotNull(individual);
    return visitorFactory.createAnnotationValueVisitor().visit(individual);
  }

  @Nonnull
  @Override
  protected Node getMainNode() {
    throw new IllegalArgumentException("Implementation error");
  }

  @Override
  protected Translation getTranslation(@Nonnull OWLObject anyObject) {
    throw new IllegalArgumentException("Implementation error");
  }
}
