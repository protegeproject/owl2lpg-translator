package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import javax.annotation.Nonnull;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.*;

/**
 * The translator sub-module for translating the OWL 2 axioms to labelled
 * property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomTranslator implements OWLObjectVisitorEx<Graph> {

  @Override
  public Graph visit(@Nonnull OWLDeclarationAxiom axiom) {
    Node axiomNode = Node(NodeLabels.DECLARATION);
    Graph entityGraph = axiom.getEntity().accept(new EntityTranslator());
    return Graph(
        Edge(axiomNode, entityGraph, EdgeLabels.ENTITY)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLSubClassOfAxiom axiom) {
    Node axiomNode = Node(NodeLabels.SUBCLASSOF);
    final Graph subClassExpressionGraph = axiom.getSubClass()
        .accept(new ClassExpressionTranslator());
    final Graph superClassExpressionGraph = axiom.getSuperClass()
        .accept(new ClassExpressionTranslator());
    return Graph(
        Edge(axiomNode, subClassExpressionGraph, EdgeLabels.SUB_CLASS_EXPRESSION),
        Edge(axiomNode, superClassExpressionGraph, EdgeLabels.SUPER_CLASS_EXPRESSION)
    );
  }
}
