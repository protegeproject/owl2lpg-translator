package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.EdgeFactory;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.translator.*;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;

/**
 * A visitor that contains the implementation to translate the OWL 2 annotations.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@TranslationSessionScope
public class AnnotationObjectVisitor implements OWLAnnotationObjectVisitorEx<Translation> {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Nonnull
  private final PropertyExpressionTranslator propertyExprTranslator;

  @Nonnull
  private final AnnotationValueTranslator annotationValueTranslator;

  @Nonnull
  private final AnnotationObjectTranslator annotationObjectTranslator;

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Inject
  public AnnotationObjectVisitor(@Nonnull NodeFactory nodeFactory,
                                 @Nonnull EdgeFactory edgeFactory,
                                 @Nonnull PropertyExpressionTranslator propertyExprTranslator,
                                 @Nonnull AnnotationValueTranslator annotationValueTranslator,
                                 @Nonnull AnnotationObjectTranslator annotationObjectTranslator,
                                 @Nonnull AxiomTranslator axiomTranslator) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
    this.propertyExprTranslator = checkNotNull(propertyExprTranslator);
    this.annotationValueTranslator = checkNotNull(annotationValueTranslator);
    this.annotationObjectTranslator = checkNotNull(annotationObjectTranslator);
    this.axiomTranslator = checkNotNull(axiomTranslator);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotation annotation) {
    var mainNode = nodeFactory.createNode(annotation, NodeLabels.ANNOTATION);
    var translations = new ImmutableList.Builder<Translation>();
    var edges = new ImmutableList.Builder<Edge>();
    var annotationPropertyTranslation = propertyExprTranslator.translate(annotation.getProperty());
    translations.add(annotationPropertyTranslation);
    edges.add(edgeFactory.createEdge(mainNode,
        annotationPropertyTranslation.getMainNode(),
        ANNOTATION_PROPERTY));
    var annotationValueTranslation = annotationValueTranslator.translate(annotation.getValue());
    translations.add(annotationValueTranslation);
    edges.add(edgeFactory.createEdge(mainNode,
        annotationValueTranslation.getMainNode(),
        ANNOTATION_VALUE));
    for (var ann : annotation.getAnnotations()) {
      var translation = annotationObjectTranslator.translate(ann);
      translations.add(translation);
      edges.add(edgeFactory.createEdge(mainNode,
          translation.getMainNode(),
          ANNOTATION_ANNOTATION));
    }
    return Translation.create(annotation, mainNode,
        edges.build(),
        translations.build());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull IRI iri) {
    return annotationValueTranslator.translate(iri);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    return annotationValueTranslator.translate(individual);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLLiteral literal) {
    return annotationValueTranslator.translate(literal);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
    return axiomTranslator.translate(axiom);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    return axiomTranslator.translate(axiom);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
    return axiomTranslator.translate(axiom);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
    return axiomTranslator.translate(axiom);
  }
}
