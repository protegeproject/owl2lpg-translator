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
  public Edge getOntologyIriStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, ONTOLOGY_IRI);
  }

  @Nonnull
  public Edge getVersionIriStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, VERSION_IRI);
  }

  @Nonnull
  public Edge getBranchStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, BRANCH);
  }

  @Nonnull
  public Edge getOntologyDocumentStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, ONTOLOGY_DOCUMENT);
  }

  @Nonnull
  public Edge getAxiomStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, AXIOM);
  }

  @Nonnull
  public Edge getEntityIriStructuralEdge(@Nonnull Node entityNode, @Nonnull Node iriNode) {
    return getStructuralEdge(entityNode, iriNode, ENTITY_IRI);
  }

  @Nonnull
  public Edge getEntityStructuralEdge(@Nonnull Node axiomNode, @Nonnull Node entityNode) {
    return getStructuralEdge(axiomNode, entityNode, ENTITY);
  }

  @Nonnull
  public Edge getClassStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, CLASS);
  }

  @Nonnull
  public Edge getObjectPropertyStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, OBJECT_PROPERTY);
  }

  @Nonnull
  public Edge getIndividualStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, INDIVIDUAL);
  }

  @Nonnull
  public Edge getClassExpressionStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, CLASS_EXPRESSION);
  }

  @Nonnull
  public Edge getDisjointClassExpressionStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, DISJOINT_CLASS_EXPRESSION);
  }

  @Nonnull
  public Edge getSubClassExpressionStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUB_CLASS_EXPRESSION);
  }

  @Nonnull
  public Edge getSuperClassExpressionStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUPER_CLASS_EXPRESSION);
  }

  @Nonnull
  public Edge getDataPropertyExpressionStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, DATA_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getSubDataPropertyExpressionStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUB_DATA_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getSuperDataPropertyExpressionStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUPER_DATA_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getObjectPropertyExpressionStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, OBJECT_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getInverseObjectPropertyExpressionStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, INVERSE_OBJECT_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getSubObjectPropertyExpressionStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUB_OBJECT_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getSuperObjectPropertyExpressionStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUPER_OBJECT_PROPERTY_EXPRESSION);
  }

  @Nonnull
  public Edge getSubAnnotationPropertyStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUB_ANNOTATION_PROPERTY);
  }

  @Nonnull
  public Edge getSuperAnnotationPropertyStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SUPER_ANNOTATION_PROPERTY);
  }

  @Nonnull
  public Edge getNextStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, NEXT);
  }

  @Nonnull
  public Edge getLiteralStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, LITERAL);
  }

  @Nonnull
  public Edge getDataRangeStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, DATA_RANGE);
  }

  @Nonnull
  public Edge getDataTypeStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, DATATYPE);
  }

  @Nonnull
  public Edge getDomainStructuralEdge(@Nonnull Node axiomNode, @Nonnull Node domainNode) {
    return getStructuralEdge(axiomNode, domainNode, DOMAIN);
  }

  @Nonnull
  public Edge getRangeStructuralEdge(@Nonnull Node axiomNode, @Nonnull Node rangeNode) {
    return getStructuralEdge(axiomNode, rangeNode, RANGE);
  }

  @Nonnull
  public Edge getRestrictionStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, RESTRICTION);
  }

  @Nonnull
  public Edge getConstrainingFacetStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, CONSTRAINING_FACET);
  }

  @Nonnull
  public Edge getRestrictionValueStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, RESTRICTION_VALUE);
  }

  @Nonnull
  public Edge getOntologyAnnotationStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, ONTOLOGY_ANNOTATION);
  }

  @Nonnull
  public Edge getSourceIndividualStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, SOURCE_INDIVIDUAL);
  }

  @Nonnull
  public Edge getTargetIndividualStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode) {
    return getStructuralEdge(startNode, endNode, TARGET_INDIVIDUAL);
  }

  @Nonnull
  public Edge getTargetValueStructuralEdge(@Nonnull Node axiomNode, @Nonnull Node literalNode) {
    return getStructuralEdge(axiomNode, literalNode, TARGET_VALUE);
  }

  @Nonnull
  public Edge getAxiomAnnotationStructuralEdge(@Nonnull Node axiomNode, @Nonnull Node axiomAnnotationNode) {
    return getStructuralEdge(axiomNode, axiomAnnotationNode, AXIOM_ANNOTATION);
  }

  @Nonnull
  public Edge getAnnotationSubjectStructuralEdge(@Nonnull Node axiomNode, @Nonnull Node annotationSubjectNode) {
    return getStructuralEdge(axiomNode, annotationSubjectNode, ANNOTATION_SUBJECT);
  }

  @Nonnull
  public Edge getAnnotationPropertyStructuralEdge(@Nonnull Node axiomNode, @Nonnull Node annotationPropertyNode) {
    return getStructuralEdge(axiomNode, annotationPropertyNode, ANNOTATION_PROPERTY);
  }

  @Nonnull
  public Edge getAnnotationValueStructuralEdge(@Nonnull Node axiomNode, @Nonnull Node annotationValueNode) {
    return getStructuralEdge(axiomNode, annotationValueNode, ANNOTATION_VALUE);
  }

  @Nonnull
  public Edge getAnnotationAnnotationStructuralEdge(@Nonnull Node startNode, @Nonnull Node annotationNode) {
    return getStructuralEdge(startNode, annotationNode, ANNOTATION_ANNOTATION);
  }

  @Nonnull
  public Edge getStructuralEdge(@Nonnull Node startNode, @Nonnull Node endNode, @Nonnull EdgeLabel edgeLabel) {
    return edgeFactory.createEdge(startNode,
        endNode,
        edgeLabel,
        Properties.of(PropertyFields.STRUCTURAL_SPEC, true));
  }
}
