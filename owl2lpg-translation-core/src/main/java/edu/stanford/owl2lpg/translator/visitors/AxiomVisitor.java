package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.*;
import static edu.stanford.owl2lpg.translator.Translation.MainNode;

/**
 * A visitor that contains the implementation to translate the OWL 2 axioms.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomVisitor extends VisitorBase
    implements OWLAxiomVisitorEx<Translation> {

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

  @Nonnull
  private final OWLAnnotationSubjectVisitorEx<Translation> annotationSubjectVisitor;

  @Nonnull
  private final OWLAnnotationValueVisitorEx<Translation> annotationValueVisitor;

  @Inject
  public AxiomVisitor(@Nonnull OWLEntityVisitorEx<Translation> entityVisitor,
                      @Nonnull OWLPropertyExpressionVisitorEx<Translation> propertyExpressionVisitor,
                      @Nonnull OWLIndividualVisitorEx<Translation> individualVisitor,
                      @Nonnull OWLDataVisitorEx<Translation> dataVisitor,
                      @Nonnull OWLClassExpressionVisitorEx<Translation> classExpressionVisitor,
                      @Nonnull OWLAnnotationSubjectVisitorEx<Translation> annotationSubjectVisitor,
                      @Nonnull OWLAnnotationValueVisitorEx<Translation> annotationValueVisitor) {
    this.entityVisitor = checkNotNull(entityVisitor);
    this.propertyExpressionVisitor = checkNotNull(propertyExpressionVisitor);
    this.individualVisitor = checkNotNull(individualVisitor);
    this.dataVisitor = checkNotNull(dataVisitor);
    this.classExpressionVisitor = checkNotNull(classExpressionVisitor);
    this.annotationSubjectVisitor = checkNotNull(annotationSubjectVisitor);
    this.annotationValueVisitor = checkNotNull(annotationValueVisitor);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDeclarationAxiom axiom) {
    var axiomNode = Node(NodeLabels.DECLARATION, withIdentifierFrom(axiom));
    var entityTranslation = createEntityTranslation(axiom.getEntity());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(entityTranslation), EdgeLabels.ENTITY)),
        ImmutableList.of(
            entityTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
    var axiomNode = Node(NodeLabels.DATATYPE_DEFINITION, withIdentifierFrom(axiom));
    var datatypeTranslation = createDataRangeTranslation(axiom.getDatatype());
    var dataRangeTranslation = createDataRangeTranslation(axiom.getDataRange());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(datatypeTranslation), EdgeLabels.DATATYPE),
            Edge(axiomNode, MainNode(dataRangeTranslation), EdgeLabels.DATA_RANGE)),
        ImmutableList.of(
            datatypeTranslation, dataRangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubClassOfAxiom axiom) {
    var axiomNode = Node(NodeLabels.SUB_CLASS_OF, withIdentifierFrom(axiom));
    var subClassTranslation = createClassExpressionTranslation(axiom.getSubClass());
    var superClassTranslation = createClassExpressionTranslation(axiom.getSuperClass());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(subClassTranslation), EdgeLabels.SUB_CLASS_EXPRESSION),
            Edge(axiomNode, MainNode(superClassTranslation), EdgeLabels.SUPER_CLASS_EXPRESSION)),
        ImmutableList.of(
            subClassTranslation, superClassTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
    var axiomNode = Node(NodeLabels.NEGATIVE_OBJECT_PROPERTY_ASSERTION, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    var sourceIndividualTranslation = createIndividualTranslation(axiom.getSubject());
    var targetIndividualTranslation = createIndividualTranslation(axiom.getObject());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(sourceIndividualTranslation), EdgeLabels.SOURCE_INDIVIDUAL),
            Edge(axiomNode, MainNode(targetIndividualTranslation), EdgeLabels.TARGET_INDIVIDUAL)),
        ImmutableList.of(
            propertyTranslation, sourceIndividualTranslation, targetIndividualTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
    var axiomNode = Node(NodeLabels.ASYMMETRIC_OBJECT_PROPERTY, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
    var axiomNode = Node(NodeLabels.REFLEXIVE_OBJECT_PROPERTY, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointClassesAxiom axiom) {
    var axiomNode = Node(NodeLabels.DISJOINT_CLASSES, withIdentifierFrom(axiom));
    var translations = createClassExpressionTranslations(axiom.getClassExpressions());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.CLASS_EXPRESSION, true))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
    var axiomNode = Node(NodeLabels.DATA_PROPERTY_DOMAIN, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    var domainTranslation = createClassExpressionTranslation(axiom.getDomain());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.DATA_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(domainTranslation), EdgeLabels.DOMAIN)),
        ImmutableList.of(
            propertyTranslation, domainTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
    var axiomNode = Node(NodeLabels.OBJECT_PROPERTY_DOMAIN, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    var domainTranslation = createClassExpressionTranslation(axiom.getDomain());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(domainTranslation), EdgeLabels.DOMAIN)),
        ImmutableList.of(
            propertyTranslation, domainTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
    var axiomNode = Node(NodeLabels.EQUIVALENT_OBJECT_PROPERTIES, withIdentifierFrom(axiom));
    var translations = createPropertyExpressionTranslations(axiom.getProperties());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION, true))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
    var axiomNode = Node(NodeLabels.NEGATIVE_DATA_PROPERTY_ASSERTION, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    var sourceIndividualTranslation = createIndividualTranslation(axiom.getSubject());
    var targetLiteralTranslation = createLiteralTranslation(axiom.getObject());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.DATA_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(sourceIndividualTranslation), EdgeLabels.SOURCE_INDIVIDUAL),
            Edge(axiomNode, MainNode(targetLiteralTranslation), EdgeLabels.TARGET_VALUE)),
        ImmutableList.of(
            propertyTranslation, sourceIndividualTranslation, targetLiteralTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
    var axiomNode = Node(NodeLabels.DIFFERENT_INDIVIDUALS, withIdentifierFrom(axiom));
    var translations = createIndividualTranslations(axiom.getIndividuals());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.INDIVIDUAL))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
    var axiomNode = Node(NodeLabels.DISJOINT_DATA_PROPERTIES, withIdentifierFrom(axiom));
    var translations = createPropertyExpressionTranslations(axiom.getProperties());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.DATA_PROPERTY_EXPRESSION, true))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
    var axiomNode = Node(NodeLabels.DISJOINT_OBJECT_PROPERTIES, withIdentifierFrom(axiom));
    var translations = createPropertyExpressionTranslations(axiom.getProperties());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION, true))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
    var axiomNode = Node(NodeLabels.OBJECT_PROPERTY_RANGE, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    var rangeTranslation = createClassExpressionTranslation(axiom.getRange());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(rangeTranslation), EdgeLabels.RANGE)),
        ImmutableList.of(
            propertyTranslation, rangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
    var axiomNode = Node(NodeLabels.OBJECT_PROPERTY_ASSERTION, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    var sourceIndividualTranslation = createIndividualTranslation(axiom.getSubject());
    var targetIndividualTranslation = createIndividualTranslation(axiom.getObject());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(sourceIndividualTranslation), EdgeLabels.SOURCE_INDIVIDUAL),
            Edge(axiomNode, MainNode(targetIndividualTranslation), EdgeLabels.TARGET_INDIVIDUAL)),
        ImmutableList.of(
            propertyTranslation, sourceIndividualTranslation, targetIndividualTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
    var axiomNode = Node(NodeLabels.FUNCTIONAL_OBJECT_PROPERTY, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
    var axiomNode = Node(NodeLabels.SUB_OBJECT_PROPERTY_OF, withIdentifierFrom(axiom));
    var subPropertyTranslation = createPropertyExpressionTranslation(axiom.getSubProperty());
    var superPropertyTranslation = createPropertyExpressionTranslation(axiom.getSuperProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(subPropertyTranslation), EdgeLabels.SUB_OBJECT_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(superPropertyTranslation), EdgeLabels.SUPER_OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            subPropertyTranslation, superPropertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointUnionAxiom axiom) {
    var axiomNode = Node(NodeLabels.DISJOINT_UNION, withIdentifierFrom(axiom));
    var classTranslation = createClassExpressionTranslation(axiom.getOWLClass());
    var classEdge = Edge(axiomNode, MainNode(classTranslation), EdgeLabels.CLASS);
    var translations = createClassExpressionTranslations(axiom.getClassExpressions());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.DISJOINT_CLASS_EXPRESSION, true))
        .collect(Collectors.toList());
    edges.add(0, classEdge);
    translations.add(0, classTranslation);
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
    var axiomNode = Node(NodeLabels.SYMMETRIC_OBJECT_PROPERTY, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
    var axiomNode = Node(NodeLabels.DATA_PROPERTY_RANGE, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    var rangeTranslation = createDataRangeTranslation(axiom.getRange());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.DATA_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(rangeTranslation), EdgeLabels.RANGE)),
        ImmutableList.of(
            propertyTranslation, rangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
    var axiomNode = Node(NodeLabels.FUNCTIONAL_DATA_PROPERTY, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.DATA_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
    var axiomNode = Node(NodeLabels.EQUIVALENT_DATA_PROPERTIES, withIdentifierFrom(axiom));
    var translations = createPropertyExpressionTranslations(axiom.getProperties());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.DATA_PROPERTY_EXPRESSION, true))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClassAssertionAxiom axiom) {
    var axiomNode = Node(NodeLabels.CLASS_ASSERTION, withIdentifierFrom(axiom));
    var classExpressionTranslation = createClassExpressionTranslation(axiom.getClassExpression());
    var individualTranslation = createIndividualTranslation(axiom.getIndividual());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(classExpressionTranslation), EdgeLabels.CLASS_EXPRESSION),
            Edge(axiomNode, MainNode(individualTranslation), EdgeLabels.INDIVIDUAL)),
        ImmutableList.of(
            classExpressionTranslation, individualTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
    var axiomNode = Node(NodeLabels.EQUIVALENT_CLASSES, withIdentifierFrom(axiom));
    var translations = createClassExpressionTranslations(axiom.getClassExpressions());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.CLASS_EXPRESSION, true))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
    var axiomNode = Node(NodeLabels.DATA_PROPERTY_ASSERTION, withIdentifierFrom(axiom));
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    var sourceIndividualTranslation = createIndividualTranslation(axiom.getSubject());
    var targetLiteralTranslation = createLiteralTranslation(axiom.getObject());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.DATA_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(sourceIndividualTranslation), EdgeLabels.SOURCE_INDIVIDUAL),
            Edge(axiomNode, MainNode(targetLiteralTranslation), EdgeLabels.TARGET_VALUE)),
        ImmutableList.of(
            propertyTranslation, sourceIndividualTranslation, targetLiteralTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
    var axiomNode = Node(NodeLabels.TRANSITIVE_OBJECT_PROPERTY, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
    var axiomNode = Node(NodeLabels.IRREFLEXIVE_OBJECT_PROPERTY, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
    var axiomNode = Node(NodeLabels.SUB_DATA_PROPERTY_OF, withIdentifierFrom(axiom));
    var subPropertyTranslation = createPropertyExpressionTranslation(axiom.getSubProperty());
    var superPropertyTranslation = createPropertyExpressionTranslation(axiom.getSuperProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(subPropertyTranslation), EdgeLabels.SUB_DATA_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(superPropertyTranslation), EdgeLabels.SUPER_DATA_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            subPropertyTranslation, superPropertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
    var axiomNode = Node(NodeLabels.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSameIndividualAxiom axiom) {
    var axiomNode = Node(NodeLabels.SAME_INDIVIDUAL, withIdentifierFrom(axiom));
    var translations = createIndividualTranslations(axiom.getIndividuals());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.INDIVIDUAL))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
    var axiomNode = Node(NodeLabels.SUB_OBJECT_PROPERTY_OF, withIdentifierFrom(axiom));
    var subPropertyTranslation = translateChainRecursively(axiom.getPropertyChain());
    var superPropertyTranslation = createPropertyExpressionTranslation(axiom.getSuperProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(subPropertyTranslation), EdgeLabels.SUB_OBJECT_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(superPropertyTranslation), EdgeLabels.SUPER_OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            subPropertyTranslation, superPropertyTranslation));
  }

  private Translation translateChainRecursively(List<OWLObjectPropertyExpression> chain) {
    if (chain.size() == 1) {
      return createPropertyExpressionTranslation(chain.get(0));
    }
    Translation currentTranslation = createPropertyExpressionTranslation(chain.get(0));
    Translation nextTranslation = translateChainRecursively(chain.subList(1, chain.size()));
    return Translation.create(MainNode(currentTranslation),
        ImmutableList.of(
            Edge(MainNode(currentTranslation), MainNode(nextTranslation), EdgeLabels.NEXT)),
        ImmutableList.of(
            nextTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
    var axiomNode = Node(NodeLabels.INVERSE_OBJECT_PROPERTIES, withIdentifierFrom(axiom));
    var firstPropertyTranslation = createPropertyExpressionTranslation(axiom.getFirstProperty());
    var secondPropertyTranslation = createPropertyExpressionTranslation(axiom.getSecondProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(firstPropertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(secondPropertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            firstPropertyTranslation, secondPropertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLHasKeyAxiom axiom) {
    var axiomNode = Node(NodeLabels.HAS_KEY, withIdentifierFrom(axiom));
    var classTranslation = createClassExpressionTranslation(axiom.getClassExpression());
    var classEdge = Edge(axiomNode, MainNode(classTranslation), EdgeLabels.CLASS_EXPRESSION);
    var objectExpressionTranslations = createPropertyExpressionTranslations(axiom.getObjectPropertyExpressions());
    var objectExpressionEdges = objectExpressionTranslations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION))
        .collect(Collectors.toList());
    var dataExpressionTranslations = createPropertyExpressionTranslations(axiom.getDataPropertyExpressions());
    var dataExpressionEdges = dataExpressionTranslations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.DATA_PROPERTY_EXPRESSION))
        .collect(Collectors.toList());
    var translations = Lists.newArrayList(classTranslation);
    translations.addAll(objectExpressionTranslations);
    translations.addAll(dataExpressionTranslations);
    var edges = Lists.newArrayList(classEdge);
    edges.addAll(dataExpressionEdges);
    edges.addAll(objectExpressionEdges);
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
    var axiomNode = Node(NodeLabels.ANNOTATION_ASSERTION, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    var annotationSubjectTranslation = createAnnotationSubjectTranslation(axiom.getSubject());
    var annotationValueTranslation = createAnnotationValueTranslation(axiom.getValue());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.ANNOTATION_PROPERTY),
            Edge(axiomNode, MainNode(annotationSubjectTranslation), EdgeLabels.ANNOTATION_SUBJECT),
            Edge(axiomNode, MainNode(annotationValueTranslation), EdgeLabels.ANNOTATION_VALUE)),
        ImmutableList.of(
            propertyTranslation, annotationSubjectTranslation, annotationValueTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    var axiomNode = Node(NodeLabels.SUB_ANNOTATION_PROPERTY_OF, withIdentifierFrom(axiom));
    var subPropertyTranslation = createPropertyExpressionTranslation(axiom.getSubProperty());
    var superPropertyTranslation = createPropertyExpressionTranslation(axiom.getSuperProperty());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(subPropertyTranslation), EdgeLabels.SUB_ANNOTATION_PROPERTY),
            Edge(axiomNode, MainNode(superPropertyTranslation), EdgeLabels.SUPER_ANNOTATION_PROPERTY)),
        ImmutableList.of(
            subPropertyTranslation, superPropertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
    var axiomNode = Node(NodeLabels.ANNOTATION_PROPERTY_DOMAIN, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    var domainTranslation = createAnnotationSubjectTranslation(axiom.getDomain());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.ANNOTATION_PROPERTY),
            Edge(axiomNode, MainNode(domainTranslation), EdgeLabels.DOMAIN)),
        ImmutableList.of(
            propertyTranslation, domainTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
    var axiomNode = Node(NodeLabels.ANNOTATION_PROPERTY_RANGE, withIdentifierFrom(axiom));
    var propertyTranslation = createPropertyExpressionTranslation(axiom.getProperty());
    var rangeTranslation = createAnnotationValueTranslation(axiom.getRange());
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.ANNOTATION_PROPERTY),
            Edge(axiomNode, MainNode(rangeTranslation), EdgeLabels.RANGE)),
        ImmutableList.of(
            propertyTranslation, rangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull SWRLRule rule) {
    throw new UnsupportedOperationException("SWRLRule translation is not supported yet");
  }

  private Translation createEntityTranslation(@Nonnull OWLEntity entity) {
    return entity.accept(entityVisitor);
  }

  private List<Translation> createClassExpressionTranslations(Set<? extends OWLClassExpression> classExpressions) {
    return classExpressions.stream()
        .map(this::createClassExpressionTranslation)
        .collect(Collectors.toList());
  }

  private Translation createClassExpressionTranslation(OWLClassExpression classExpression) {
    return classExpression.accept(classExpressionVisitor);
  }

  private List<Translation> createPropertyExpressionTranslations(Set<? extends OWLPropertyExpression> propertyExpressions) {
    return propertyExpressions.stream()
        .map(this::createPropertyExpressionTranslation)
        .collect(Collectors.toList());
  }

  private Translation createPropertyExpressionTranslation(OWLPropertyExpression propertyExpression) {
    return propertyExpression.accept(propertyExpressionVisitor);
  }

  private List<Translation> createIndividualTranslations(Set<OWLIndividual> individuals) {
    return individuals.stream()
        .map(this::createIndividualTranslation)
        .collect(Collectors.toList());
  }

  private Translation createIndividualTranslation(OWLIndividual individual) {
    return individual.accept(individualVisitor);
  }

  private Translation createLiteralTranslation(@Nonnull OWLLiteral literal) {
    return literal.accept(dataVisitor);
  }

  private Translation createDataRangeTranslation(OWLDataRange dataRange) {
    return dataRange.accept(dataVisitor);
  }

  private Translation createAnnotationSubjectTranslation(OWLAnnotationSubject subject) {
    return subject.accept(annotationSubjectVisitor);
  }

  private Translation createAnnotationValueTranslation(OWLAnnotationValue value) {
    return value.accept(annotationValueVisitor);
  }
}
