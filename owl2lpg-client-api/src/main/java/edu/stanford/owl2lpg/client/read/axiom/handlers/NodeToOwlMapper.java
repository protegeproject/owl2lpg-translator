package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.*;

import javax.inject.Inject;
import java.util.Set;

import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NodeToOwlMapper {

  @Inject
  public NodeToOwlMapper() {
  }

  public OWLClassExpression toClassExpr(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var endNode = nodeIndex.getEndNode(mainNode, CLASS_EXPRESSION.name());
    return nodeMapper.toObject(endNode, nodeIndex, OWLClassExpression.class);
  }

  public OWLClassExpression toSubClassExpr(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var endNode = nodeIndex.getEndNode(mainNode, SUB_CLASS_EXPRESSION.name());
    return nodeMapper.toObject(endNode, nodeIndex, OWLClassExpression.class);
  }

  public OWLClassExpression toSuperClassExpr(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var endNode = nodeIndex.getEndNode(mainNode, SUPER_CLASS_EXPRESSION.name());
    return nodeMapper.toObject(endNode, nodeIndex, OWLClassExpression.class);
  }

  public OWLObjectPropertyExpression toObjectPropertyExpr(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var propertyNode = nodeIndex.getEndNode(mainNode, OBJECT_PROPERTY_EXPRESSION.name());
    return nodeMapper.toObject(propertyNode, nodeIndex, OWLObjectPropertyExpression.class);
  }

  public OWLDataPropertyExpression toDataPropertyExpr(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var propertyNode = nodeIndex.getEndNode(mainNode, DATA_PROPERTY_EXPRESSION.name());
    return nodeMapper.toObject(propertyNode, nodeIndex, OWLDataPropertyExpression.class);
  }

  public OWLEntity toEntity(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var entityNode = nodeIndex.getEndNode(mainNode, ENTITY.name());
    return nodeMapper.toObject(entityNode, nodeIndex, OWLEntity.class);
  }

  public OWLAnnotationProperty toAnnotationProperty(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var propertyNode = nodeIndex.getEndNode(mainNode, ANNOTATION_PROPERTY.name());
    return nodeMapper.toObject(propertyNode, nodeIndex, OWLAnnotationProperty.class);
  }

  public OWLDatatype toDatatype(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var datatypeNode = nodeIndex.getEndNode(mainNode, DATATYPE.name());
    return nodeMapper.toObject(datatypeNode, nodeIndex, OWLDatatype.class);
  }

  public OWLDataRange toDataRange(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var dataRangeNode = nodeIndex.getEndNode(mainNode, DATA_RANGE.name());
    return nodeMapper.toObject(dataRangeNode, nodeIndex, OWLDataRange.class);
  }

  public OWLNamedIndividual toNamedIndividual(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var individualNode = nodeIndex.getEndNode(mainNode, INDIVIDUAL.name());
    return nodeMapper.toObject(individualNode, nodeIndex, OWLNamedIndividual.class);
  }

  public Set<OWLAnnotation> toAxiomAnnotations(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var annotationNodes = nodeIndex.getEndNodes(mainNode, AXIOM_ANNOTATION.name());
    return nodeMapper.toObjects(annotationNodes, nodeIndex, OWLAnnotation.class);
  }
}
