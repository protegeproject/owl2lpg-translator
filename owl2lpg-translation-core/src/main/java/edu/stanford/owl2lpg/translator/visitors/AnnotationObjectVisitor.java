package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A visitor that contains the implementation to translate the OWL 2 annotations.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationObjectVisitor extends VisitorBase
    implements OWLAnnotationObjectVisitorEx<Translation> {

  @Nonnull
  private final OWLEntityVisitorEx<Translation> entityVisitor;

  @Nonnull
  private final OWLAnnotationValueVisitorEx<Translation> annotationValueVisitor;

  private Node mainNode;

  public AnnotationObjectVisitor(@Nonnull OWLEntityVisitorEx<Translation> entityVisitor,
                                 @Nonnull OWLAnnotationValueVisitorEx<Translation> annotationValueVisitor) {
    this.entityVisitor = checkNotNull(entityVisitor);
    this.annotationValueVisitor = checkNotNull(annotationValueVisitor);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotation annotation) {
    mainNode = createNode(annotation, NodeLabels.ANNOTATION);
    var annotationPropertyEdge = createEdge(annotation.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    var annotationPropertyTranslation = createNestedTranslation(annotation.getProperty());
    var annotationValueEdge = createEdge(annotation.getValue(), EdgeLabels.ANNOTATION_VALUE);
    var annotationValueTranslation = createNestedTranslation(annotation.getValue());
    return Translation.create(mainNode,
        ImmutableList.of(annotationPropertyEdge, annotationValueEdge),
        ImmutableList.of(annotationPropertyTranslation, annotationValueTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull IRI iri) {
    return annotationValueVisitor.visit(iri);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    return annotationValueVisitor.visit(individual);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLLiteral literal) {
    return annotationValueVisitor.visit(literal);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
    throw new UnsupportedOperationException("Use the AxiomVisitor to visit OWLAnnotationAssertionAxiom");
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    throw new UnsupportedOperationException("Use the AxiomVisitor to visit OWLSubAnnotationPropertyOfAxiom");
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
    throw new UnsupportedOperationException("Use the AxiomVisitor to visit OWLAnnotationPropertyDomainAxiom");
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
    throw new UnsupportedOperationException("Use the AxiomVisitor to visit OWLAnnotationPropertyRangeAxiom");
  }

  @Override
  protected Node getMainNode() {
    return mainNode;
  }

  @Override
  protected Translation getTranslation(@Nonnull OWLObject anyObject) {
    checkNotNull(anyObject);
    if (anyObject instanceof OWLAnnotationProperty) {
      return createAnnotationPropertyTranslation((OWLAnnotationProperty) anyObject);
    } else if (anyObject instanceof OWLAnnotationValue) {
      return createAnnotationValueTranslation((OWLAnnotationValue) anyObject);
    }
    throw new IllegalArgumentException("Implementation error");
  }

  private Translation createAnnotationPropertyTranslation(OWLAnnotationProperty annotationProperty) {
    return annotationProperty.accept(entityVisitor);
  }

  private Translation createAnnotationValueTranslation(OWLAnnotationValue value) {
    return value.accept(this);
  }
}
