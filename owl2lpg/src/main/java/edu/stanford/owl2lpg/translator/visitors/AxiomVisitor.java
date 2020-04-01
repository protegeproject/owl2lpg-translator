package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.Edge;
import static edu.stanford.owl2lpg.model.GraphFactory.Node;
import static edu.stanford.owl2lpg.translator.Translation.MainNode;

/**
 * A visitor that contains the implementation to translate the OWL 2 axioms.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomVisitor implements OWLAxiomVisitorEx<Translation> {

  @Nonnull
  private final OWLEntityVisitorEx<Translation> entityVisitor;

  @Nonnull
  private final OWLPropertyExpressionVisitorEx<Translation> propertyExpressionVisitor;

  @Nonnull
  private final OWLIndividualVisitorEx<Translation> individualVisitor;

  @Nonnull
  private final OWLDataVisitorEx<Translation> dataVisitor;

  @Nonnull
  private final OWLClassExpressionVisitorEx<Translation> classExpressionVisitor;

  @Inject
  public AxiomVisitor(@Nonnull OWLEntityVisitorEx<Translation> entityVisitor,
                      @Nonnull OWLPropertyExpressionVisitorEx<Translation> propertyExpressionVisitor,
                      @Nonnull OWLIndividualVisitorEx<Translation> individualVisitor,
                      @Nonnull OWLDataVisitorEx<Translation> dataVisitor,
                      @Nonnull OWLClassExpressionVisitorEx<Translation> classExpressionVisitor) {
    this.entityVisitor = checkNotNull(entityVisitor);
    this.propertyExpressionVisitor = checkNotNull(propertyExpressionVisitor);
    this.individualVisitor = checkNotNull(individualVisitor);
    this.dataVisitor = checkNotNull(dataVisitor);
    this.classExpressionVisitor = checkNotNull(classExpressionVisitor);
  }

  @Override
  public Translation visit(@Nonnull OWLDeclarationAxiom axiom) {
    var axiomNode = Node(NodeLabels.DECLARATION);
    var entityTranslation = axiom.getEntity().accept(entityVisitor);
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(entityTranslation), EdgeLabels.ENTITY)),
        ImmutableList.of(
            entityTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
    return null;
  }

  @Override
  public Translation visit(@Nonnull OWLSubClassOfAxiom axiom) {
    var axiomNode = Node(NodeLabels.SUBCLASSOF);
    var subClassExpressionTranslation = axiom.getSubClass().accept(classExpressionVisitor);
    var superClassExpressionTranslation = axiom.getSuperClass().accept(classExpressionVisitor);
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(subClassExpressionTranslation), EdgeLabels.SUB_CLASS_EXPRESSION),
            Edge(axiomNode, MainNode(superClassExpressionTranslation), EdgeLabels.SUPER_CLASS_EXPRESSION)),
        ImmutableList.of(
            subClassExpressionTranslation, superClassExpressionTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointClassesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointUnionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClassAssertionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSameIndividualAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLHasKeyAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull SWRLRule rule) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
    return null;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
    return null;
  }
}
