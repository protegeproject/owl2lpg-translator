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
import java.util.stream.Collectors;

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
    var axiomNode = Node(NodeLabels.DATATYPE_DEFINITION);
    var datatypeTranslation = axiom.getDatatype().accept(dataVisitor);
    var dataRangeTranslation = axiom.getDataRange().accept(dataVisitor);
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
    var axiomNode = Node(NodeLabels.SUB_CLASS_OF);
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
    var axiomNode = Node(NodeLabels.NEGATIVE_OBJECT_PROPERTY_ASSERTION);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    var sourceIndividualTranslation = axiom.getSubject().accept(individualVisitor);
    var targetIndividualTranslation = axiom.getObject().accept(individualVisitor);
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
    var axiomNode = Node(NodeLabels.ASYMMETRIC_OBJECT_PROPERTY);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
    var axiomNode = Node(NodeLabels.REFLEXIVE_OBJECT_PROPERTY);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointClassesAxiom axiom) {
    var axiomNode = Node(NodeLabels.DISJOINT_CLASSES);
    var translations = axiom.getClassExpressions().stream()
        .map(ce -> ce.accept(classExpressionVisitor))
        .collect(Collectors.toList());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.CLASS_EXPRESSION))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
    var axiomNode = Node(NodeLabels.DATA_PROPERTY_DOMAIN);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    var domainTranslation = axiom.getDomain().accept(classExpressionVisitor);
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
    var axiomNode = Node(NodeLabels.OBJECT_PROPERTY_DOMAIN);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    var domainTranslation = axiom.getDomain().accept(classExpressionVisitor);
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
    var axiomNode = Node(NodeLabels.EQUIVALENT_OBJECT_PROPERTIES);
    var translations = axiom.getProperties().stream()
        .map(ce -> ce.accept(propertyExpressionVisitor))
        .collect(Collectors.toList());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
    var axiomNode = Node(NodeLabels.NEGATIVE_DATA_PROPERTY_ASSERTION);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    var sourceIndividualTranslation = axiom.getSubject().accept(individualVisitor);
    var targetLiteralTranslation = axiom.getObject().accept(dataVisitor);
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
    var axiomNode = Node(NodeLabels.DIFFERENT_INDIVIDUALS);
    var translations = axiom.getIndividuals().stream()
        .map(ce -> ce.accept(individualVisitor))
        .collect(Collectors.toList());
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
    var axiomNode = Node(NodeLabels.DISJOINT_DATA_PROPERTIES);
    var translations = axiom.getProperties().stream()
        .map(ce -> ce.accept(propertyExpressionVisitor))
        .collect(Collectors.toList());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.DATA_PROPERTY_EXPRESSION))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
    var axiomNode = Node(NodeLabels.DISJOINT_OBJECT_PROPERTIES);
    var translations = axiom.getProperties().stream()
        .map(ce -> ce.accept(propertyExpressionVisitor))
        .collect(Collectors.toList());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
    var axiomNode = Node(NodeLabels.OBJECT_PROPERTY_RANGE);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    var rangeTranslation = axiom.getRange().accept(classExpressionVisitor);
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
    var axiomNode = Node(NodeLabels.OBJECT_PROPERTY_ASSERTION);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    var sourceIndividualTranslation = axiom.getSubject().accept(individualVisitor);
    var targetIndividualTranslation = axiom.getObject().accept(individualVisitor);
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
    var axiomNode = Node(NodeLabels.FUNCTIONAL_OBJECT_PROPERTY);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
    var axiomNode = Node(NodeLabels.SUB_OBJECT_PROPERTY_OF);
    var subPropertyTranslation = axiom.getSubProperty().accept(propertyExpressionVisitor);
    var superPropertyTranslation = axiom.getSuperProperty().accept(propertyExpressionVisitor);
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
    var axiomNode = Node(NodeLabels.DISJOINT_UNION);
    var classTranslation = axiom.getOWLClass().accept(classExpressionVisitor);
    var classEdge = Edge(axiomNode, MainNode(classTranslation), EdgeLabels.CLASS);
    var translations = axiom.getClassExpressions().stream()
        .map(ce -> ce.accept(classExpressionVisitor))
        .collect(Collectors.toList());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.DISJOINT_CLASS_EXPRESSION))
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
    var axiomNode = Node(NodeLabels.SYMMETRIC_OBJECT_PROPERTY);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
    var axiomNode = Node(NodeLabels.DATA_PROPERTY_RANGE);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    var rangeTranslation = axiom.getRange().accept(dataVisitor);
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
    var axiomNode = Node(NodeLabels.FUNCTIONAL_DATA_PROPERTY);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.DATA_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
    var axiomNode = Node(NodeLabels.EQUIVALENT_DATA_PROPERTIES);
    var translations = axiom.getProperties().stream()
        .map(ce -> ce.accept(propertyExpressionVisitor))
        .collect(Collectors.toList());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.DATA_PROPERTY_EXPRESSION))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClassAssertionAxiom axiom) {
    var axiomNode = Node(NodeLabels.CLASS_ASSERTION);
    var classExpressionTranslation = axiom.getClassExpression().accept(classExpressionVisitor);
    var individualTranslation = axiom.getIndividual().accept(individualVisitor);
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
    var axiomNode = Node(NodeLabels.EQUIVALENT_CLASSES);
    var translations = axiom.getClassExpressions().stream()
        .map(ce -> ce.accept(classExpressionVisitor))
        .collect(Collectors.toList());
    var edges = translations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.CLASS_EXPRESSION))
        .collect(Collectors.toList());
    return Translation.create(axiomNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
    var axiomNode = Node(NodeLabels.DATA_PROPERTY_ASSERTION);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    var sourceIndividualTranslation = axiom.getSubject().accept(individualVisitor);
    var targetLiteralTranslation = axiom.getObject().accept(dataVisitor);
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
    var axiomNode = Node(NodeLabels.TRANSITIVE_OBJECT_PROPERTY);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
    var axiomNode = Node(NodeLabels.IRREFLEXIVE_OBJECT_PROPERTY);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
    var axiomNode = Node(NodeLabels.SUB_DATA_PROPERTY_OF);
    var subPropertyTranslation = axiom.getSubProperty().accept(propertyExpressionVisitor);
    var superPropertyTranslation = axiom.getSuperProperty().accept(propertyExpressionVisitor);
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
    var axiomNode = Node(NodeLabels.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSameIndividualAxiom axiom) {
    var axiomNode = Node(NodeLabels.SAME_INDIVIDUAL);
    var translations = axiom.getIndividuals().stream()
        .map(ce -> ce.accept(individualVisitor))
        .collect(Collectors.toList());
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
    var axiomNode = Node(NodeLabels.SUB_OBJECT_PROPERTY_OF);
    var subPropertyTranslation = translateChainRecursively(axiom.getPropertyChain());
    var superPropertyTranslation = axiom.getSuperProperty().accept(propertyExpressionVisitor);
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(subPropertyTranslation), EdgeLabels.SUB_OBJECT_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(superPropertyTranslation), EdgeLabels.SUPER_OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            subPropertyTranslation, superPropertyTranslation));
  }

  private Translation translateChainRecursively(List<OWLObjectPropertyExpression> chain) {
    if (chain.size() == 1) {
      return chain.get(0).accept(propertyExpressionVisitor);
    }
    Translation currentTranslation = chain.get(0).accept(propertyExpressionVisitor);
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
    var axiomNode = Node(NodeLabels.INVERSE_OBJECT_PROPERTIES);
    var firstPropertyTranslation = axiom.getFirstProperty().accept(propertyExpressionVisitor);
    var secondPropertyTranslation = axiom.getSecondProperty().accept(propertyExpressionVisitor);
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
    var axiomNode = Node(NodeLabels.HAS_KEY);
    var classTranslation = axiom.getClassExpression().accept(classExpressionVisitor);
    var classEdge = Edge(axiomNode, MainNode(classTranslation), EdgeLabels.CLASS_EXPRESSION);
    var objectExpressionTranslations = axiom.getObjectPropertyExpressions().stream()
        .map(ope -> ope.accept(propertyExpressionVisitor))
        .collect(Collectors.toList());
    var objectExpressionEdges = objectExpressionTranslations.stream()
        .map(translation -> Edge(axiomNode, MainNode(translation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION))
        .collect(Collectors.toList());
    var dataExpressionTranslations = axiom.getDataPropertyExpressions().stream()
        .map(dpe -> dpe.accept(propertyExpressionVisitor))
        .collect(Collectors.toList());
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
    var axiomNode = Node(NodeLabels.ANNOTATION_ASSERTION);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    var annotationSubjectTranslation = axiom.getSubject().accept(annotationSubjectVisitor);
    var annotationSubjectVisitor = axiom.getValue().accept(annotationValueVisitor);
    return Translation.create(axiomNode,
        ImmutableList.of(
            Edge(axiomNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
            Edge(axiomNode, MainNode(annotationSubjectTranslation), EdgeLabels.SOURCE_INDIVIDUAL),
            Edge(axiomNode, MainNode(annotationSubjectVisitor), EdgeLabels.TARGET_INDIVIDUAL)),
        ImmutableList.of(
            propertyTranslation, annotationSubjectTranslation, annotationSubjectVisitor));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    var axiomNode = Node(NodeLabels.SUB_ANNOTATION_PROPERTY_OF);
    var subPropertyTranslation = axiom.getSubProperty().accept(propertyExpressionVisitor);
    var superPropertyTranslation = axiom.getSuperProperty().accept(propertyExpressionVisitor);
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
    var axiomNode = Node(NodeLabels.ANNOTATION_PROPERTY_DOMAIN);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    var domainTranslation = axiom.getDomain().accept(annotationSubjectVisitor);
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
    var axiomNode = Node(NodeLabels.ANNOTATION_PROPERTY_RANGE);
    var propertyTranslation = axiom.getProperty().accept(propertyExpressionVisitor);
    var rangeTranslation = axiom.getRange().accept(annotationValueVisitor);
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
}
