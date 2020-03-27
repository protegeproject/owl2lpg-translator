package edu.stanford.owl2lpg.translator.impl;

import edu.stanford.owl2lpg.datastructure.Edge;
import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.EdgeLabels;
import edu.stanford.owl2lpg.translator.NodeLabels;
import org.semanticweb.owlapi.model.*;

/**
 * The translator sub-module for the OWL 2 entities to labelled property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class EntityVisitor extends HasIriVisitor implements OWLEntityVisitorEx<Graph> {

  @Override
  public Graph visit(OWLClass c) {
    Node entityNode = Node.create(NodeLabels.CLASS);
    Node iriNode = createIriNode(c);
    return Graph.create(Edge.create(entityNode, iriNode, EdgeLabels.ENTITY_IRI));
  }

  @Override
  public Graph visit(OWLDatatype dt) {
    Node entityNode = Node.create(NodeLabels.DATATYPE);
    Node iriNode = createIriNode(dt);
    return Graph.create(Edge.create(entityNode, iriNode, EdgeLabels.ENTITY_IRI));
  }

  @Override
  public Graph visit(OWLObjectProperty op) {
    Node entityNode = Node.create(NodeLabels.OBJECT_PROPERTY);
    Node iriNode = createIriNode(op);
    return Graph.create(Edge.create(entityNode, iriNode, EdgeLabels.ENTITY_IRI));
  }

  @Override
  public Graph visit(OWLDataProperty dp) {
    Node entityNode = Node.create(NodeLabels.DATA_PROPERTY);
    Node iriNode = createIriNode(dp);
    return Graph.create(Edge.create(entityNode, iriNode, EdgeLabels.ENTITY_IRI));
  }

  @Override
  public Graph visit(OWLAnnotationProperty ap) {
    Node entityNode = Node.create(NodeLabels.ANNOTATION_PROPERTY);
    Node iriNode = createIriNode(ap);
    return Graph.create(Edge.create(entityNode, iriNode, EdgeLabels.ENTITY_IRI));
  }

  @Override
  public Graph visit(OWLNamedIndividual a) {
    Node entityNode = Node.create(NodeLabels.NAMED_INDIVIDUAL);
    Node iriNode = createIriNode(a);
    return Graph.create(Edge.create(entityNode, iriNode, EdgeLabels.ENTITY_IRI));
  }
}
