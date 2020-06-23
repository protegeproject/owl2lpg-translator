package edu.stanford.owl2lpg.client.read.frame2;

import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SUB_CLASS_OF;

public class SubClassOfHandler implements NodeHandler<OWLSubClassOfAxiom> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public SubClassOfHandler(@Nonnull OWLDataFactory dataFactory) {
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public String getLabel() {
    return SUB_CLASS_OF.getMainLabel();
  }

  @Override
  public OWLSubClassOfAxiom handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var subClassExpr = toClassExpression(node, SUB_CLASS_EXPRESSION.name(), nodeIndex, nodeMapper);
    var superClassExpr = toClassExpression(node, SUPER_CLASS_EXPRESSION.name(), nodeIndex, nodeMapper);
    var axiomAnnotations = toAnnotations(node, nodeIndex, nodeMapper);
    return dataFactory.getOWLSubClassOfAxiom(subClassExpr, superClassExpr, axiomAnnotations);
  }

  private OWLClassExpression toClassExpression(Node startNode, String relName, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var endNode = nodeIndex.getEndNode(startNode, relName);
    return nodeMapper.toObject(endNode, nodeIndex, OWLClassExpression.class);
  }

  private Set<OWLAnnotation> toAnnotations(Node startNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var endNodes = nodeIndex.getEndNodes(startNode, AXIOM_ANNOTATION.name());
    return nodeMapper.toObjects(endNodes, nodeIndex, OWLAnnotation.class);
  }
}
