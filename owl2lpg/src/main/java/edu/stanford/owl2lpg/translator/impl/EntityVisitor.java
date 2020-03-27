package edu.stanford.owl2lpg.translator.impl;

import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.EdgeLabels;
import edu.stanford.owl2lpg.translator.NodeLabels;
import org.semanticweb.owlapi.model.*;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.*;

/**
 * The translator sub-module for the OWL 2 entities to labelled property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class EntityVisitor extends HasIriVisitor implements OWLEntityVisitorEx<Graph> {

  @Override
  public Graph visit(OWLClass c) {
    Node entityNode = Node(NodeLabels.CLASS);
    Node iriNode = createIriNode(c);
    Graph entityGraph = Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
    return entityGraph;
  }

  @Override
  public Graph visit(OWLDatatype dt) {
    Node entityNode = Node(NodeLabels.DATATYPE);
    Node iriNode = createIriNode(dt);
    Graph entityGraph = Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
    return entityGraph;
  }

  @Override
  public Graph visit(OWLObjectProperty op) {
    Node entityNode = Node(NodeLabels.OBJECT_PROPERTY);
    Node iriNode = createIriNode(op);
    Graph entityGraph = Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
    return entityGraph;
  }

  @Override
  public Graph visit(OWLDataProperty dp) {
    Node entityNode = Node(NodeLabels.DATA_PROPERTY);
    Node iriNode = createIriNode(dp);
    Graph entityGraph = Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
    return entityGraph;
  }

  @Override
  public Graph visit(OWLAnnotationProperty ap) {
    Node entityNode = Node(NodeLabels.ANNOTATION_PROPERTY);
    Node iriNode = createIriNode(ap);
    Graph entityGraph = Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
    return entityGraph;
  }

  @Override
  public Graph visit(OWLNamedIndividual a) {
    Node entityNode = Node(NodeLabels.NAMED_INDIVIDUAL);
    Node iriNode = createIriNode(a);
    Graph entityGraph = Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
    return entityGraph;
  }
}
