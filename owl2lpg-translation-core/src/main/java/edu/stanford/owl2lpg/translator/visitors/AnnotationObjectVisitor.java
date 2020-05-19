package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
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

  private final VisitorFactory visitorFactory;

  private Node mainNode;

  public AnnotationObjectVisitor(@Nonnull VisitorFactory visitorFactory) {
    super(visitorFactory.getNodeIdMapper());
    this.visitorFactory = checkNotNull(visitorFactory);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotation annotation) {
    mainNode = createNode(annotation, NodeLabels.ANNOTATION);
    var annotationPropertyEdge = createEdge(annotation.getProperty(), EdgeLabel.ANNOTATION_PROPERTY);
    var annotationPropertyTranslation = createNestedTranslation(annotation.getProperty());
    var annotationValueEdge = createEdge(annotation.getValue(), EdgeLabel.ANNOTATION_VALUE);
    var annotationValueTranslation = createNestedTranslation(annotation.getValue());
    var annotationAnnotationEdges = createEdges(annotation.getAnnotations(), EdgeLabel.ANNOTATION_ANNOTATION);
    var annotationAnnotationTranslations = createNestedTranslations(annotation.getAnnotations());
    var allEdges = Lists.newArrayList(annotationPropertyEdge, annotationValueEdge);
    allEdges.addAll(annotationAnnotationEdges);
    var allNestedTranslations = Lists.newArrayList(annotationPropertyTranslation, annotationValueTranslation);
    allNestedTranslations.addAll(annotationAnnotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull IRI iri) {
    return visitorFactory.createAnnotationValueVisitor().visit(iri);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    return visitorFactory.createIndividualVisitor().visit(individual);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLLiteral literal) {
    return visitorFactory.createDataVisitor().visit(literal);
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
    if (anyObject instanceof OWLAnnotation) {
      return getAnnotationTranslation((OWLAnnotation) anyObject);
    } else if (anyObject instanceof OWLAnnotationProperty) {
      return getAnnotationPropertyTranslation((OWLAnnotationProperty) anyObject);
    } else if (anyObject instanceof OWLAnnotationValue) {
      return getAnnotationValueTranslation((OWLAnnotationValue) anyObject);
    }
    throw new IllegalArgumentException("Implementation error");
  }

  private Translation getAnnotationTranslation(OWLAnnotation annotation) {
    var annotationVisitor = visitorFactory.createAnnotationObjectVisitor();
    return annotation.accept(annotationVisitor);
  }

  private Translation getAnnotationPropertyTranslation(OWLAnnotationProperty annotationProperty) {
    var entityVisitor = visitorFactory.createEntityVisitor();
    return annotationProperty.accept(entityVisitor);
  }

  private Translation getAnnotationValueTranslation(OWLAnnotationValue value) {
    var annotationObjectVisitor = visitorFactory.createAnnotationObjectVisitor();
    return value.accept(annotationObjectVisitor);
  }
}
