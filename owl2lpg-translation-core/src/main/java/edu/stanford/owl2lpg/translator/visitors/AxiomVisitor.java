package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import edu.stanford.owl2lpg.model.*;
import edu.stanford.owl2lpg.translator.*;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ANNOTATION_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.CLASS;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.CLASS_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATATYPE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATA_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATA_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.LITERAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.OBJECT_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SAME_INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_ANNOTATION_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_DATA_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_OBJECT_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SUB_CLASS_OF;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.*;

/**
 * A visitor that contains the implementation to translate the OWL 2 axioms.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@TranslationSessionScope
public class AxiomVisitor implements OWLAxiomVisitorEx<Translation> {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Nonnull
  private final EntityTranslator entityTranslator;

  @Nonnull
  private final ClassExpressionTranslator classExprTranslator;

  @Nonnull
  private final PropertyExpressionTranslator propertyExprTranslator;

  @Nonnull
  private final DataRangeTranslator dataRangeTranslator;

  @Nonnull
  private final LiteralTranslator literalTranslator;

  @Nonnull
  private final IndividualTranslator individualTranslator;

  @Nonnull
  private final AnnotationObjectTranslator annotationTranslator;

  @Nonnull
  private final AnnotationSubjectTranslator annotationSubjectTranslator;

  @Nonnull
  private final AnnotationValueTranslator annotationValueTranslator;

  @Nonnull
  private final AugmentedEdgeInclusionChecker augmentedEdgeInclusionChecker;

  @Inject
  public AxiomVisitor(@Nonnull NodeFactory nodeFactory,
                      @Nonnull EdgeFactory edgeFactory,
                      @Nonnull EntityTranslator entityTranslator,
                      @Nonnull ClassExpressionTranslator classExprTranslator,
                      @Nonnull PropertyExpressionTranslator propertyExprTranslator,
                      @Nonnull DataRangeTranslator dataRangeTranslator,
                      @Nonnull LiteralTranslator literalTranslator,
                      @Nonnull IndividualTranslator individualTranslator,
                      @Nonnull AnnotationObjectTranslator annotationTranslator,
                      @Nonnull AnnotationSubjectTranslator annotationSubjectTranslator,
                      @Nonnull AnnotationValueTranslator annotationValueTranslator,
                      @Nonnull AugmentedEdgeInclusionChecker augmentedEdgeInclusionChecker) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
    this.entityTranslator = checkNotNull(entityTranslator);
    this.classExprTranslator = checkNotNull(classExprTranslator);
    this.propertyExprTranslator = checkNotNull(propertyExprTranslator);
    this.dataRangeTranslator = checkNotNull(dataRangeTranslator);
    this.literalTranslator = checkNotNull(literalTranslator);
    this.individualTranslator = checkNotNull(individualTranslator);
    this.annotationTranslator = checkNotNull(annotationTranslator);
    this.annotationSubjectTranslator = checkNotNull(annotationSubjectTranslator);
    this.annotationValueTranslator = checkNotNull(annotationValueTranslator);
    this.augmentedEdgeInclusionChecker = checkNotNull(augmentedEdgeInclusionChecker);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDeclarationAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DECLARATION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var entityNode = addEntityTranslationAndStructuralEdge(axiom.getEntity(),
        axiomNode, ENTITY, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, entityNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DATATYPE_DEFINITION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var entityNode = addEntityTranslationAndStructuralEdge(axiom.getDatatype(),
        axiomNode, DATATYPE, translations, edges);
    addDataRangeTranslationAndStructuralEdge(axiom.getDataRange(),
        axiomNode, DATA_RANGE, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, entityNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubClassOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, SUB_CLASS_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subClassExpression = axiom.getSubClass();
    var subClassNode = addClassExprTranslationAndStructuralEdge(subClassExpression,
        axiomNode, SUB_CLASS_EXPRESSION, translations, edges);
    var superClassExpresion = axiom.getSuperClass();
    var superClassNode = addClassExprTranslationAndStructuralEdge(superClassExpresion,
        axiomNode, SUPER_CLASS_EXPRESSION, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subClassNode, edges);
    addSubClassOfAugmentedEdge(subClassNode, superClassNode, edges);
    addSubClassRelatedToAugmentedEdge(subClassNode, superClassExpresion, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NEGATIVE_OBJECT_PROPERTY_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subjectNode = addIndividualTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, SOURCE_INDIVIDUAL, translations, edges);
    addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addIndividualTranslationAndStructuralEdge(axiom.getObject(),
        axiomNode, TARGET_INDIVIDUAL, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subjectNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, FUNCTIONAL_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, SYMMETRIC_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, ASYMMETRIC_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, TRANSITIVE_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, REFLEXIVE_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, IRREFLEXIVE_OBJECT_PROPERTY);
  }

  private Translation translateObjectPropertyCharacteristic(@Nonnull OWLObjectPropertyCharacteristicAxiom axiom,
                                                            @Nonnull NodeLabels nodeLabels) {
    var axiomNode = createAxiomNode(axiom, nodeLabels);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyNode = addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointClassesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DISJOINT_CLASSES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var classExprNodes = addClassExprTranslationsAndStructuralEdges(axiom.getClassExpressions(),
        axiomNode, CLASS_EXPRESSION, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, classExprNodes, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
    return translatePropertyDomain(axiom, DATA_PROPERTY_DOMAIN, DATA_PROPERTY_EXPRESSION);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
    return translatePropertyDomain(axiom, OBJECT_PROPERTY_DOMAIN, OBJECT_PROPERTY_EXPRESSION);
  }

  @SuppressWarnings("rawtypes")
  private Translation translatePropertyDomain(@Nonnull OWLPropertyDomainAxiom axiom,
                                              @Nonnull NodeLabels nodeLabels,
                                              @Nonnull EdgeLabel propertyEdgeLabel) {
    var axiomNode = createAxiomNode(axiom, nodeLabels);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNode = addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, propertyEdgeLabel, translations, edges);
    var domainNode = addClassExprTranslationAndStructuralEdge(axiom.getDomain(),
        axiomNode, DOMAIN, translations, edges);
    addDomainAugmentedEdge(propertyExprNode, domainNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, EQUIVALENT_OBJECT_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNodes = addPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprNodes, edges);
    addSymmetricalSubObjectPropertyOfAugmentedEdges(propertyExprNodes, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NEGATIVE_DATA_PROPERTY_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subjectNode = addIndividualTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, SOURCE_INDIVIDUAL, translations, edges);
    addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges);
    addLiteralTranslationAndStructuralEdge(axiom.getObject(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subjectNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DIFFERENT_INDIVIDUALS);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualNodes = addIndividualTranslationsAndStructuralEdges(axiom.getIndividuals(),
        axiomNode, INDIVIDUAL, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, individualNodes, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DISJOINT_DATA_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNodes = addPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprNodes, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DISJOINT_OBJECT_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNodes = addPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprNodes, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, OBJECT_PROPERTY_RANGE);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNode = addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    var rangeNode = addClassExprTranslationAndStructuralEdge(axiom.getRange(),
        axiomNode, RANGE, translations, edges);
    addRangeAugmentedEdge(propertyExprNode, rangeNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, OBJECT_PROPERTY_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualSubjectNode = addIndividualTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, SOURCE_INDIVIDUAL, translations, edges);
    addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    var individualObjectNode = addIndividualTranslationAndStructuralEdge(axiom.getObject(),
        axiomNode, TARGET_INDIVIDUAL, translations, edges);
    addRelatedToAugmentedEdge(individualSubjectNode, individualObjectNode, axiom.getProperty(), edges);
    addAxiomSubjectAugmentedEdge(axiomNode, individualSubjectNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subPropertyNode = addPropertyExprTranslationAndStructuralEdge(axiom.getSubProperty(),
        axiomNode, SUB_OBJECT_PROPERTY_EXPRESSION, translations, edges);
    var superPropertyNode = addPropertyExprTranslationAndStructuralEdge(axiom.getSuperProperty(),
        axiomNode, SUPER_OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addSubObjectPropertyOfAugmentedEdge(subPropertyNode, superPropertyNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subPropertyNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointUnionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DISJOINT_UNION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    addEntityTranslationAndStructuralEdge(axiom.getOWLClass(),
        axiomNode, CLASS, translations, edges);
    var classExprNodes = addClassExprTranslationsAndStructuralEdges(axiom.getClassExpressions(),
        axiomNode, DISJOINT_CLASS_EXPRESSION, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, classExprNodes, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DATA_PROPERTY_RANGE);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNode = addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges);
    var rangeNode = addDataRangeTranslationAndStructuralEdge(axiom.getRange(),
        axiomNode, RANGE, translations, edges);
    addRangeAugmentedEdge(propertyExprNode, rangeNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, FUNCTIONAL_DATA_PROPERTY);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyNode = addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, EQUIVALENT_DATA_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprList = addPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprList, edges);
    addSymmetricalSubDataPropertyOfAugmentedEdges(propertyExprList, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClassAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, CLASS_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualNode = addIndividualTranslationAndStructuralEdge(axiom.getIndividual(),
        axiomNode, INDIVIDUAL, translations, edges);
    var classExprNode = addClassExprTranslationAndStructuralEdge(axiom.getClassExpression(),
        axiomNode, CLASS_EXPRESSION, translations, edges);
    addAugmentedEdge(individualNode, classExprNode, TYPE, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, individualNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, EQUIVALENT_CLASSES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var classList = addClassExprTranslationsAndStructuralEdges(axiom.getClassExpressions(),
        axiomNode, CLASS_EXPRESSION, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, classList, edges);
    addSymmetricalSubClassOfAugmentedEdges(classList, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DATA_PROPERTY_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualNode = addIndividualTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, SOURCE_INDIVIDUAL, translations, edges);
    addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges);
    var literalNode = addLiteralTranslationAndStructuralEdge(axiom.getObject(),
        axiomNode, translations, edges);
    addRelatedToAugmentedEdge(individualNode, literalNode, axiom.getProperty(), edges);
    addAxiomSubjectAugmentedEdge(axiomNode, individualNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_DATA_PROPERTY_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subPropertyNode = addPropertyExprTranslationAndStructuralEdge(axiom.getSubProperty(),
        axiomNode, SUB_DATA_PROPERTY_EXPRESSION, translations, edges);
    var superPropertyNode = addPropertyExprTranslationAndStructuralEdge(axiom.getSuperProperty(),
        axiomNode, SUPER_DATA_PROPERTY_EXPRESSION, translations, edges);
    addSubDataPropertyOfAugmentedEdge(subPropertyNode, superPropertyNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subPropertyNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSameIndividualAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SAME_INDIVIDUAL);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualList = addIndividualTranslationsAndStructuralEdges(axiom.getIndividuals(),
        axiomNode, INDIVIDUAL, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, individualList, edges);
    addSameIndividualAugmentedEdges(individualList, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var firstChainNode = addPropertyChainTranslationAndEdge(axiom.getPropertyChain(),
        axiomNode, translations, edges);
    addPropertyExprTranslationAndStructuralEdge(axiom.getSuperProperty(),
        axiomNode, SUPER_OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, firstChainNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  private Node addPropertyChainTranslationAndEdge(@Nonnull List<OWLObjectPropertyExpression> propertyChain,
                                                  @Nonnull Node axiomNode,
                                                  @Nonnull Builder<Translation> translations,
                                                  @Nonnull Builder<Edge> edges) {
    var firstChainNode = addPropertyExprTranslationAndStructuralEdge(propertyChain.get(0),
        axiomNode, SUB_OBJECT_PROPERTY_EXPRESSION, translations, edges);
    if (propertyChain.size() > 1) {
      var nextChain = propertyChain.subList(1, propertyChain.size());
      addPropertyChainRecursively(nextChain, firstChainNode, translations, edges);
    }
    return firstChainNode;
  }

  private void addPropertyChainRecursively(@Nonnull List<OWLObjectPropertyExpression> propertyChain,
                                           @Nonnull Node mainNode,
                                           @Nonnull Builder<Translation> translations,
                                           @Nonnull Builder<Edge> edges) {
    if (!propertyChain.isEmpty()) {
      var nextMainNode = addPropertyExprTranslationAndStructuralEdge(propertyChain.get(0),
          mainNode, NEXT, translations, edges);
      var nextChain = propertyChain.subList(1, propertyChain.size());
      addPropertyChainRecursively(nextChain, nextMainNode, translations, edges);
    }
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, INVERSE_OBJECT_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var ope1Node = addPropertyExprTranslationAndStructuralEdge(axiom.getFirstProperty(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    var ope2Node = addPropertyExprTranslationAndStructuralEdge(axiom.getSecondProperty(),
        axiomNode, INVERSE_OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addInverseOfAugmentedEdge(ope1Node, ope2Node, edges);
    addInverseOfAugmentedEdge(ope2Node, ope1Node, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, ope1Node, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLHasKeyAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, HAS_KEY);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var classExprNode = addClassExprTranslationAndStructuralEdge(axiom.getClassExpression(),
        axiomNode, CLASS_EXPRESSION, translations, edges);
    addPropertyExprTranslationsAndStructuralEdges(axiom.getObjectPropertyExpressions(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addPropertyExprTranslationsAndStructuralEdges(axiom.getDataPropertyExpressions(),
        axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, classExprNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, ANNOTATION_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var annotationSubjectNode = addAnnotationSubjectTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, translations, edges);
    addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, ANNOTATION_PROPERTY, translations, edges);
    var annotationValueNode = addAnnotationValueTranslationAndStructuralEdge(axiom.getValue(),
        axiomNode, translations, edges);
    addRelatedToAugmentedEdge(annotationSubjectNode, annotationValueNode,
        Properties.create(ImmutableMap.of(
            PropertyFields.IRI, String.valueOf(axiom.getProperty().getIRI()),
            PropertyFields.TYPE, axiom.getProperty().getEntityType().getName())),
        edges);
    addAxiomSubjectAugmentedEdge(axiomNode, annotationSubjectNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_ANNOTATION_PROPERTY_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subPropertyNode = addPropertyExprTranslationAndStructuralEdge(axiom.getSubProperty(),
        axiomNode, SUB_ANNOTATION_PROPERTY, translations, edges);
    var superPropertyNode = addPropertyExprTranslationAndStructuralEdge(axiom.getSuperProperty(),
        axiomNode, SUPER_ANNOTATION_PROPERTY, translations, edges);
    addSubAnnotationPropertyOfAugmentedEdge(subPropertyNode, superPropertyNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subPropertyNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, ANNOTATION_PROPERTY_DOMAIN);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyNode = addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, ANNOTATION_PROPERTY, translations, edges);
    var domainNode = addIriTranslationAndStructuralEdge(axiom.getDomain(),
        axiomNode, DOMAIN, translations, edges);
    addDomainAugmentedEdge(propertyNode, domainNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, ANNOTATION_PROPERTY_RANGE);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyNode = addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, ANNOTATION_PROPERTY, translations, edges);
    var rangeNode = addIriTranslationAndStructuralEdge(axiom.getRange(),
        axiomNode, RANGE, translations, edges);
    addRangeAugmentedEdge(propertyNode, rangeNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyNode, edges);
    addAxiomAnnotationStructuralEdges(axiom.getAnnotations(), axiomNode, translations, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull SWRLRule rule) {
    return Translation.create(rule,
        Node.create(NodeId.create("0"), SWRL_RULE),
        ImmutableList.of(),
        ImmutableList.of());
  }

  @Nonnull
  private Node createAxiomNode(OWLAxiom axiom, NodeLabels nodeLabels) {
    return nodeFactory.createNode(axiom, nodeLabels);
  }

  @Nonnull
  private static Builder<Translation> newTranslationBuilder() {
    return new Builder<>();
  }

  @Nonnull
  private static Builder<Edge> newEdgesBuilder() {
    return new Builder<>();
  }

  /* Main translation and structural edge */

  private Node addIriTranslationAndStructuralEdge(@Nonnull IRI iri,
                                                  @Nonnull Node axiomNode,
                                                  @Nonnull EdgeLabel edgeLabel,
                                                  @Nonnull Builder<Translation> translations,
                                                  @Nonnull Builder<Edge> edges) {
    var iriTranslation = annotationValueTranslator.translate(iri);
    return addTranslationAndStructuralEdge(axiomNode, iriTranslation, edgeLabel, translations, edges);
  }

  private Node addEntityTranslationAndStructuralEdge(@Nonnull OWLEntity entity,
                                                     @Nonnull Node axiomNode,
                                                     @Nonnull EdgeLabel edgeLabel,
                                                     @Nonnull Builder<Translation> translations,
                                                     @Nonnull Builder<Edge> edges) {
    Translation entityTranslation = entityTranslator.translate(entity);
    return addTranslationAndStructuralEdge(axiomNode, entityTranslation, edgeLabel, translations, edges);
  }

  private List<Node> addIndividualTranslationsAndStructuralEdges(@Nonnull Set<OWLIndividual> individuals,
                                                                 @Nonnull Node axiomNode,
                                                                 @Nonnull EdgeLabel edgeLabel,
                                                                 @Nonnull Builder<Translation> translations,
                                                                 @Nonnull Builder<Edge> edges) {
    return individuals.stream()
        .map(individual ->
            addIndividualTranslationAndStructuralEdge(individual, axiomNode, edgeLabel, translations, edges))
        .collect(Collectors.toList());
  }

  private Node addIndividualTranslationAndStructuralEdge(@Nonnull OWLIndividual individual,
                                                         @Nonnull Node axiomNode,
                                                         @Nonnull EdgeLabel edgeLabel,
                                                         @Nonnull Builder<Translation> translations,
                                                         @Nonnull Builder<Edge> edges) {
    var individualTranslation = individualTranslator.translate(individual);
    return addTranslationAndStructuralEdge(axiomNode, individualTranslation, edgeLabel, translations, edges);
  }

  private Node addLiteralTranslationAndStructuralEdge(@Nonnull OWLLiteral literal,
                                                      @Nonnull Node axiomNode,
                                                      @Nonnull Builder<Translation> translations,
                                                      @Nonnull Builder<Edge> edges) {
    var literalTranslation = literalTranslator.translate(literal);
    return addTranslationAndStructuralEdge(axiomNode, literalTranslation, TARGET_VALUE, translations, edges);
  }

  private List<Node> addClassExprTranslationsAndStructuralEdges(@Nonnull Set<OWLClassExpression> classExprs,
                                                                @Nonnull Node axiomNode,
                                                                @Nonnull EdgeLabel edgeLabel,
                                                                @Nonnull Builder<Translation> translations,
                                                                @Nonnull Builder<Edge> edges) {
    return classExprs.stream()
        .map(classExpr ->
            addClassExprTranslationAndStructuralEdge(classExpr, axiomNode, edgeLabel, translations, edges))
        .collect(Collectors.toList());
  }

  private Node addClassExprTranslationAndStructuralEdge(@Nonnull OWLClassExpression classExpr,
                                                        @Nonnull Node axiomNode,
                                                        @Nonnull EdgeLabel edgeLabel,
                                                        @Nonnull Builder<Translation> translations,
                                                        @Nonnull Builder<Edge> edges) {
    var classExprTranslation = classExprTranslator.translate(classExpr);
    return addTranslationAndStructuralEdge(axiomNode, classExprTranslation, edgeLabel, translations, edges);
  }

  private List<Node> addPropertyExprTranslationsAndStructuralEdges(@Nonnull Set<? extends OWLPropertyExpression> propertyExprs,
                                                                   @Nonnull Node axiomNode,
                                                                   @Nonnull EdgeLabel edgeLabel,
                                                                   @Nonnull Builder<Translation> translations,
                                                                   @Nonnull Builder<Edge> edges) {
    return propertyExprs.stream()
        .map(propertyExpr ->
            addPropertyExprTranslationAndStructuralEdge(propertyExpr, axiomNode, edgeLabel, translations, edges))
        .collect(Collectors.toList());
  }

  private Node addPropertyExprTranslationAndStructuralEdge(@Nonnull OWLPropertyExpression propertyExpr,
                                                           @Nonnull Node axiomNode,
                                                           @Nonnull EdgeLabel edgeLabel,
                                                           @Nonnull Builder<Translation> translations,
                                                           @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    return addTranslationAndStructuralEdge(axiomNode, propertyExprTranslation, edgeLabel, translations, edges);
  }

  private Node addDataRangeTranslationAndStructuralEdge(@Nonnull OWLDataRange dataRange,
                                                        @Nonnull Node axiomNode,
                                                        @Nonnull EdgeLabel edgeLabel,
                                                        @Nonnull Builder<Translation> translations,
                                                        @Nonnull Builder<Edge> edges) {
    var dataRangeTranslation = dataRangeTranslator.translate(dataRange);
    return addTranslationAndStructuralEdge(axiomNode, dataRangeTranslation, edgeLabel, translations, edges);
  }

  private void addAnnotationTranslationAndStructuralEdge(@Nonnull OWLAnnotation annotation,
                                                         @Nonnull Node axiomNode,
                                                         @Nonnull Builder<Translation> translations,
                                                         @Nonnull Builder<Edge> edges) {
    var annotationTranslation = annotationTranslator.translate(annotation);
    addTranslationAndStructuralEdge(axiomNode, annotationTranslation, AXIOM_ANNOTATION, translations, edges);
  }

  private Node addAnnotationSubjectTranslationAndStructuralEdge(@Nonnull OWLAnnotationSubject subject,
                                                                @Nonnull Node axiomNode,
                                                                @Nonnull Builder<Translation> translations,
                                                                @Nonnull Builder<Edge> edges) {
    var annotationSubjectTranslation = annotationSubjectTranslator.translate(subject);
    return addTranslationAndStructuralEdge(axiomNode, annotationSubjectTranslation, ANNOTATION_SUBJECT, translations, edges);
  }

  private Node addAnnotationValueTranslationAndStructuralEdge(@Nonnull OWLAnnotationValue value,
                                                              @Nonnull Node axiomNode,
                                                              @Nonnull Builder<Translation> translations,
                                                              @Nonnull Builder<Edge> edges) {
    var annotationValueTranslation = annotationValueTranslator.translate(value);
    return addTranslationAndStructuralEdge(axiomNode, annotationValueTranslation, ANNOTATION_VALUE, translations, edges);
  }

  private void addAxiomAnnotationStructuralEdges(@Nonnull Set<OWLAnnotation> axiomAnnotations,
                                                 @Nonnull Node axiomNode,
                                                 @Nonnull Builder<Translation> translations,
                                                 @Nonnull Builder<Edge> edges) {
    axiomAnnotations.forEach(ann -> addAnnotationTranslationAndStructuralEdge(ann, axiomNode, translations, edges));
  }

  private Node addTranslationAndStructuralEdge(@Nonnull Node axiomNode,
                                               @Nonnull Translation translation,
                                               @Nonnull EdgeLabel edgeLabel,
                                               @Nonnull Builder<Translation> translations,
                                               @Nonnull Builder<Edge> edges) {
    var translationMainNode = translation.getMainNode();
    var structuralEdge = edgeFactory.createEdge(axiomNode,
        translationMainNode,
        edgeLabel,
        Properties.of(PropertyFields.STRUCTURAL_SPEC, true));
    translations.add(translation);
    edges.add(structuralEdge);
    return translationMainNode;
  }

  /* Augmented edges */

  private void addAxiomSubjectAugmentedEdge(Node axiomNode, Node subjectNode, Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(AXIOM_SUBJECT)) {
      addAugmentedEdge(axiomNode, subjectNode, AXIOM_SUBJECT, edges);
    }
  }

  private void addAxiomSubjectAugmentedEdges(Node axiomNode, List<Node> subjectNodes, Builder<Edge> edges) {
    subjectNodes.forEach(subjectNode -> addAxiomSubjectAugmentedEdge(axiomNode, subjectNode, edges));
  }

  private void addSubClassOfAugmentedEdge(Node subClassNode, Node superClassNode, Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(EdgeLabel.SUB_CLASS_OF)) {
      addAugmentedEdge(subClassNode, superClassNode, EdgeLabel.SUB_CLASS_OF, edges);
    }
  }

  private void addSymmetricalSubClassOfAugmentedEdges(List<Node> classNodes, Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(EdgeLabel.SUB_CLASS_OF)) {
      addSymmetricalAugmentedEdges(classNodes, EdgeLabel.SUB_CLASS_OF, edges);
    }
  }

  private void addSubObjectPropertyOfAugmentedEdge(Node subPropertyNode, Node superPropertyOf, Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(SUB_OBJECT_PROPERTY_OF)) {
      addAugmentedEdge(subPropertyNode, superPropertyOf, SUB_OBJECT_PROPERTY_OF, edges);
    }
  }

  private void addSymmetricalSubObjectPropertyOfAugmentedEdges(List<Node> objectPropertyNodes, Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(SUB_OBJECT_PROPERTY_OF)) {
      addSymmetricalAugmentedEdges(objectPropertyNodes, SUB_OBJECT_PROPERTY_OF, edges);
    }
  }

  private void addSubDataPropertyOfAugmentedEdge(Node subPropertyNode, Node superPropertyOf, Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(SUB_DATA_PROPERTY_OF)) {
      addAugmentedEdge(subPropertyNode, superPropertyOf, SUB_DATA_PROPERTY_OF, edges);
    }
  }

  private void addSymmetricalSubDataPropertyOfAugmentedEdges(List<Node> dataPropertyNodes, Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(SUB_DATA_PROPERTY_OF)) {
      addSymmetricalAugmentedEdges(dataPropertyNodes, SUB_DATA_PROPERTY_OF, edges);
    }
  }

  private void addSubAnnotationPropertyOfAugmentedEdge(Node subPropertyNode, Node superPropertyNode, Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(SUB_ANNOTATION_PROPERTY_OF)) {
      addAugmentedEdge(subPropertyNode, superPropertyNode, SUB_ANNOTATION_PROPERTY_OF, edges);
    }
  }

  private void addDomainAugmentedEdge(Node propertyNode, Node domainNode, Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(HAS_DOMAIN)) {
      addAugmentedEdge(propertyNode, domainNode, HAS_DOMAIN, edges);
    }
  }

  private void addRangeAugmentedEdge(Node propertyNode, Node rangeNode, Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(HAS_RANGE)) {
      addAugmentedEdge(propertyNode, rangeNode, HAS_RANGE, edges);
    }
  }

  private void addInverseOfAugmentedEdge(Node node1, Node node2, Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(INVERSE_OF)) {
      addAugmentedEdge(node1, node2, INVERSE_OF, edges);
    }
  }

  private void addSameIndividualAugmentedEdges(List<Node> individualNodes, Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(SAME_INDIVIDUAL)) {
      addSymmetricalAugmentedEdges(individualNodes, SAME_INDIVIDUAL, edges);
    }
  }

  private void addSubClassRelatedToAugmentedEdge(@Nonnull Node subClassNode,
                                                 @Nonnull OWLClassExpression superClassExpresion,
                                                 @Nonnull Builder<Edge> edges) {
    if (augmentedEdgeInclusionChecker.allows(EdgeLabel.SUB_CLASS_OF)) {
      /* Add extra augmented edges when the superclass is an expression */
      superClassExpresion.accept(new OWLClassExpressionVisitorAdapter() {
        @Override
        public void visit(OWLObjectIntersectionOf ce) {
          var translation = classExprTranslator.translate(ce);
          translation.getDirectNodes()
              .forEach(ceNode -> addAugmentedEdge(
                  subClassNode, ceNode, EdgeLabel.SUB_CLASS_OF, edges));
        }

        @Override
        public void visit(OWLObjectSomeValuesFrom ce) {
          addNestedRelatedToAugmentedEdge(subClassNode, ce,
              OBJECT_PROPERTY_EXPRESSION,
              CLASS_EXPRESSION,
              edges);
        }

        @Override
        public void visit(OWLDataSomeValuesFrom ce) {
          addNestedRelatedToAugmentedEdge(subClassNode, ce,
              DATA_PROPERTY_EXPRESSION,
              DATA_RANGE,
              edges);
        }

        @Override
        public void visit(OWLObjectHasValue ce) {
          addNestedRelatedToAugmentedEdge(subClassNode, ce,
              OBJECT_PROPERTY_EXPRESSION,
              INDIVIDUAL,
              edges);
        }

        @Override
        public void visit(OWLDataHasValue ce) {
          addNestedRelatedToAugmentedEdge(subClassNode, ce,
              DATA_PROPERTY_EXPRESSION,
              LITERAL,
              edges);
        }

        @Override
        public void visit(OWLDataMinCardinality ce) {
          visitDataCardinalityRestriction(ce);
        }

        @Override
        public void visit(OWLDataExactCardinality ce) {
          visitDataCardinalityRestriction(ce);
        }

        @Override
        public void visit(OWLDataMaxCardinality ce) {
          visitDataCardinalityRestriction(ce);
        }

        private void visitDataCardinalityRestriction(OWLDataCardinalityRestriction ce) {
          var cardinality = ce.getCardinality();
          if (cardinality > 0) {
            addNestedRelatedToAugmentedEdge(subClassNode, ce,
                DATA_PROPERTY_EXPRESSION,
                DATA_RANGE,
                edges);
          }
        }

        @Override
        public void visit(OWLObjectMinCardinality ce) {
          visitObjectCardinalityRestriction(ce);
        }

        @Override
        public void visit(OWLObjectExactCardinality ce) {
          visitObjectCardinalityRestriction(ce);
        }

        @Override
        public void visit(OWLObjectMaxCardinality ce) {
          visitObjectCardinalityRestriction(ce);
        }

        private void visitObjectCardinalityRestriction(OWLObjectCardinalityRestriction ce) {
          var cardinality = ce.getCardinality();
          if (cardinality > 0) {
            addNestedRelatedToAugmentedEdge(subClassNode, ce,
                OBJECT_PROPERTY_EXPRESSION,
                CLASS_EXPRESSION,
                edges);
          }
        }

        private void addNestedRelatedToAugmentedEdge(@Nonnull Node subjectNode,
                                                     @Nonnull OWLRestriction restriction,
                                                     @Nonnull EdgeLabel propertyEdgeLabel,
                                                     @Nonnull EdgeLabel fillerEdgeLabel,
                                                     @Nonnull Builder<Edge> edges) {
          var property = restriction.getProperty();
          if (property.isNamed()) {
            var entity = (OWLEntity) property;
            var translation = classExprTranslator.translate(restriction);
            var propertyNode = translation.findFirstDirectNodeFrom(propertyEdgeLabel);
            var fillerNode = translation.findFirstDirectNodeFrom(fillerEdgeLabel);
            if (propertyNode.isPresent() && fillerNode.isPresent()) {
              addRelatedToAugmentedEdge(subjectNode,
                  fillerNode.get(),
                  Properties.create(ImmutableMap.of(
                      PropertyFields.IRI, propertyNode.get().getProperty(PropertyFields.IRI),
                      PropertyFields.TYPE, entity.getEntityType().getName())),
                  edges);
            }
          }
        }
      });
    }
  }

  private void addRelatedToAugmentedEdge(@Nonnull Node subjectNode,
                                         @Nonnull Node fillerNode,
                                         @Nonnull OWLPropertyExpression propertyExpr,
                                         @Nonnull Builder<Edge> edges) {
    if (propertyExpr.isNamed()) {
      addRelatedToAugmentedEdge(subjectNode, fillerNode, (OWLEntity) propertyExpr, edges);
    }
  }

  private void addRelatedToAugmentedEdge(@Nonnull Node subjectNode,
                                         @Nonnull Node fillerNode,
                                         @Nonnull OWLEntity entity,
                                         @Nonnull Builder<Edge> edges) {
    addRelatedToAugmentedEdge(subjectNode,
        fillerNode,
        Properties.create(ImmutableMap.of(
            PropertyFields.IRI, String.valueOf(entity.getIRI()),
            PropertyFields.TYPE, entity.getEntityType().getName())),
        edges);
  }

  private void addRelatedToAugmentedEdge(@Nonnull Node fromNode,
                                         @Nonnull Node toNode,
                                         @Nonnull Properties properties,
                                         @Nonnull Builder<Edge> edges) {
    addAugmentedEdge(fromNode, toNode, RELATED_TO, properties, edges);
  }

  private void addSymmetricalAugmentedEdges(List<Node> nodes, EdgeLabel edgeLabel, Builder<Edge> edges) {
    for (int i = 0; i < nodes.size(); i++) {
      for (int j = 0; j < nodes.size(); j++) {
        if (i != j) {
          addAugmentedEdge(nodes.get(i), nodes.get(j), edgeLabel, edges);
        }
      }
    }
  }

  private void addAugmentedEdge(@Nonnull Node fromNode,
                                @Nonnull Node toNode,
                                @Nonnull EdgeLabel edgeLabel,
                                @Nonnull Builder<Edge> edges) {
    addAugmentedEdge(fromNode, toNode, edgeLabel, Properties.empty(), edges);
  }

  private void addAugmentedEdge(@Nonnull Node fromNode,
                                @Nonnull Node toNode,
                                @Nonnull EdgeLabel edgeLabel,
                                @Nonnull Properties properties,
                                @Nonnull Builder<Edge> edges) {
    Edge augmentedEdge = edgeFactory.createEdge(fromNode, toNode, edgeLabel, properties);
    edges.add(augmentedEdge);
  }

  /* Other functions */

  private static Translation buildTranslation(@Nonnull OWLAxiom axiom,
                                              @Nonnull Node mainNode,
                                              @Nonnull Builder<Translation> translations,
                                              @Nonnull Builder<Edge> edges) {
    return Translation.create(axiom, mainNode, edges.build(), translations.build());
  }
}
