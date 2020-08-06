package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.AnnotationObjectTranslator;
import edu.stanford.owl2lpg.translator.AnnotationSubjectTranslator;
import edu.stanford.owl2lpg.translator.AnnotationValueTranslator;
import edu.stanford.owl2lpg.translator.ClassExpressionTranslator;
import edu.stanford.owl2lpg.translator.DataRangeTranslator;
import edu.stanford.owl2lpg.translator.EntityTranslator;
import edu.stanford.owl2lpg.translator.IndividualTranslator;
import edu.stanford.owl2lpg.translator.LiteralTranslator;
import edu.stanford.owl2lpg.translator.PropertyExpressionTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ANNOTATION_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.CLASS;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.CLASS_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATATYPE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATA_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATA_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DISJOINT_CLASS_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DOMAIN;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.INVERSE_OBJECT_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.NEXT;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.OBJECT_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.RANGE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SOURCE_INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_ANNOTATION_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_CLASS_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_DATA_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_OBJECT_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUPER_ANNOTATION_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUPER_CLASS_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUPER_DATA_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUPER_OBJECT_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.TARGET_INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.TARGET_VALUE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_PROPERTY_DOMAIN;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_PROPERTY_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ASYMMETRIC_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.CLASS_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATATYPE_DEFINITION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY_DOMAIN;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DECLARATION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DIFFERENT_INDIVIDUALS;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DISJOINT_CLASSES;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DISJOINT_DATA_PROPERTIES;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DISJOINT_OBJECT_PROPERTIES;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DISJOINT_UNION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.EQUIVALENT_CLASSES;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.EQUIVALENT_DATA_PROPERTIES;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.EQUIVALENT_OBJECT_PROPERTIES;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.FUNCTIONAL_DATA_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.FUNCTIONAL_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.HAS_KEY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.INVERSE_FUNCTIONAL_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.INVERSE_OBJECT_PROPERTIES;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.IRREFLEXIVE_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.NEGATIVE_DATA_PROPERTY_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.NEGATIVE_OBJECT_PROPERTY_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_DOMAIN;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.REFLEXIVE_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SUB_CLASS_OF;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SWRL_RULE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SYMMETRIC_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.TRANSITIVE_OBJECT_PROPERTY;

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
  private final StructuralEdgeFactory structuralEdgeFactory;

  @Nonnull
  private final AugmentedEdgeFactory augmentedEdgeFactory;

  @Inject
  public AxiomVisitor(@Nonnull NodeFactory nodeFactory,
                      @Nonnull EntityTranslator entityTranslator,
                      @Nonnull ClassExpressionTranslator classExprTranslator,
                      @Nonnull PropertyExpressionTranslator propertyExprTranslator,
                      @Nonnull DataRangeTranslator dataRangeTranslator,
                      @Nonnull LiteralTranslator literalTranslator,
                      @Nonnull IndividualTranslator individualTranslator,
                      @Nonnull AnnotationObjectTranslator annotationTranslator,
                      @Nonnull AnnotationSubjectTranslator annotationSubjectTranslator,
                      @Nonnull AnnotationValueTranslator annotationValueTranslator,
                      @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                      @Nonnull AugmentedEdgeFactory augmentedEdgeFactory) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.entityTranslator = checkNotNull(entityTranslator);
    this.classExprTranslator = checkNotNull(classExprTranslator);
    this.propertyExprTranslator = checkNotNull(propertyExprTranslator);
    this.dataRangeTranslator = checkNotNull(dataRangeTranslator);
    this.literalTranslator = checkNotNull(literalTranslator);
    this.individualTranslator = checkNotNull(individualTranslator);
    this.annotationTranslator = checkNotNull(annotationTranslator);
    this.annotationSubjectTranslator = checkNotNull(annotationSubjectTranslator);
    this.annotationValueTranslator = checkNotNull(annotationValueTranslator);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.augmentedEdgeFactory = checkNotNull(augmentedEdgeFactory);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDeclarationAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DECLARATION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var entityNode = addEntityTranslationAndStructuralEdge(axiom.getEntity(),
        axiomNode, ENTITY, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, entityNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DATATYPE_DEFINITION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var dataTypeNode = addDataRangeTranslationAndStructuralEdge(axiom.getDatatype(),
        axiomNode, DATATYPE, translations, edges);
    addDataRangeTranslationAndStructuralEdge(axiom.getDataRange(),
        axiomNode, DATA_RANGE, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, dataTypeNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubClassOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, SUB_CLASS_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subClassNode = addClassExprTranslationAndStructuralEdge(axiom.getSubClass(),
        axiomNode, SUB_CLASS_EXPRESSION, translations, edges);
    var superClassNode = addClassExprTranslationAndStructuralEdge(axiom.getSuperClass(),
        axiomNode, SUPER_CLASS_EXPRESSION, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subClassNode, edges);
    addSubClassOfAugmentedEdge(subClassNode, superClassNode, edges);
    addRelatedToAugmentedEdges(subClassNode, axiom.getSuperClass(), edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subjectNode, edges);
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

  @Nonnull
  private Translation translateObjectPropertyCharacteristic(@Nonnull OWLObjectPropertyCharacteristicAxiom axiom,
                                                            @Nonnull NodeLabels nodeLabels) {
    var axiomNode = createAxiomNode(axiom, nodeLabels);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyNode = addPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, classExprNodes, edges);
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

  @Nonnull
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addDomainAugmentedEdge(propertyExprNode, domainNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprNodes, edges);
    addSymmetricalSubObjectPropertyOfAugmentedEdges(propertyExprNodes, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subjectNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, individualNodes, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprNodes, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprNodes, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRangeAugmentedEdge(propertyExprNode, rangeNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRelatedToAugmentedEdge(individualSubjectNode, individualObjectNode, axiom.getProperty(), edges);
    addAxiomSubjectAugmentedEdge(axiomNode, individualSubjectNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addSubObjectPropertyOfAugmentedEdge(subPropertyNode, superPropertyNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subPropertyNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, classExprNodes, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRangeAugmentedEdge(propertyExprNode, rangeNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, EQUIVALENT_DATA_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNodes = addPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprNodes, edges);
    addSymmetricalSubDataPropertyOfAugmentedEdges(propertyExprNodes, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addTypeAugmentedEdge(individualNode, classExprNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, individualNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, EQUIVALENT_CLASSES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var classNodes = addClassExprTranslationsAndStructuralEdges(axiom.getClassExpressions(),
        axiomNode, CLASS_EXPRESSION, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, classNodes, edges);
    addSymmetricalSubClassOfAugmentedEdges(classNodes, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRelatedToAugmentedEdge(individualNode, literalNode, axiom.getProperty(), edges);
    addAxiomSubjectAugmentedEdge(axiomNode, individualNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addSubDataPropertyOfAugmentedEdge(subPropertyNode, superPropertyNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subPropertyNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSameIndividualAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SAME_INDIVIDUAL);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individuals = addIndividualTranslationsAndStructuralEdges(axiom.getIndividuals(),
        axiomNode, INDIVIDUAL, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, individuals, edges);
    addSameIndividualAugmentedEdges(individuals, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, firstChainNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addInverseOfAugmentedEdge(ope1Node, ope2Node, edges);
    addInverseOfAugmentedEdge(ope2Node, ope1Node, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, ope1Node, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, classExprNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRelatedToAugmentedEdge(annotationSubjectNode, annotationValueNode, axiom.getProperty(), edges);
    addAxiomSubjectAugmentedEdge(axiomNode, annotationSubjectNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addSubAnnotationPropertyOfAugmentedEdge(subPropertyNode, superPropertyNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subPropertyNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addDomainAugmentedEdge(propertyNode, domainNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyNode, edges);
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
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRangeAugmentedEdge(propertyNode, rangeNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyNode, edges);
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

  /**
   * Utility methods
   */

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

  @Nonnull
  private static Translation buildTranslation(@Nonnull OWLAxiom axiom,
                                              @Nonnull Node mainNode,
                                              @Nonnull Builder<Translation> translations,
                                              @Nonnull Builder<Edge> edges) {
    return Translation.create(axiom, mainNode, edges.build(), translations.build());
  }

  /**
   * Methods to create translations and structural edges
   */

  private Node addIriTranslationAndStructuralEdge(@Nonnull IRI iri,
                                                  @Nonnull Node axiomNode,
                                                  @Nonnull EdgeLabel edgeLabel,
                                                  @Nonnull Builder<Translation> translations,
                                                  @Nonnull Builder<Edge> edges) {
    var iriTranslation = annotationValueTranslator.translate(iri);
    translations.add(iriTranslation);
    var iriNode = iriTranslation.getMainNode();
    var edge = structuralEdgeFactory.getEntityStructuralEdge(axiomNode, iriNode, edgeLabel);
    edges.add(edge);
    return iriNode;
  }

  private Node addEntityTranslationAndStructuralEdge(@Nonnull OWLEntity entity,
                                                     @Nonnull Node axiomNode,
                                                     @Nonnull EdgeLabel edgeLabel,
                                                     @Nonnull Builder<Translation> translations,
                                                     @Nonnull Builder<Edge> edges) {
    var entityTranslation = entityTranslator.translate(entity);
    translations.add(entityTranslation);
    var entityNode = entityTranslation.getMainNode();
    var edge = structuralEdgeFactory.getEntityStructuralEdge(axiomNode, entityNode, edgeLabel);
    edges.add(edge);
    return entityNode;
  }

  private Stream<Node> addIndividualTranslationsAndStructuralEdges(@Nonnull Set<OWLIndividual> individuals,
                                                                   @Nonnull Node axiomNode,
                                                                   @Nonnull EdgeLabel edgeLabel,
                                                                   @Nonnull Builder<Translation> translations,
                                                                   @Nonnull Builder<Edge> edges) {
    return individuals.stream()
        .map(individual ->
            addIndividualTranslationAndStructuralEdge(individual, axiomNode, edgeLabel, translations, edges));
  }

  private Node addIndividualTranslationAndStructuralEdge(@Nonnull OWLIndividual individual,
                                                         @Nonnull Node axiomNode,
                                                         @Nonnull EdgeLabel edgeLabel,
                                                         @Nonnull Builder<Translation> translations,
                                                         @Nonnull Builder<Edge> edges) {
    var individualTranslation = individualTranslator.translate(individual);
    translations.add(individualTranslation);
    var individualNode = individualTranslation.getMainNode();
    var edge = structuralEdgeFactory.getIndividualStructuralEdge(axiomNode, individualNode, edgeLabel);
    edges.add(edge);
    return individualNode;
  }

  private Node addDataRangeTranslationAndStructuralEdge(@Nonnull OWLDataRange dataRange,
                                                        @Nonnull Node axiomNode,
                                                        @Nonnull EdgeLabel edgeLabel,
                                                        @Nonnull Builder<Translation> translations,
                                                        @Nonnull Builder<Edge> edges) {
    var dataRangeTranslation = dataRangeTranslator.translate(dataRange);
    translations.add(dataRangeTranslation);
    var dataRangeNode = dataRangeTranslation.getMainNode();
    var edge = structuralEdgeFactory.getStructuralEdge(axiomNode, dataRangeNode, edgeLabel);
    edges.add(edge);
    return dataRangeNode;
  }

  private Node addLiteralTranslationAndStructuralEdge(@Nonnull OWLLiteral literal,
                                                      @Nonnull Node axiomNode,
                                                      @Nonnull Builder<Translation> translations,
                                                      @Nonnull Builder<Edge> edges) {
    var literalTranslation = literalTranslator.translate(literal);
    translations.add(literalTranslation);
    var literalNode = literalTranslation.getMainNode();
    var edge = structuralEdgeFactory.getStructuralEdge(axiomNode, literalNode, TARGET_VALUE);
    edges.add(edge);
    return literalNode;
  }

  private Stream<Node> addClassExprTranslationsAndStructuralEdges(@Nonnull Set<OWLClassExpression> classExprs,
                                                                  @Nonnull Node axiomNode,
                                                                  @Nonnull EdgeLabel edgeLabel,
                                                                  @Nonnull Builder<Translation> translations,
                                                                  @Nonnull Builder<Edge> edges) {
    return classExprs.stream()
        .map(classExpr ->
            addClassExprTranslationAndStructuralEdge(classExpr, axiomNode, edgeLabel, translations, edges));
  }

  private Node addClassExprTranslationAndStructuralEdge(@Nonnull OWLClassExpression classExpr,
                                                        @Nonnull Node axiomNode,
                                                        @Nonnull EdgeLabel edgeLabel,
                                                        @Nonnull Builder<Translation> translations,
                                                        @Nonnull Builder<Edge> edges) {
    var classExprTranslation = classExprTranslator.translate(classExpr);
    translations.add(classExprTranslation);
    var classExprNode = classExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getClassExprStructuralEdge(axiomNode, classExprNode, edgeLabel);
    edges.add(edge);
    return classExprNode;
  }

  private Stream<Node> addPropertyExprTranslationsAndStructuralEdges(@Nonnull Set<? extends OWLPropertyExpression> propertyExprs,
                                                                     @Nonnull Node axiomNode,
                                                                     @Nonnull EdgeLabel edgeLabel,
                                                                     @Nonnull Builder<Translation> translations,
                                                                     @Nonnull Builder<Edge> edges) {
    return propertyExprs.stream()
        .map(propertyExpr ->
            addPropertyExprTranslationAndStructuralEdge(propertyExpr, axiomNode, edgeLabel, translations, edges));
  }

  private Node addPropertyExprTranslationAndStructuralEdge(@Nonnull OWLPropertyExpression propertyExpr,
                                                           @Nonnull Node axiomNode,
                                                           @Nonnull EdgeLabel edgeLabel,
                                                           @Nonnull Builder<Translation> translations,
                                                           @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getPropertyExprStructuralEdge(axiomNode, propertyExprNode, edgeLabel);
    edges.add(edge);
    return propertyExprNode;
  }

  private Node addAnnotationSubjectTranslationAndStructuralEdge(@Nonnull OWLAnnotationSubject subject,
                                                                @Nonnull Node axiomNode,
                                                                @Nonnull Builder<Translation> translations,
                                                                @Nonnull Builder<Edge> edges) {
    var annotationSubjectTranslation = annotationSubjectTranslator.translate(subject);
    translations.add(annotationSubjectTranslation);
    var annotationSubjectNode = annotationSubjectTranslation.getMainNode();
    var edge = structuralEdgeFactory.getAnnotationSubjectStructuralEdge(axiomNode, annotationSubjectNode);
    edges.add(edge);
    return annotationSubjectNode;
  }

  private Node addAnnotationValueTranslationAndStructuralEdge(@Nonnull OWLAnnotationValue value,
                                                              @Nonnull Node axiomNode,
                                                              @Nonnull Builder<Translation> translations,
                                                              @Nonnull Builder<Edge> edges) {
    var annotationValueTranslation = annotationValueTranslator.translate(value);
    translations.add(annotationValueTranslation);
    var annotationValueNode = annotationValueTranslation.getMainNode();
    var edge = structuralEdgeFactory.getAnnotationValueStructuralEdge(axiomNode, annotationValueNode);
    edges.add(edge);
    return annotationValueNode;
  }

  private void addAxiomAnnotationTranslationsAndStructuralEdges(@Nonnull Set<OWLAnnotation> annotations,
                                                                @Nonnull Node axiomNode,
                                                                @Nonnull Builder<Translation> translations,
                                                                @Nonnull Builder<Edge> edges) {
    annotations.forEach(annotation ->
        addAxiomAnnotationTranslationsAndStructuralEdge(annotation, axiomNode, translations, edges));
  }

  private void addAxiomAnnotationTranslationsAndStructuralEdge(@Nonnull OWLAnnotation annotation,
                                                               @Nonnull Node axiomNode,
                                                               @Nonnull Builder<Translation> translations,
                                                               @Nonnull Builder<Edge> edges) {
    var axiomAnnotationTranslation = annotationTranslator.translate(annotation);
    translations.add(axiomAnnotationTranslation);
    Node axiomAnnotationNode = axiomAnnotationTranslation.getMainNode();
    var edge = structuralEdgeFactory.getAxiomAnnotationStructuralEdge(axiomNode, axiomAnnotationNode);
    edges.add(edge);
  }

  /**
   * Methods to create augmented edges
   */

  private void addAxiomSubjectAugmentedEdge(Node axiomNode, Node subjectNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getAxiomSubjectAugmentedEdge(axiomNode, subjectNode).ifPresent(edges::add);
  }

  private void addAxiomSubjectAugmentedEdges(Node axiomNode, Stream<Node> subjectNodes, Builder<Edge> edges) {
    augmentedEdgeFactory.getAxiomSubjectAugmentedEdges(axiomNode, subjectNodes).ifPresent(edges::addAll);
  }

  private void addSubClassOfAugmentedEdge(Node subClassNode, Node superClassNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getSubClassOfAugmentedEdge(subClassNode, superClassNode).ifPresent(edges::add);
  }

  private void addSymmetricalSubClassOfAugmentedEdges(Stream<Node> classNodes, Builder<Edge> edges) {
    augmentedEdgeFactory.getSymmetricalSubClassOfAugmentedEdges(classNodes).ifPresent(edges::addAll);
  }

  private void addSubObjectPropertyOfAugmentedEdge(Node subPropertyNode, Node superPropertyOf, Builder<Edge> edges) {
    augmentedEdgeFactory.getSubObjectPropertyOfAugmentedEdge(subPropertyNode, superPropertyOf).ifPresent(edges::add);
  }

  private void addSymmetricalSubObjectPropertyOfAugmentedEdges(Stream<Node> objectPropertyNodes, Builder<Edge> edges) {
    augmentedEdgeFactory.getSymmetricalSubObjectPropertyOfAugmentedEdges(objectPropertyNodes).ifPresent(edges::addAll);
  }

  private void addSubDataPropertyOfAugmentedEdge(Node subPropertyNode, Node superPropertyOf, Builder<Edge> edges) {
    augmentedEdgeFactory.getSubDataPropertyOfAugmentedEdge(subPropertyNode, superPropertyOf).ifPresent(edges::add);
  }

  private void addSymmetricalSubDataPropertyOfAugmentedEdges(Stream<Node> dataPropertyNodes, Builder<Edge> edges) {
    augmentedEdgeFactory.getSymmetricalSubDataPropertyOfAugmentedEdges(dataPropertyNodes).ifPresent(edges::addAll);
  }

  private void addSubAnnotationPropertyOfAugmentedEdge(Node subPropertyNode, Node superPropertyNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getSubAnnotationPropertyOfAugmentedEdge(subPropertyNode, superPropertyNode).ifPresent(edges::add);
  }

  private void addDomainAugmentedEdge(Node propertyNode, Node domainNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getDomainAugmentedEdge(propertyNode, domainNode).ifPresent(edges::add);
  }

  private void addRangeAugmentedEdge(Node propertyNode, Node rangeNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getRangeAugmentedEdge(propertyNode, rangeNode).ifPresent(edges::add);
  }

  private void addInverseOfAugmentedEdge(Node node1, Node node2, Builder<Edge> edges) {
    augmentedEdgeFactory.getInverseOfAugmentedEdge(node1, node2).ifPresent(edges::add);
  }

  private void addSameIndividualAugmentedEdges(Stream<Node> individualNodes, Builder<Edge> edges) {
    augmentedEdgeFactory.getSameIndividualAugmentedEdges(individualNodes).ifPresent(edges::addAll);
  }

  private void addRelatedToAugmentedEdges(Node subClassNode, OWLClassExpression superClassExpresion, Builder<Edge> edges) {
    augmentedEdgeFactory.getRelatedToAugmentedEdges(subClassNode, superClassExpresion).ifPresent(edges::addAll);
  }

  private void addRelatedToAugmentedEdge(Node subjectNode, Node fillerNode, OWLPropertyExpression propertyExpr, Builder<Edge> edges) {
    augmentedEdgeFactory.getRelatedToAugmentedEdge(subjectNode, fillerNode, propertyExpr).ifPresent(edges::add);
  }

  private void addTypeAugmentedEdge(Node subjectNode, Node fillerNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getTypeAugmentedEdge(subjectNode, fillerNode).ifPresent(edges::add);
  }
}
