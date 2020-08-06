package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.translator.AnnotationObjectTranslator;
import edu.stanford.owl2lpg.translator.AnnotationValueTranslator;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.PropertyExpressionTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

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
  private final StructuralEdgeFactory structuralEdgeFactory;

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
                                 @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                                 @Nonnull PropertyExpressionTranslator propertyExprTranslator,
                                 @Nonnull AnnotationValueTranslator annotationValueTranslator,
                                 @Nonnull AnnotationObjectTranslator annotationObjectTranslator,
                                 @Nonnull AxiomTranslator axiomTranslator) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
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
    edges.add(structuralEdgeFactory.getAnnotationPropertyStructuralEdge(mainNode, annotationPropertyTranslation.getMainNode()));
    var annotationValueTranslation = annotationValueTranslator.translate(annotation.getValue());
    translations.add(annotationValueTranslation);
    edges.add(structuralEdgeFactory.getAnnotationValueStructuralEdge(mainNode, annotationValueTranslation.getMainNode()));
    for (var ann : annotation.getAnnotations()) {
      var translation = annotationObjectTranslator.translate(ann);
      translations.add(translation);
      edges.add(structuralEdgeFactory.getAnnotationAnnotationStructuralEdge(mainNode, translation.getMainNode()));
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
