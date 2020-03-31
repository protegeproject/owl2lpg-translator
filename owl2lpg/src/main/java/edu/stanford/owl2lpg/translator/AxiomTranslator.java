package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.*;

/**
 * The translator sub-module for translating the OWL 2 axioms to labelled
 * property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomTranslator implements OWLAxiomVisitorEx<Graph> {

  @Override
  public Graph visit(@Nonnull OWLDeclarationAxiom axiom) {
    Node axiomNode = Node(NodeLabels.DECLARATION);
    Graph entityGraph = axiom.getEntity().accept(new EntityTranslator());
    return Graph(
        Edge(axiomNode, entityGraph, EdgeLabels.ENTITY)
    );
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
    return null;
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

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLDisjointClassesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLDisjointUnionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLClassAssertionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLSameIndividualAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLHasKeyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull SWRLRule rule) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Graph visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
    return null;
  }
}
