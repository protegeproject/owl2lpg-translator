package edu.stanford.owl2lpg.translator.impl;

import edu.stanford.owl2lpg.datastructure.Edge;
import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.EdgeLabels;
import edu.stanford.owl2lpg.translator.NodeLabels;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The translator sub-module for the OWL 2 class expressions to labelled
 * property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassExpressionVisitor extends HasIriVisitor
    implements OWLClassExpressionVisitorEx<Graph> {

  @Override
  public Graph visit(OWLClass ce) {
    Node entityNode = Node.create(NodeLabels.CLASS);
    Node iriNode = createIriNode(ce);
    Graph classGraph = Graph.create(
        Edge.create(entityNode, iriNode, EdgeLabels.ENTITY_IRI));
    return classGraph;
  }

  @Override
  public Graph visit(OWLObjectIntersectionOf ce) {
    Node intersectionNode = Node.create(NodeLabels.OBJECT_INTERSECTION_OF);
    List<Edge> listOfEdges = ce.getOperandsAsList().stream()
        .map(operand -> Edge.create(
            intersectionNode, operand.accept(this), EdgeLabels.CLASS_EXPRESSION))
        .collect(Collectors.toList());
    Graph intersectionGraph = Graph.create(listOfEdges);
    return intersectionGraph;
  }
}
