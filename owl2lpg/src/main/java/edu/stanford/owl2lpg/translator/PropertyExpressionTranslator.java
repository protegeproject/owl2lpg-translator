package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import javax.annotation.Nonnull;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.*;

/**
 * The translator sub-module for translating the OWL 2 property expression
 * to labelled property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertyExpressionTranslator extends HasIriTranslator
    implements OWLPropertyExpressionVisitorEx<Graph> {

  @Override
  public Graph visit(@Nonnull OWLDataProperty dp) {
    Node entityNode = Node(NodeLabels.DATA_PROPERTY);
    Node iriNode = createIriNode(dp);
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
  public Graph visit(@Nonnull OWLObjectInverseOf ope) {
    Node inverseNode = Node(NodeLabels.OBJECT_INVERSE_OF);
    Graph operandGraph = ope.getInverseProperty().accept(this);
    return Graph(
        Edge(inverseNode, operandGraph, EdgeLabels.OBJECT_PROPERTY)
    );
  }
}
