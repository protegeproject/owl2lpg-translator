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
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;

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
  public Edge getOntologyIriEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, ONTOLOGY_IRI);
  }

  @Nonnull
  public Edge getVersionIriEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, VERSION_IRI);
  }

  @Nonnull
  public Edge getBranchEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, BRANCH);
  }

  @Nonnull
  public Edge getOntologyDocumentEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, ONTOLOGY_DOCUMENT);
  }

  @Nonnull
  public Edge getAxiomEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, AXIOM);
  }

  @Nonnull
  public Edge getEntityIriEdge(@Nonnull Node entityNode, @Nonnull Node iriNode) {
    return getStructuralEdge(entityNode, iriNode, ENTITY_IRI);
  }

  @Nonnull
  public Edge getEntityEdge(@Nonnull Node axiomNode, @Nonnull Node entityNode) {
    return getStructuralEdge(axiomNode, entityNode, ENTITY);
  }

  @Nonnull
  public Edge getClassEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, CLASS);
  }

  @Nonnull
  public Edge getObjectPropertyEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, OBJECT_PROPERTY);
  }

  @Nonnull
  public Edge getIndividualEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, INDIVIDUAL);
  }

  @Nonnull
  public Edge getClassExpressionEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, CLASS_EXPRESSION);
  }

  @Nonnull
  public Edge getDisjointClassExpressionEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, DISJOINT_CLASS_EXPRESSION);
  }

  @Nonnull
  public Edge getSubClassExpressionEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUB_CLASS_EXPRESSION);
  }

  @Nonnull
  public Edge getSuperClassExpressionEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUPER_CLASS_EXPRESSION);
  }

  @Nonnull
  public Edge getDataPropertyExpressionEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, DATA_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getSubDataPropertyExpressionEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUB_DATA_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getSuperDataPropertyExpressionEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUPER_DATA_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getObjectPropertyExpressionEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, OBJECT_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getObjectPropertyExpressionEdge(@Nonnull Node startNode, @Nonnull Node endNode, int orderPosition) {
    return getStructuralEdge(startNode, endNode, OBJECT_PROPERTY_EXPRESSION, orderPosition);
  }

  @Nonnull
  public Edge getInverseObjectPropertyExpressionEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, INVERSE_OBJECT_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getSubObjectPropertyExpressionEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUB_OBJECT_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getSuperObjectPropertyExpressionEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUPER_OBJECT_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getSubAnnotationPropertyEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUB_ANNOTATION_PROPERTY);
  }

  @Nonnull
  public Edge getSuperAnnotationPropertyEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUPER_ANNOTATION_PROPERTY);
  }

  @Nonnull
  public Edge getLiteralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, LITERAL);
  }

  @Nonnull
  public Edge getDataRangeEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, DATA_RANGE);
  }

  @Nonnull
  public Edge getDataTypeEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, DATATYPE);
  }

  @Nonnull
  public Edge getDomainEdge(@Nonnull Node axiomNode, @Nonnull Node domainNode) {
    return getStructuralEdge(axiomNode, domainNode, DOMAIN);
  }

  @Nonnull
  public Edge getRangeEdge(@Nonnull Node axiomNode, @Nonnull Node rangeNode) {
    return getStructuralEdge(axiomNode, rangeNode, RANGE);
  }

  @Nonnull
  public Edge getRestrictionEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, RESTRICTION);
  }

  @Nonnull
  public Edge getConstrainingFacetEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, CONSTRAINING_FACET);
  }

  @Nonnull
  public Edge getRestrictionValueEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, RESTRICTION_VALUE);
  }

  @Nonnull
  public Edge getOntologyAnnotationEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, ONTOLOGY_ANNOTATION);
  }

  @Nonnull
  public Edge getSourceIndividualEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SOURCE_INDIVIDUAL);
  }

  @Nonnull
  public Edge getTargetIndividualEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, TARGET_INDIVIDUAL);
  }

  @Nonnull
  public Edge getTargetValueEdge(@Nonnull Node axiomNode, @Nonnull Node literalNode) {
    return getStructuralEdge(axiomNode, literalNode, TARGET_VALUE);
  }

  @Nonnull
  public Edge getAxiomAnnotationEdge(@Nonnull Node axiomNode, @Nonnull Node axiomAnnotationNode) {
    return getStructuralEdge(axiomNode, axiomAnnotationNode, AXIOM_ANNOTATION);
  }

  @Nonnull
  public Edge getAnnotationSubjectEdge(@Nonnull Node axiomNode, @Nonnull Node annotationSubjectNode) {
    return getStructuralEdge(axiomNode, annotationSubjectNode, ANNOTATION_SUBJECT);
  }

  @Nonnull
  public Edge getAnnotationPropertyEdge(@Nonnull Node axiomNode, @Nonnull Node annotationPropertyNode) {
    return getStructuralEdge(axiomNode, annotationPropertyNode, ANNOTATION_PROPERTY);
  }

  @Nonnull
  public Edge getAnnotationValueEdge(@Nonnull Node axiomNode, @Nonnull Node annotationValueNode) {
    return getStructuralEdge(axiomNode, annotationValueNode, ANNOTATION_VALUE);
  }

  @Nonnull
  public Edge getAnnotationAnnotationEdge(@Nonnull Node startNode, @Nonnull Node annotationNode) {
    return getStructuralEdge(startNode, annotationNode, ANNOTATION_ANNOTATION);
  }

  @Nonnull
  public Edge getStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode, @Nonnull EdgeLabel edgeLabel) {
    return edgeFactory.createEdge(startNode,
        endNode,
        edgeLabel,
        Properties.of(PropertyFields.STRUCTURAL_SPEC, true));
  }

  @Nonnull
  public Edge getStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode, @Nonnull EdgeLabel edgeLabel, int orderPosition) {
    return edgeFactory.createEdge(startNode,
        endNode,
        edgeLabel,
        Properties.of(PropertyFields.STRUCTURAL_SPEC, true, PropertyFields.POS, orderPosition));
  }
}
