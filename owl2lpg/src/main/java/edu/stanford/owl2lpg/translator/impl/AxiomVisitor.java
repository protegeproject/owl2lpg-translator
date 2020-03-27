package edu.stanford.owl2lpg.translator.impl;

import edu.stanford.owl2lpg.datastructure.Edge;
import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.EdgeLabels;
import edu.stanford.owl2lpg.translator.NodeLabels;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 * The translator sub-module for the OWL 2 axioms to labelled property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomVisitor implements OWLObjectVisitorEx<Graph> {

  @Override
  public Graph visit(OWLDeclarationAxiom axiom) {
    Node axiomNode = Node.create(NodeLabels.DECLARATION);
    Graph entityGraph = axiom.getEntity().accept(new EntityVisitor());
    Graph axiomGraph = Graph.create(
        Edge.create(axiomNode, entityGraph, EdgeLabels.ENTITY));
    return axiomGraph;
  }

  @Override
  public Graph visit(OWLSubClassOfAxiom axiom) {
    Node axiomNode = Node.create(NodeLabels.SUBCLASSOF);
    final Graph subClassExpressionGraph = axiom.getSubClass().accept(new ClassExpressionVisitor());
    final Graph superClassExpressionGraph = axiom.getSuperClass().accept(new ClassExpressionVisitor());
    final Graph axiomGraph = Graph.create(
        Edge.create(axiomNode, subClassExpressionGraph, EdgeLabels.SUB_CLASS_EXPRESSION),
        Edge.create(axiomNode, superClassExpressionGraph, EdgeLabels.SUPER_CLASS_EXPRESSION));
    return axiomGraph;
  }
}
