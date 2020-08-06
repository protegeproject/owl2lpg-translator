package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.EdgeFactory;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ANNOTATION_SUBJECT;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ANNOTATION_VALUE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.AXIOM_ANNOTATION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DOMAIN;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY_IRI;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.RANGE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.TARGET_VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class StructuralEdgeFactory {

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Inject
  public StructuralEdgeFactory(@Nonnull EdgeFactory edgeFactory) {
    this.edgeFactory = checkNotNull(edgeFactory);
  }

  @Nonnull
  public Edge getEntityIriStructuralEdge(@Nonnull Node entityNode,
                                         @Nonnull Node iriNode) {
    return getStructuralEdge(entityNode, iriNode, ENTITY_IRI);
  }

  @Nonnull
  public Edge getEntityStructuralEdge(@Nonnull Node axiomNode,
                                      @Nonnull Node entityNode,
                                      @Nonnull EdgeLabel edgeLabel) {
    return getStructuralEdge(axiomNode, entityNode, edgeLabel);
  }

  @Nonnull
  public Edge getIndividualStructuralEdge(@Nonnull Node axiomNode,
                                          @Nonnull Node individualNode,
                                          @Nonnull EdgeLabel edgeLabel) {
    return getStructuralEdge(axiomNode, individualNode, edgeLabel);
  }

  @Nonnull
  public Edge getClassExprStructuralEdge(@Nonnull Node axiomNode,
                                         @Nonnull Node classExprNode,
                                         @Nonnull EdgeLabel edgeLabel) {
    return getStructuralEdge(axiomNode, classExprNode, edgeLabel);
  }

  @Nonnull
  public Edge getPropertyExprStructuralEdge(@Nonnull Node axiomNode,
                                            @Nonnull Node propertyExprNode,
                                            @Nonnull EdgeLabel edgeLabel) {
    return getStructuralEdge(axiomNode, propertyExprNode, edgeLabel);
  }

  @Nonnull
  public Edge getTargetValueStructuralEdge(@Nonnull Node axiomNode,
                                           @Nonnull Node literalNode) {
    return getStructuralEdge(axiomNode, literalNode, TARGET_VALUE);
  }

  @Nonnull
  public Edge getAxiomAnnotationStructuralEdge(@Nonnull Node axiomNode,
                                               @Nonnull Node axiomAnnotationNode) {
    return getStructuralEdge(axiomNode, axiomNode, AXIOM_ANNOTATION);
  }

  @Nonnull
  public Edge getAnnotationSubjectStructuralEdge(@Nonnull Node axiomNode,
                                                 @Nonnull Node annotationSubjectNode) {
    return getStructuralEdge(axiomNode, annotationSubjectNode, ANNOTATION_SUBJECT);
  }

  @Nonnull
  public Edge getAnnotationValueStructuralEdge(@Nonnull Node axiomNode,
                                               @Nonnull Node annotationValueNode) {
    return getStructuralEdge(axiomNode, annotationValueNode, ANNOTATION_VALUE);
  }

  @Nonnull
  public Edge getDomainStructuralEdge(@Nonnull Node axiomNode,
                                      @Nonnull Node domainNode) {
    return getStructuralEdge(axiomNode, domainNode, DOMAIN);
  }

  @Nonnull
  public Edge getRangeStructuralEdge(@Nonnull Node axiomNode,
                                     @Nonnull Node rangeNode) {
    return getStructuralEdge(axiomNode, rangeNode, RANGE);
  }

  @Nonnull
  public Edge getStructuralEdge(@Nonnull Node startNode,
                                @Nonnull Node endNode,
                                @Nonnull EdgeLabel edgeLabel) {
    return edgeFactory.createEdge(startNode,
        endNode,
        edgeLabel,
        Properties.of(PropertyFields.STRUCTURAL_SPEC, true));
  }
}
