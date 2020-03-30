package edu.stanford.owl2lpg.translator.impl;

import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.EdgeLabels;
import edu.stanford.owl2lpg.translator.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.*;

/**
 * The translator sub-module for translating the OWL 2 entities to labelled
 * property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class EntityTranslator extends HasIriTranslator
    implements OWLEntityVisitorEx<Graph> {

  @Override
  public Graph visit(@Nonnull OWLClass c) {
    Node entityNode = Node(NodeLabels.CLASS);
    Node iriNode = createIriNode(c);
    return Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLDatatype dt) {
    Node entityNode = Node(NodeLabels.DATATYPE);
    Node iriNode = createIriNode(dt);
    return Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLObjectProperty op) {
    Node entityNode = Node(NodeLabels.OBJECT_PROPERTY);
    Node iriNode = createIriNode(op);
    return Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLDataProperty dp) {
    Node entityNode = Node(NodeLabels.DATA_PROPERTY);
    Node iriNode = createIriNode(dp);
    return Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLAnnotationProperty ap) {
    Node entityNode = Node(NodeLabels.ANNOTATION_PROPERTY);
    Node iriNode = createIriNode(ap);
    return Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLNamedIndividual a) {
    Node entityNode = Node(NodeLabels.NAMED_INDIVIDUAL);
    Node iriNode = createIriNode(a);
    return Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
  }
}
