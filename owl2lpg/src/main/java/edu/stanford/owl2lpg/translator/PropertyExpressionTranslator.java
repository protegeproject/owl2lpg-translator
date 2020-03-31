package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.*;

/**
 * The translator sub-module for translating the OWL 2 property expression
 * to labelled property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertyExpressionTranslator implements OWLPropertyExpressionVisitorEx<Graph> {

  @Override
  public Graph visit(@Nonnull OWLDataProperty dp) {
    return dp.accept(new EntityTranslator());
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLAnnotationProperty ap) {
    return ap.accept(new EntityTranslator());
  }

  @Override
  public Graph visit(@Nonnull OWLObjectProperty op) {
    return op.accept(new EntityTranslator());
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
