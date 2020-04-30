package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A visitor that contains the implementation to translate the OWL 2 axioms.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomVisitor extends VisitorBase
    implements OWLAxiomVisitorEx<Translation> {

  private Node mainNode;

  private final VisitorFactory visitorFactory;

  @Inject
  public AxiomVisitor(@Nonnull NodeIdMapper nodeIdMapper,
                      @Nonnull VisitorFactory visitorFactory) {
    super(nodeIdMapper);
    this.visitorFactory = checkNotNull(visitorFactory);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDeclarationAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DECLARATION);
    var entityEdge = createEdge(axiom.getEntity(), EdgeLabels.ENTITY);
    var entityTranslation = createNestedTranslation(axiom.getEntity());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(entityEdge, annotationEdges);
    var allNestedTranslations = concatTranslations(entityTranslation, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DATATYPE_DEFINITION);
    var datatypeEdge = createEdge(axiom.getDatatype(), EdgeLabels.DATATYPE);
    var datatypeTranslation = createNestedTranslation(axiom.getDatatype());
    var dataRangeEdge = createEdge(axiom.getDataRange(), EdgeLabels.DATA_RANGE);
    var dataRangeTranslation = createNestedTranslation(axiom.getDataRange());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(datatypeEdge, dataRangeEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(datatypeTranslation, dataRangeTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubClassOfAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SUB_CLASS_OF);
    var subClassEdge = createEdge(axiom.getSubClass(), EdgeLabels.SUB_CLASS_EXPRESSION);
    var subClassTranslation = createNestedTranslation(axiom.getSubClass());
    var superClassEdge = createEdge(axiom.getSuperClass(), EdgeLabels.SUPER_CLASS_EXPRESSION);
    var superClassTranslation = createNestedTranslation(axiom.getSuperClass());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var augmentedSubClassOfEdge = createAugmentedEdge(axiom.getSubClass(), axiom.getSuperClass(),
        EdgeLabels.SUB_CLASS_OF);
    var augmentedIsSubjectOfEdge = createAugmentedEdge(axiom.getSubClass(), axiom,
        EdgeLabels.IS_SUBJECT_OF);
    var allEdges = concatEdges(concatEdges(
        concatEdges(subClassEdge, superClassEdge), annotationEdges),
        concatEdges(augmentedSubClassOfEdge, augmentedIsSubjectOfEdge));
    var allNestedTranslations = concatTranslations(
        concatTranslations(subClassTranslation, superClassTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.NEGATIVE_OBJECT_PROPERTY_ASSERTION);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var sourceIndividualEdge = createEdge(axiom.getSubject(), EdgeLabels.SOURCE_INDIVIDUAL);
    var sourceIndividualTranslation = createNestedTranslation(axiom.getSubject());
    var targetIndividualEdge = createEdge(axiom.getObject(), EdgeLabels.TARGET_INDIVIDUAL);
    var targetIndividualTranslation = createNestedTranslation(axiom.getObject());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(
        concatEdges(propertyExprEdge, sourceIndividualEdge, targetIndividualEdge),
        annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(propertyExprTranslation, sourceIndividualTranslation, targetIndividualTranslation),
        annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.ASYMMETRIC_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(propertyExprEdge, annotationEdges);
    var allNestedTranslations = concatTranslations(propertyExprTranslation, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.REFLEXIVE_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(propertyExprEdge, annotationEdges);
    var allNestedTranslations = concatTranslations(propertyExprTranslation, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointClassesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DISJOINT_CLASSES);
    var classExprEdges = createEdges(axiom.getClassExpressions(), EdgeLabels.CLASS_EXPRESSION);
    var classExprTranslations = createNestedTranslations(axiom.getClassExpressions());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(classExprEdges, annotationEdges);
    var allNestedTranslations = concatTranslations(classExprTranslations, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DATA_PROPERTY_DOMAIN);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var domainEdge = createEdge(axiom.getDomain(), EdgeLabels.DOMAIN);
    var domainTranslation = createNestedTranslation(axiom.getDomain());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(propertyExprEdge, domainEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(propertyExprTranslation, domainTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.OBJECT_PROPERTY_DOMAIN);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var domainEdge = createEdge(axiom.getDomain(), EdgeLabels.DOMAIN);
    var domainTranslation = createNestedTranslation(axiom.getDomain());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(propertyExprEdge, domainEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(propertyExprTranslation, domainTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.EQUIVALENT_OBJECT_PROPERTIES);
    var propertyExprEdges = createEdges(axiom.getProperties(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslations = createNestedTranslations(axiom.getProperties());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(propertyExprEdges, annotationEdges);
    var allNestedTranslations = concatTranslations(propertyExprTranslations, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.NEGATIVE_DATA_PROPERTY_ASSERTION);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var sourceIndividualEdge = createEdge(axiom.getSubject(), EdgeLabels.SOURCE_INDIVIDUAL);
    var sourceIndividualTranslation = createNestedTranslation(axiom.getSubject());
    var targetValueEdge = createEdge(axiom.getObject(), EdgeLabels.TARGET_VALUE);
    var targetValueTranslation = createNestedTranslation(axiom.getObject());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(
        concatEdges(propertyExprEdge, sourceIndividualEdge, targetValueEdge),
        annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(propertyExprTranslation, sourceIndividualTranslation, targetValueTranslation),
        annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DIFFERENT_INDIVIDUALS);
    var individualEdges = createEdges(axiom.getIndividuals(), EdgeLabels.INDIVIDUAL);
    var individualTranslations = createNestedTranslations(axiom.getIndividuals());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(individualEdges, annotationEdges);
    var allNestedTranslations = concatTranslations(individualTranslations, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DISJOINT_DATA_PROPERTIES);
    var propertyExprEdges = createEdges(axiom.getProperties(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslations = createNestedTranslations(axiom.getProperties());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(propertyExprEdges, annotationEdges);
    var allNestedTranslations = concatTranslations(propertyExprTranslations, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DISJOINT_OBJECT_PROPERTIES);
    var propertyExprEdges = createEdges(axiom.getProperties(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslations = createNestedTranslations(axiom.getProperties());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(propertyExprEdges, annotationEdges);
    var allNestedTranslations = concatTranslations(propertyExprTranslations, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.OBJECT_PROPERTY_RANGE);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var rangeEdge = createEdge(axiom.getRange(), EdgeLabels.RANGE);
    var rangeTranslation = createNestedTranslation(axiom.getRange());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(propertyExprEdge, rangeEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(propertyExprTranslation, rangeTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.OBJECT_PROPERTY_ASSERTION);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var sourceIndividualEdge = createEdge(axiom.getSubject(), EdgeLabels.SOURCE_INDIVIDUAL);
    var sourceIndividualTranslation = createNestedTranslation(axiom.getSubject());
    var targetIndividualEdge = createEdge(axiom.getObject(), EdgeLabels.TARGET_INDIVIDUAL);
    var targetIndividualTranslation = createNestedTranslation(axiom.getObject());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(
        concatEdges(propertyExprEdge, sourceIndividualEdge, targetIndividualEdge),
        annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(propertyExprTranslation, sourceIndividualTranslation, targetIndividualTranslation),
        annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.FUNCTIONAL_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(propertyExprEdge, annotationEdges);
    var allNestedTranslations = concatTranslations(propertyExprTranslation, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    var subPropertyEdge = createEdge(axiom.getSubProperty(), EdgeLabels.SUB_OBJECT_PROPERTY_EXPRESSION);
    var subPropertyTranslation = createNestedTranslation(axiom.getSubProperty());
    var superPropertyEdge = createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_OBJECT_PROPERTY_EXPRESSION);
    var superPropertyTranslation = createNestedTranslation(axiom.getSuperProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(subPropertyEdge, superPropertyEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(subPropertyTranslation, superPropertyTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointUnionAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DISJOINT_UNION);
    var classEdge = createEdge(axiom.getOWLClass(), EdgeLabels.CLASS);
    var classTranslation = createNestedTranslation(axiom.getOWLClass());
    var disjointClassExprEdges = createEdges(axiom.getClassExpressions(), EdgeLabels.DISJOINT_CLASS_EXPRESSION);
    var disjointClassExprTranslations = createNestedTranslations(axiom.getClassExpressions());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(classEdge), concatEdges(disjointClassExprEdges, annotationEdges));
    var allNestedTranslations = concatTranslations(
        concatTranslations(classTranslation,
            concatTranslations(disjointClassExprTranslations, annotationTranslations)));
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SYMMETRIC_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(propertyExprEdge, annotationEdges);
    var allNestedTranslations = concatTranslations(propertyExprTranslation, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DATA_PROPERTY_RANGE);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var rangeEdge = createEdge(axiom.getRange(), EdgeLabels.RANGE);
    var rangeTranslation = createNestedTranslation(axiom.getRange());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(propertyExprEdge, rangeEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(propertyExprTranslation, rangeTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.FUNCTIONAL_DATA_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(propertyExprEdge, annotationEdges);
    var allNestedTranslations = concatTranslations(propertyExprTranslation, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.EQUIVALENT_DATA_PROPERTIES);
    var propertyExprEdges = createEdges(axiom.getProperties(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslations = createNestedTranslations(axiom.getProperties());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(propertyExprEdges, annotationEdges);
    var allNestedTranslations = concatTranslations(propertyExprTranslations, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClassAssertionAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.CLASS_ASSERTION);
    var classExprEdge = createEdge(axiom.getClassExpression(), EdgeLabels.CLASS_EXPRESSION);
    var classExprTranslation = createNestedTranslation(axiom.getClassExpression());
    var individualEdge = createEdge(axiom.getIndividual(), EdgeLabels.INDIVIDUAL);
    var individualTranslation = createNestedTranslation(axiom.getIndividual());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(classExprEdge, individualEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(classExprTranslation, individualTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.EQUIVALENT_CLASSES);
    var classExprEdges = createEdges(axiom.getClassExpressions(), EdgeLabels.CLASS_EXPRESSION);
    var classExprTranslations = createNestedTranslations(axiom.getClassExpressions());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(classExprEdges, annotationEdges);
    var allNestedTranslations = concatTranslations(classExprTranslations, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DATA_PROPERTY_ASSERTION);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var sourceIndividualEdge = createEdge(axiom.getSubject(), EdgeLabels.SOURCE_INDIVIDUAL);
    var sourceIndividualTranslation = createNestedTranslation(axiom.getSubject());
    var targetValueEdge = createEdge(axiom.getObject(), EdgeLabels.TARGET_VALUE);
    var targetValueTranslation = createNestedTranslation(axiom.getObject());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(
        concatEdges(propertyExprEdge, sourceIndividualEdge, targetValueEdge),
        annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(propertyExprTranslation, sourceIndividualTranslation, targetValueTranslation),
        annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.TRANSITIVE_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(propertyExprEdge, annotationEdges);
    var allNestedTranslations = concatTranslations(propertyExprTranslation, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.IRREFLEXIVE_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(propertyExprEdge, annotationEdges);
    var allNestedTranslations = concatTranslations(propertyExprTranslation, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SUB_DATA_PROPERTY_OF);
    var subPropertyEdge = createEdge(axiom.getSubProperty(), EdgeLabels.SUB_DATA_PROPERTY_EXPRESSION);
    var subPropertyTranslation = createNestedTranslation(axiom.getSubProperty());
    var superPropertyEdge = createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_DATA_PROPERTY_EXPRESSION);
    var superPropertyTranslation = createNestedTranslation(axiom.getSuperProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(subPropertyEdge, superPropertyEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(subPropertyTranslation, superPropertyTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(propertyExprEdge, annotationEdges);
    var allNestedTranslations = concatTranslations(propertyExprTranslation, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSameIndividualAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SAME_INDIVIDUAL);
    var individualEdges = createEdges(axiom.getIndividuals(), EdgeLabels.INDIVIDUAL);
    var individualTranslations = createNestedTranslations(axiom.getIndividuals());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(individualEdges, annotationEdges);
    var allNestedTranslations = concatTranslations(individualTranslations, annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    var subPropertyEdge = createEdge(axiom.getPropertyChain().get(0), EdgeLabels.SUB_OBJECT_PROPERTY_EXPRESSION);
    var subPropertyTranslation = createChainTranslation(axiom.getPropertyChain());
    var superPropertyEdge = createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_OBJECT_PROPERTY_EXPRESSION);
    var superPropertyTranslation = createNestedTranslation(axiom.getSuperProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(subPropertyEdge, superPropertyEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(subPropertyTranslation, superPropertyTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  protected Translation createChainTranslation(List<OWLObjectPropertyExpression> chain) {
    if (chain.size() == 1) {
      return createNestedTranslation(chain.get(0));
    } else {
      var headChainNode = createNestedTranslation(chain.get(0)).getMainNode();
      return translateChainRecursively(headChainNode, chain.subList(1, chain.size()));
    }
  }

  private Translation translateChainRecursively(Node headNode, List<OWLObjectPropertyExpression> chain) {
    var nextPropertyNode = createNestedTranslation(chain.get(0)).getMainNode();
    var nextPropertyEdge = Edge.create(headNode, nextPropertyNode, EdgeLabels.NEXT, Properties.empty());
    if (chain.size() == 1) {
      var nextPropertyTranslation = createNestedTranslation(chain.get(0));
      return Translation.create(headNode,
          ImmutableList.of(nextPropertyEdge),
          ImmutableList.of(nextPropertyTranslation));
    } else {
      var nextPropertyTranslation = translateChainRecursively(nextPropertyNode, chain.subList(1, chain.size()));
      return Translation.create(headNode,
          ImmutableList.of(nextPropertyEdge),
          ImmutableList.of(nextPropertyTranslation));
    }
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.INVERSE_OBJECT_PROPERTIES);
    var firstPropertyEdge = createEdge(axiom.getFirstProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var firstPropertyTranslation = createNestedTranslation(axiom.getFirstProperty());
    var secondPropertyEdge = createEdge(axiom.getSecondProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var secondPropertyTranslation = createNestedTranslation(axiom.getSecondProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(firstPropertyEdge, secondPropertyEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(firstPropertyTranslation, secondPropertyTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLHasKeyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.HAS_KEY);
    var classEdge = createEdge(axiom.getClassExpression(), EdgeLabels.CLASS_EXPRESSION);
    var classTranslation = createNestedTranslation(axiom.getClassExpression());
    var objectPropertyExprEdges = createEdges(axiom.getObjectPropertyExpressions(),
        EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var objectPropertyExprTranslations = createNestedTranslations(axiom.getObjectPropertyExpressions());
    var dataPropertyExprEdges = createEdges(axiom.getDataPropertyExpressions(),
        EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var dataPropertyExprTranslations = createNestedTranslations(axiom.getDataPropertyExpressions());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(classEdge), concatEdges(
        concatEdges(objectPropertyExprEdges, dataPropertyExprEdges), annotationEdges));
    var allNestedTranslations = concatTranslations(concatTranslations(classTranslation), concatTranslations(
        concatTranslations(concatTranslations(objectPropertyExprTranslations, dataPropertyExprTranslations),
            annotationTranslations)));
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.ANNOTATION_ASSERTION);
    var annotationPropertyEdge = createEdge(axiom.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    var annotationPropertyTranslation = createNestedTranslation(axiom.getProperty());
    var annotationSubjectEdge = createEdge(axiom.getSubject(), EdgeLabels.ANNOTATION_SUBJECT);
    var annotationSubjectTranslation = createNestedTranslation(axiom.getSubject());
    var annotationValueEdge = createEdge(axiom.getValue(), EdgeLabels.ANNOTATION_VALUE);
    var annotationValueTranslation = createNestedTranslation(axiom.getValue());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(annotationPropertyEdge, annotationSubjectEdge, annotationValueEdge),
        annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(annotationPropertyTranslation, annotationSubjectTranslation, annotationValueTranslation),
        annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SUB_ANNOTATION_PROPERTY_OF);
    var subPropertyEdge = createEdge(axiom.getSubProperty(), EdgeLabels.SUB_ANNOTATION_PROPERTY);
    var subPropertyTranslation = createNestedTranslation(axiom.getSubProperty());
    var superPropertyEdge = createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_ANNOTATION_PROPERTY);
    var superPropertyTranslation = createNestedTranslation(axiom.getSuperProperty());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(subPropertyEdge, superPropertyEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(subPropertyTranslation, superPropertyTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.ANNOTATION_PROPERTY_DOMAIN);
    var annotationPropertyEdge = createEdge(axiom.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    var annotationPropertyTranslation = createNestedTranslation(axiom.getProperty());
    var domainEdge = createEdge(axiom.getDomain(), EdgeLabels.DOMAIN);
    var domainTranslation = createNestedTranslation(axiom.getDomain());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(annotationPropertyEdge, domainEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(annotationPropertyTranslation, domainTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.ANNOTATION_PROPERTY_RANGE);
    var annotationPropertyEdge = createEdge(axiom.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    var annotationPropertyTranslation = createNestedTranslation(axiom.getProperty());
    var rangeEdge = createEdge(axiom.getRange(), EdgeLabels.RANGE);
    var rangeTranslation = createNestedTranslation(axiom.getRange());
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(annotationPropertyEdge, rangeEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
        concatTranslations(annotationPropertyTranslation, rangeTranslation), annotationTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allNestedTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull SWRLRule rule) {
    throw new UnsupportedOperationException("SWRLRule translation is not supported yet");
  }

  @Override
  protected Node getMainNode() {
    return mainNode;
  }

  @Override
  protected Translation getTranslation(@Nonnull OWLObject anyObject) {
    checkNotNull(anyObject);
    if (anyObject instanceof OWLAxiom) {
      return getAxiomTranslation((OWLAxiom) anyObject);
    } else if (anyObject instanceof OWLEntity) {
      return getEntityTranslation((OWLEntity) anyObject);
    } else if (anyObject instanceof OWLClassExpression) {
      return getClassExpressionTranslation((OWLClassExpression) anyObject);
    } else if (anyObject instanceof OWLPropertyExpression) {
      return getPropertyExpressionTranslation((OWLPropertyExpression) anyObject);
    } else if (anyObject instanceof OWLIndividual) {
      return getIndividualTranslation((OWLIndividual) anyObject);
    } else if (anyObject instanceof OWLLiteral) {
      return getLiteralTranslation((OWLLiteral) anyObject);
    } else if (anyObject instanceof OWLDataRange) {
      return getDataRangeTranslation((OWLDataRange) anyObject);
    } else if (anyObject instanceof OWLAnnotation) {
      return getAxiomAnnotation((OWLAnnotation) anyObject);
    } else if (anyObject instanceof OWLAnnotationSubject) {
      return getAnnotationSubjectTranslation((OWLAnnotationSubject) anyObject);
    } else if (anyObject instanceof OWLAnnotationValue) {
      return getAnnotationValueTranslation((OWLAnnotationValue) anyObject);
    }
    throw new IllegalArgumentException("Implementation error");
  }

  private Translation getAxiomTranslation(OWLAxiom axiom) {
    var axiomVisitor = visitorFactory.createAxiomVisitor();
    return axiom.accept(axiomVisitor);
  }

  private Translation getEntityTranslation(OWLEntity entity) {
    var entityVisitor = visitorFactory.createEntityVisitor();
    return entity.accept(entityVisitor);
  }

  private Translation getClassExpressionTranslation(OWLClassExpression classExpression) {
    var classExpressionVisitor = visitorFactory.createClassExpressionVisitor();
    return classExpression.accept(classExpressionVisitor);
  }

  private Translation getPropertyExpressionTranslation(OWLPropertyExpression propertyExpression) {
    var propertyExpressionVisitor = visitorFactory.createPropertyExpressionVisitor();
    return propertyExpression.accept(propertyExpressionVisitor);
  }

  private Translation getIndividualTranslation(OWLIndividual individual) {
    var individualVisitor = visitorFactory.createIndividualVisitor();
    return individual.accept(individualVisitor);
  }

  private Translation getLiteralTranslation(OWLLiteral literal) {
    var dataVisitor = visitorFactory.createDataVisitor();
    return literal.accept(dataVisitor);
  }

  private Translation getDataRangeTranslation(OWLDataRange dataRange) {
    var dataVisitor = visitorFactory.createDataVisitor();
    return dataRange.accept(dataVisitor);
  }

  private Translation getAxiomAnnotation(OWLAnnotation annotation) {
    var annotationVisitor = visitorFactory.createAnnotationObjectVisitor();
    return annotation.accept(annotationVisitor);
  }

  private Translation getAnnotationSubjectTranslation(OWLAnnotationSubject subject) {
    var annotationSubjectVisitor = visitorFactory.createAnnotationSubjectVisitor();
    return subject.accept(annotationSubjectVisitor);
  }

  private Translation getAnnotationValueTranslation(OWLAnnotationValue value) {
    var annotationValueVisitor = visitorFactory.createAnnotationValueVisitor();
    return value.accept(annotationValueVisitor);
  }

  /* Helpful methods for concatenating edges and translations */

  private static Collection<Edge> concatEdges(Edge... edges) {
    return Lists.newArrayList(edges);
  }

  @SafeVarargs
  private static Collection<Edge> concatEdges(Collection<Edge>... edgeCollections) {
    return Stream.of(edgeCollections)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private static Collection<Edge> concatEdges(Edge edge, Collection<Edge> edgeCollection) {
    return concatEdges(concatEdges(edge), edgeCollection);
  }

  private static Collection<Translation> concatTranslations(Translation... translations) {
    return Lists.newArrayList(translations);
  }

  @SafeVarargs
  private static Collection<Translation> concatTranslations(Collection<Translation>... translationCollections) {
    return Stream.of(translationCollections)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private static Collection<Translation> concatTranslations(Translation translation,
                                                            Collection<Translation> translationCollection) {
    return concatTranslations(concatTranslations(translation), translationCollection);
  }
}
