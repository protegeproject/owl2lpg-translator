package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import edu.stanford.owl2lpg.model.AugmentedEdgeFactory;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.model.StructuralEdgeFactory;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.AnnotationObjectTranslator;
import edu.stanford.owl2lpg.translator.AnnotationSubjectTranslator;
import edu.stanford.owl2lpg.translator.AnnotationValueTranslator;
import edu.stanford.owl2lpg.translator.ClassExpressionTranslator;
import edu.stanford.owl2lpg.translator.DataRangeTranslator;
import edu.stanford.owl2lpg.translator.EntityTranslator;
import edu.stanford.owl2lpg.translator.IndividualTranslator;
import edu.stanford.owl2lpg.translator.LiteralTranslator;
import edu.stanford.owl2lpg.translator.PropertyExpressionTranslator;
import edu.stanford.owl2lpg.translator.shared.BytesDigester;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectSerializer;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
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
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ENTITY;
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
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.PROPERTY_CHAIN;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.REFLEXIVE_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SUB_CLASS_OF;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SWRL_RULE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SYMMETRIC_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.TRANSITIVE_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.DIGEST;

/**
 * A visitor that contains the implementation to translate the OWL 2 axioms.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomVisitor implements OWLAxiomVisitorEx<Translation> {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final StructuralEdgeFactory structuralEdgeFactory;

  @Nonnull
  private final AugmentedEdgeFactory augmentedEdgeFactory;

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
  private final OntologyObjectSerializer ontologyObjectSerializer;

  @Nonnull
  private final BytesDigester bytesDigester;

  @Inject
  public AxiomVisitor(@Nonnull NodeFactory nodeFactory,
                      @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                      @Nonnull AugmentedEdgeFactory augmentedEdgeFactory,
                      @Nonnull EntityTranslator entityTranslator,
                      @Nonnull ClassExpressionTranslator classExprTranslator,
                      @Nonnull PropertyExpressionTranslator propertyExprTranslator,
                      @Nonnull DataRangeTranslator dataRangeTranslator,
                      @Nonnull LiteralTranslator literalTranslator,
                      @Nonnull IndividualTranslator individualTranslator,
                      @Nonnull AnnotationObjectTranslator annotationTranslator,
                      @Nonnull AnnotationSubjectTranslator annotationSubjectTranslator,
                      @Nonnull AnnotationValueTranslator annotationValueTranslator,
                      @Nonnull OntologyObjectSerializer ontologyObjectSerializer,
                      @Nonnull BytesDigester bytesDigester) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.augmentedEdgeFactory = checkNotNull(augmentedEdgeFactory);
    this.entityTranslator = checkNotNull(entityTranslator);
    this.classExprTranslator = checkNotNull(classExprTranslator);
    this.propertyExprTranslator = checkNotNull(propertyExprTranslator);
    this.dataRangeTranslator = checkNotNull(dataRangeTranslator);
    this.literalTranslator = checkNotNull(literalTranslator);
    this.individualTranslator = checkNotNull(individualTranslator);
    this.annotationTranslator = checkNotNull(annotationTranslator);
    this.annotationSubjectTranslator = checkNotNull(annotationSubjectTranslator);
    this.annotationValueTranslator = checkNotNull(annotationValueTranslator);
    this.ontologyObjectSerializer = checkNotNull(ontologyObjectSerializer);
    this.bytesDigester = checkNotNull(bytesDigester);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDeclarationAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DECLARATION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var entityTranslation = addEntityTranslationAndStructuralEdge(axiom.getEntity(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, entityTranslation, edges);
    addInAxiomSignatureAugmentedEdge(entityTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DATATYPE_DEFINITION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var dataTypeTranslation = addDataTypeTranslationAndStructuralEdge(axiom.getDatatype(),
        axiomNode, translations, edges);
    var dataRangeTranslation = addDataRangeTranslationAndStructuralEdge(axiom.getDataRange(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, dataTypeTranslation, edges);
    addInAxiomSignatureAugmentedEdge(dataTypeTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(dataRangeTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubClassOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, SUB_CLASS_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subClassTranslation = addSubClassExprTranslationAndStructuralEdge(axiom.getSubClass(),
        axiomNode, translations, edges);
    var superClassTranslation = addSuperClassExprTranslationAndStructuralEdge(axiom.getSuperClass(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subClassTranslation, edges);
    addSubClassOfAugmentedEdge(subClassTranslation, superClassTranslation, edges);
    addRelatedToAugmentedEdges(subClassTranslation, superClassTranslation, edges);
    addInAxiomSignatureAugmentedEdge(subClassTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(superClassTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NEGATIVE_OBJECT_PROPERTY_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subjectTranslation = addSourceIndividualTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, translations, edges);
    var propertyExprTranslation = addObjectPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var individualTranslation = addTargetIndividualTranslationAndStructuralEdge(axiom.getObject(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subjectTranslation, edges);
    addInAxiomSignatureAugmentedEdge(subjectTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(propertyExprTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(individualTranslation, axiomNode, edges);
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
    var propertyTranslation = addObjectPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyTranslation, edges);
    addInAxiomSignatureAugmentedEdge(propertyTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointClassesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DISJOINT_CLASSES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var classExprTranslations = addClassExprTranslationsAndStructuralEdges(axiom.getClassExpressions(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, classExprTranslations, edges);
    addInAxiomSignatureAugmentedEdges(classExprTranslations, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DATA_PROPERTY_DOMAIN);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprTranslation = addDataPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var domainTranslation = addDomainTranslationAndStructuralEdge(axiom.getDomain(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addHasDomainAugmentedEdge(propertyExprTranslation, domainTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprTranslation, edges);
    addInAxiomSignatureAugmentedEdge(propertyExprTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(domainTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, OBJECT_PROPERTY_DOMAIN);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprTranslation = addObjectPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var domainTranslation = addDomainTranslationAndStructuralEdge(axiom.getDomain(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addHasDomainAugmentedEdge(propertyExprTranslation, domainTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprTranslation, edges);
    addInAxiomSignatureAugmentedEdge(propertyExprTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(domainTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, EQUIVALENT_OBJECT_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprTranslations = addObjectPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprTranslations, edges);
    addSymmetricalSubObjectPropertyOfAugmentedEdges(propertyExprTranslations, edges);
    addInAxiomSignatureAugmentedEdges(propertyExprTranslations, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NEGATIVE_DATA_PROPERTY_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subjectTranslation = addSourceIndividualTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, translations, edges);
    var propertyExprTranslation = addDataPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    addTargetValueTranslationAndStructuralEdge(axiom.getObject(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subjectTranslation, edges);
    addInAxiomSignatureAugmentedEdge(subjectTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(propertyExprTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DIFFERENT_INDIVIDUALS);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualTranslations = addIndividualTranslationsAndStructuralEdges(axiom.getIndividuals(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, individualTranslations, edges);
    addInAxiomSignatureAugmentedEdges(individualTranslations, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DISJOINT_DATA_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprTranslations = addDataPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprTranslations, edges);
    addInAxiomSignatureAugmentedEdges(propertyExprTranslations, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DISJOINT_OBJECT_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprTranslations = addObjectPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprTranslations, edges);
    addInAxiomSignatureAugmentedEdges(propertyExprTranslations, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, OBJECT_PROPERTY_RANGE);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprTranslation = addObjectPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var rangeTranslation = addRangeTranslationAndStructuralEdge(axiom.getRange(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRangeAugmentedEdge(propertyExprTranslation, rangeTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprTranslation, edges);
    addInAxiomSignatureAugmentedEdge(propertyExprTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(rangeTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, OBJECT_PROPERTY_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualSubjectTranslation = addSourceIndividualTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, translations, edges);
    var propertyTranslation = addObjectPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var individualObjectTranslation = addTargetIndividualTranslationAndStructuralEdge(axiom.getObject(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRelatedToAugmentedEdge(individualSubjectTranslation, individualObjectTranslation, propertyTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, individualSubjectTranslation, edges);
    addInAxiomSignatureAugmentedEdge(individualSubjectTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(propertyTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(individualObjectTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subPropertyTranslation = addSubObjectPropertyExprTranslationAndStructuralEdge(axiom.getSubProperty(),
        axiomNode, translations, edges);
    var superPropertyTranslation = addSuperObjectPropertyExprTranslationAndStructuralEdge(axiom.getSuperProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addSubObjectPropertyOfAugmentedEdge(subPropertyTranslation, superPropertyTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subPropertyTranslation, edges);
    addInAxiomSignatureAugmentedEdge(subPropertyTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(superPropertyTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointUnionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DISJOINT_UNION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var classTranslation = addClassTranslationAndStructuralEdge(axiom.getOWLClass(),
        axiomNode, translations, edges);
    var classExprTranslations = addDisjointClassExprTranslationsAndStructuralEdges(axiom.getClassExpressions(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, classExprTranslations, edges);
    addInAxiomSignatureAugmentedEdge(classTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdges(classExprTranslations, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DATA_PROPERTY_RANGE);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprTranslation = addDataPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var rangeTranslation = addRangeTranslationAndStructuralEdge(axiom.getRange(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRangeAugmentedEdge(propertyExprTranslation, rangeTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprTranslation, edges);
    addInAxiomSignatureAugmentedEdge(propertyExprTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(rangeTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, FUNCTIONAL_DATA_PROPERTY);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprTranslation = addDataPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprTranslation, edges);
    addInAxiomSignatureAugmentedEdge(propertyExprTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, EQUIVALENT_DATA_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprTranslations = addDataPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprTranslations, edges);
    addSymmetricalSubDataPropertyOfAugmentedEdges(propertyExprTranslations, edges);
    addInAxiomSignatureAugmentedEdges(propertyExprTranslations, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClassAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, CLASS_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualTranslation = addIndividualTranslationAndStructuralEdge(axiom.getIndividual(),
        axiomNode, translations, edges);
    var classExprTranslation = addClassExprTranslationAndStructuralEdge(axiom.getClassExpression(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addTypeAugmentedEdge(individualTranslation, classExprTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, individualTranslation, edges);
    addInAxiomSignatureAugmentedEdge(individualTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(classExprTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, EQUIVALENT_CLASSES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var classTranslations = addClassExprTranslationsAndStructuralEdges(axiom.getClassExpressions(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, classTranslations, edges);
    addSymmetricalSubClassOfAugmentedEdges(classTranslations, edges);
    addInAxiomSignatureAugmentedEdges(classTranslations, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DATA_PROPERTY_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualTranslation = addSourceIndividualTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, translations, edges);
    var propertyExprTranslation = addDataPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var literalTranslation = addTargetValueTranslationAndStructuralEdge(axiom.getObject(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRelatedToAugmentedEdge(individualTranslation, literalTranslation, propertyExprTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, individualTranslation, edges);
    addInAxiomSignatureAugmentedEdge(individualTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(propertyExprTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_DATA_PROPERTY_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subPropertyTranslation = addSubDataPropertyExprTranslationAndStructuralEdge(axiom.getSubProperty(),
        axiomNode, translations, edges);
    var superPropertyTranslation = addSuperDataPropertyExprTranslationAndStructuralEdge(axiom.getSuperProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addSubDataPropertyOfAugmentedEdge(subPropertyTranslation, superPropertyTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subPropertyTranslation, edges);
    addInAxiomSignatureAugmentedEdge(subPropertyTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(superPropertyTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSameIndividualAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SAME_INDIVIDUAL);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualTranslations = addIndividualTranslationsAndStructuralEdges(axiom.getIndividuals(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, individualTranslations, edges);
    addSameIndividualAugmentedEdges(individualTranslations, edges);
    addInAxiomSignatureAugmentedEdges(individualTranslations, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_PROPERTY_CHAIN_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyChain = OWLPropertyChain.create(ImmutableList.copyOf(axiom.getPropertyChain()));
    addSubObjectPropertyChainTranslationAndEdge(propertyChain,
        axiomNode, translations, edges);
    var superPropertyTranslation = addSuperObjectPropertyExprTranslationAndStructuralEdge(axiom.getSuperProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addInAxiomSignatureAugmentedEdge(superPropertyTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  private void addSubObjectPropertyChainTranslationAndEdge(@Nonnull OWLPropertyChain propertyChain,
                                                           @Nonnull Node axiomNode,
                                                           @Nonnull Builder<Translation> translations,
                                                           @Nonnull Builder<Edge> edges) {
    var propertyChainTranslations = newTranslationBuilder();
    var propertyChainEdges = newEdgesBuilder();
    var propertyChainNode = addPropertyChainTranslationAndEdge(propertyChain, propertyChainTranslations, propertyChainEdges);
    var propertyChainTranslation = Translation.create(propertyChain, propertyChainNode, propertyChainEdges.build(), propertyChainTranslations.build());
    var subObjectPropertyOfEdge = structuralEdgeFactory.getSubObjectPropertyExpressionEdge(axiomNode, propertyChainNode);
    addInAxiomSignatureAugmentedEdge(propertyChainTranslation, axiomNode, edges);
    translations.add(propertyChainTranslation);
    edges.add(subObjectPropertyOfEdge);
  }

  @Nonnull
  private Node addPropertyChainTranslationAndEdge(@Nonnull OWLPropertyChain propertyChain,
                                                  @Nonnull Builder<Translation> propertyChainTranslations,
                                                  @Nonnull Builder<Edge> propertyChainEdges) {
    var propertyChainNode = nodeFactory.createNode(propertyChain, PROPERTY_CHAIN);
    var pos = 1;
    for (var propertyExpr : propertyChain) {
      var propertyExprTranslation = addObjectPropertyExpressionTranslation(propertyExpr, propertyChainTranslations);
      addNextObjectPropertyExpressionStructuralEdge(propertyChainNode, propertyExprTranslation, pos, propertyChainEdges);
      pos++;
    }
    return propertyChainNode;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, INVERSE_OBJECT_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprTranslation1 = addObjectPropertyExprTranslationAndStructuralEdge(axiom.getFirstProperty(),
        axiomNode, translations, edges);
    var propertyExprTranslation2 = addInverseObjectPropertyExprTranslationAndStructuralEdge(axiom.getSecondProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addInverseOfAugmentedEdge(propertyExprTranslation1, propertyExprTranslation2, edges);
    addInverseOfAugmentedEdge(propertyExprTranslation2, propertyExprTranslation1, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprTranslation1, edges);
    addInAxiomSignatureAugmentedEdge(propertyExprTranslation1, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(propertyExprTranslation2, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLHasKeyAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, HAS_KEY);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var classExprTranslation = addClassExprTranslationAndStructuralEdge(axiom.getClassExpression(),
        axiomNode, translations, edges);
    var objectPropertyExprTranslations = addObjectPropertyExprTranslationsAndStructuralEdges(axiom.getObjectPropertyExpressions(),
        axiomNode, translations, edges);
    var dataPropertyExprTranslations = addDataPropertyExprTranslationsAndStructuralEdges(axiom.getDataPropertyExpressions(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, classExprTranslation, edges);
    addInAxiomSignatureAugmentedEdge(classExprTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdges(objectPropertyExprTranslations, axiomNode, edges);
    addInAxiomSignatureAugmentedEdges(dataPropertyExprTranslations, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, ANNOTATION_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var annotationSubjectTranslation = addAnnotationSubjectTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, translations, edges);
    var annotationPropertyTranslation = addAnnotationPropertyTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var annotationValueTranslation = addAnnotationValueTranslationAndStructuralEdge(axiom.getValue(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRelatedToAugmentedEdge(annotationSubjectTranslation, annotationValueTranslation, annotationPropertyTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, annotationSubjectTranslation, edges);
    addInAxiomSignatureAugmentedEdge(annotationPropertyTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_ANNOTATION_PROPERTY_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subPropertyTranslation = addSubAnnotationPropertyTranslationAndStructuralEdge(axiom.getSubProperty(),
        axiomNode, translations, edges);
    var superPropertyTranslation = addSuperAnnotationPropertyTranslationAndStructuralEdge(axiom.getSuperProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addSubAnnotationPropertyOfAugmentedEdge(subPropertyTranslation, superPropertyTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subPropertyTranslation, edges);
    addInAxiomSignatureAugmentedEdge(subPropertyTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(superPropertyTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, ANNOTATION_PROPERTY_DOMAIN);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyTranslation = addAnnotationPropertyTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var domainTranslation = addDomainTranslationAndStructuralEdge(axiom.getDomain(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addHasDomainAugmentedEdge(propertyTranslation, domainTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyTranslation, edges);
    addInAxiomSignatureAugmentedEdge(propertyTranslation, axiomNode, edges);
    addInAxiomSignatureAugmentedEdge(domainTranslation, axiomNode, edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, ANNOTATION_PROPERTY_RANGE);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyTranslation = addAnnotationPropertyTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var rangeTranslation = addRangeTranslationAndStructuralEdge(axiom.getRange(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRangeAugmentedEdge(propertyTranslation, rangeTranslation, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyTranslation, edges);
    addInAxiomSignatureAugmentedEdge(propertyTranslation, axiomNode, edges);
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
    var digestString = bytesDigester.getDigestString(ontologyObjectSerializer.serialize(axiom));
    return nodeFactory.createNode(axiom, nodeLabels, Properties.of(DIGEST, digestString));
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
  private static Translation buildTranslation(OWLAxiom axiom, Node
      mainNode, Builder<Translation> translations, Builder<Edge> edges) {
    return Translation.create(axiom, mainNode, edges.build(), translations.build());
  }

  /**
   * Methods to create translations and structural edges
   */

  private Translation addClassTranslationAndStructuralEdge(@Nonnull OWLClass cls,
                                                           @Nonnull Node axiomNode,
                                                           @Nonnull Builder<Translation> translations,
                                                           @Nonnull Builder<Edge> edges) {
    var classTranslation = addEntityTranslation(cls, translations);
    addClassStructuralEdge(axiomNode, classTranslation, edges);
    return classTranslation;
  }

  private Translation addEntityTranslationAndStructuralEdge(@Nonnull OWLEntity entity,
                                                            @Nonnull Node axiomNode,
                                                            @Nonnull Builder<Translation> translations,
                                                            @Nonnull Builder<Edge> edges) {
    var entityTranslation = addEntityTranslation(entity, translations);
    addEntityStructuralEdge(axiomNode, entityTranslation, edges);
    return entityTranslation;
  }

  private List<Translation> addIndividualTranslationsAndStructuralEdges(@Nonnull Set<OWLIndividual> individuals,
                                                                        @Nonnull Node axiomNode,
                                                                        @Nonnull Builder<Translation> translations,
                                                                        @Nonnull Builder<Edge> edges) {
    return individuals.stream()
        .map(individual ->
            addIndividualTranslationAndStructuralEdge(individual, axiomNode, translations, edges))
        .collect(ImmutableList.toImmutableList());
  }

  private Translation addIndividualTranslationAndStructuralEdge(@Nonnull OWLIndividual individual,
                                                                @Nonnull Node axiomNode,
                                                                @Nonnull Builder<Translation> translations,
                                                                @Nonnull Builder<Edge> edges) {
    var individualTranslation = addIndividualTranslation(individual, translations);
    addIndividualStructuralEdge(axiomNode, individualTranslation, edges);
    return individualTranslation;
  }

  private Translation addSourceIndividualTranslationAndStructuralEdge(@Nonnull OWLIndividual individual,
                                                                      @Nonnull Node axiomNode,
                                                                      @Nonnull Builder<Translation> translations,
                                                                      @Nonnull Builder<Edge> edges) {
    var individualTranslation = addIndividualTranslation(individual, translations);
    addSourceIndividualStructuralEdge(axiomNode, individualTranslation, edges);
    return individualTranslation;
  }

  private Translation addTargetIndividualTranslationAndStructuralEdge(@Nonnull OWLIndividual individual,
                                                                      @Nonnull Node axiomNode,
                                                                      @Nonnull Builder<Translation> translations,
                                                                      @Nonnull Builder<Edge> edges) {
    var individualTranslation = addIndividualTranslation(individual, translations);
    addTargetIndividualStructuralEdge(axiomNode, individualTranslation, edges);
    return individualTranslation;
  }

  private Translation addDataRangeTranslationAndStructuralEdge(@Nonnull OWLDataRange dataRange,
                                                               @Nonnull Node axiomNode,
                                                               @Nonnull Builder<Translation> translations,
                                                               @Nonnull Builder<Edge> edges) {
    var dataRangeTranslation = addDataRangeTranslation(dataRange, translations);
    addDataRangeStructuralEdge(axiomNode, dataRangeTranslation, edges);
    return dataRangeTranslation;
  }

  private Translation addDataTypeTranslationAndStructuralEdge(@Nonnull OWLDatatype datatype,
                                                              @Nonnull Node axiomNode,
                                                              @Nonnull Builder<Translation> translations,
                                                              @Nonnull Builder<Edge> edges) {
    var dataTypeTranslation = addDataTypeTranslation(datatype, translations);
    addDataTypeStructuralEdge(axiomNode, dataTypeTranslation, edges);
    return dataTypeTranslation;
  }

  private Translation addTargetValueTranslationAndStructuralEdge(@Nonnull OWLLiteral literal,
                                                                 @Nonnull Node axiomNode,
                                                                 @Nonnull Builder<Translation> translations,
                                                                 @Nonnull Builder<Edge> edges) {
    Translation literalTranslation = addLiteralTranslation(literal, translations);
    addTargetValueStructuralEdge(axiomNode, literalTranslation, edges);
    return literalTranslation;
  }

  private List<Translation> addClassExprTranslationsAndStructuralEdges(@Nonnull Set<OWLClassExpression> classExprs,
                                                                       @Nonnull Node axiomNode,
                                                                       @Nonnull Builder<Translation> translations,
                                                                       @Nonnull Builder<Edge> edges) {
    return classExprs.stream()
        .map(classExpr ->
            addClassExprTranslationAndStructuralEdge(classExpr, axiomNode, translations, edges))
        .collect(ImmutableList.toImmutableList());
  }

  private Translation addClassExprTranslationAndStructuralEdge(@Nonnull OWLClassExpression classExpr,
                                                               @Nonnull Node axiomNode,
                                                               @Nonnull Builder<Translation> translations,
                                                               @Nonnull Builder<Edge> edges) {
    var classExprTranslation = addClassExpressionTranslation(classExpr, translations);
    addClassExpressionStructuralEdge(axiomNode, classExprTranslation, edges);
    return classExprTranslation;
  }

  private Translation addSubClassExprTranslationAndStructuralEdge(@Nonnull OWLClassExpression classExpr,
                                                                  @Nonnull Node axiomNode,
                                                                  @Nonnull Builder<Translation> translations,
                                                                  @Nonnull Builder<Edge> edges) {
    var classExprTranslation = addClassExpressionTranslation(classExpr, translations);
    addSubClassExpressionStructuralEdge(axiomNode, classExprTranslation, edges);
    return classExprTranslation;
  }

  private Translation addSuperClassExprTranslationAndStructuralEdge(@Nonnull OWLClassExpression classExpr,
                                                                    @Nonnull Node axiomNode,
                                                                    @Nonnull Builder<Translation> translations,
                                                                    @Nonnull Builder<Edge> edges) {
    var classExprTranslation = addClassExpressionTranslation(classExpr, translations);
    addSuperClassExpressionStructuralEdge(axiomNode, classExprTranslation, edges);
    return classExprTranslation;
  }

  private Translation addSubDataPropertyExprTranslationAndStructuralEdge(@Nonnull OWLDataPropertyExpression propertyExpr,
                                                                         @Nonnull Node axiomNode,
                                                                         @Nonnull Builder<Translation> translations,
                                                                         @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = addDataPropertyExpressionTranslation(propertyExpr, translations);
    addSubDataPropertyExpressionStructuralEdge(axiomNode, propertyExprTranslation, edges);
    return propertyExprTranslation;
  }

  private Translation addSuperDataPropertyExprTranslationAndStructuralEdge(@Nonnull OWLDataPropertyExpression propertyExpr,
                                                                           @Nonnull Node axiomNode,
                                                                           @Nonnull Builder<Translation> translations,
                                                                           @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = addDataPropertyExpressionTranslation(propertyExpr, translations);
    addSuperDataPropertyExpressionStructuralEdge(axiomNode, propertyExprTranslation, edges);
    return propertyExprTranslation;
  }

  private Translation addSubObjectPropertyExprTranslationAndStructuralEdge(@Nonnull OWLObjectPropertyExpression propertyExpr,
                                                                           @Nonnull Node axiomNode,
                                                                           @Nonnull Builder<Translation> translations,
                                                                           @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = addObjectPropertyExpressionTranslation(propertyExpr, translations);
    addSubObjectPropertyExpressionStructuralEdge(axiomNode, propertyExprTranslation, edges);
    return propertyExprTranslation;
  }

  private Translation addSuperObjectPropertyExprTranslationAndStructuralEdge(@Nonnull OWLObjectPropertyExpression propertyExpr,
                                                                             @Nonnull Node axiomNode,
                                                                             @Nonnull Builder<Translation> translations,
                                                                             @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = addObjectPropertyExpressionTranslation(propertyExpr, translations);
    addSuperObjectPropertyExpressionStructuralEdge(axiomNode, propertyExprTranslation, edges);
    return propertyExprTranslation;
  }

  private Translation addSubAnnotationPropertyTranslationAndStructuralEdge(@Nonnull OWLAnnotationProperty annotationProperty,
                                                                           @Nonnull Node axiomNode,
                                                                           @Nonnull Builder<Translation> translations,
                                                                           @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = addAnnotationPropertyTranslation(annotationProperty, translations);
    addSubAnnotationPropertyStructuralEdge(axiomNode, propertyExprTranslation, edges);
    return propertyExprTranslation;
  }

  private Translation addSuperAnnotationPropertyTranslationAndStructuralEdge(@Nonnull OWLAnnotationProperty annotationProperty,
                                                                             @Nonnull Node axiomNode,
                                                                             @Nonnull Builder<Translation> translations,
                                                                             @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = addAnnotationPropertyTranslation(annotationProperty, translations);
    addSuperAnnotationPropertyStructuralEdge(axiomNode, propertyExprTranslation, edges);
    return propertyExprTranslation;
  }

  private List<Translation>
  addDisjointClassExprTranslationsAndStructuralEdges(@Nonnull Set<OWLClassExpression> classExprs,
                                                     @Nonnull Node axiomNode,
                                                     @Nonnull Builder<Translation> translations,
                                                     @Nonnull Builder<Edge> edges) {
    return classExprs.stream()
        .map(classExpr ->
            addDisjointClassExprTranslationAndStructuralEdge(classExpr, axiomNode, translations, edges))
        .collect(ImmutableList.toImmutableList());
  }

  private Translation addDisjointClassExprTranslationAndStructuralEdge(@Nonnull OWLClassExpression classExpr,
                                                                       @Nonnull Node axiomNode,
                                                                       @Nonnull Builder<Translation> translations,
                                                                       @Nonnull Builder<Edge> edges) {
    var classExprTranslation = addClassExpressionTranslation(classExpr, translations);
    addDisjointClassExpressionStructuralEdge(axiomNode, classExprTranslation, edges);
    return classExprTranslation;
  }

  private List<Translation>
  addObjectPropertyExprTranslationsAndStructuralEdges(@Nonnull Set<OWLObjectPropertyExpression> propertyExprs,
                                                      @Nonnull Node axiomNode,
                                                      @Nonnull Builder<Translation> translations,
                                                      @Nonnull Builder<Edge> edges) {
    return propertyExprs.stream()
        .map(propertyExpr ->
            addObjectPropertyExprTranslationAndStructuralEdge(propertyExpr, axiomNode, translations, edges))
        .collect(ImmutableList.toImmutableList());
  }

  private Translation addObjectPropertyExprTranslationAndStructuralEdge(@Nonnull OWLObjectPropertyExpression propertyExpr,
                                                                        @Nonnull Node axiomNode,
                                                                        @Nonnull Builder<Translation> translations,
                                                                        @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = addObjectPropertyExpressionTranslation(propertyExpr, translations);
    addObjectPropertyExpressionStructuralEdge(axiomNode, propertyExprTranslation, edges);
    return propertyExprTranslation;
  }

  private List<Translation>
  addDataPropertyExprTranslationsAndStructuralEdges(@Nonnull Set<OWLDataPropertyExpression> propertyExprs,
                                                    @Nonnull Node axiomNode,
                                                    @Nonnull Builder<Translation> translations,
                                                    @Nonnull Builder<Edge> edges) {
    return propertyExprs.stream()
        .map(propertyExpr ->
            addDataPropertyExprTranslationAndStructuralEdge(propertyExpr, axiomNode, translations, edges))
        .collect(ImmutableList.toImmutableList());
  }

  private Translation addDataPropertyExprTranslationAndStructuralEdge(@Nonnull OWLDataPropertyExpression propertyExpr,
                                                                      @Nonnull Node axiomNode,
                                                                      @Nonnull Builder<Translation> translations,
                                                                      @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = addDataPropertyExpressionTranslation(propertyExpr, translations);
    addDataPropertyExpressionStructuralEdge(axiomNode, propertyExprTranslation, edges);
    return propertyExprTranslation;
  }

  private Translation addInverseObjectPropertyExprTranslationAndStructuralEdge(@Nonnull OWLObjectPropertyExpression propertyExpr,
                                                                               @Nonnull Node mainNode,
                                                                               @Nonnull Builder<Translation> translations,
                                                                               @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = addObjectPropertyExpressionTranslation(propertyExpr, translations);
    addInverseObjectPropertyExprStructuralEdge(mainNode, propertyExprTranslation, edges);
    return propertyExprTranslation;
  }

  private Translation addDomainTranslationAndStructuralEdge(@Nonnull IRI domain,
                                                            @Nonnull Node axiomNode,
                                                            @Nonnull Builder<Translation> translations,
                                                            @Nonnull Builder<Edge> edges) {
    Translation domainTranslation = addIriTranslation(domain, translations);
    addDomainStructuralEdge(axiomNode, domainTranslation, edges);
    return domainTranslation;
  }

  private Translation addDomainTranslationAndStructuralEdge(@Nonnull OWLClassExpression domain,
                                                            @Nonnull Node axiomNode,
                                                            @Nonnull Builder<Translation> translations,
                                                            @Nonnull Builder<Edge> edges) {
    var domainTranslation = addClassExpressionTranslation(domain, translations);
    addDomainStructuralEdge(axiomNode, domainTranslation, edges);
    return domainTranslation;
  }

  private Translation addRangeTranslationAndStructuralEdge(@Nonnull OWLClassExpression range,
                                                           @Nonnull Node axiomNode,
                                                           @Nonnull Builder<Translation> translations,
                                                           @Nonnull Builder<Edge> edges) {
    var rangeTranslation = addClassExpressionTranslation(range, translations);
    addRangeStructuralEdge(axiomNode, rangeTranslation, edges);
    return rangeTranslation;
  }

  private Translation addRangeTranslationAndStructuralEdge(@Nonnull IRI range,
                                                           @Nonnull Node axiomNode,
                                                           @Nonnull Builder<Translation> translations,
                                                           @Nonnull Builder<Edge> edges) {
    var rangeTranslation = addIriTranslation(range, translations);
    addRangeStructuralEdge(axiomNode, rangeTranslation, edges);
    return rangeTranslation;
  }

  private Translation addRangeTranslationAndStructuralEdge(@Nonnull OWLDataRange range,
                                                           @Nonnull Node axiomNode,
                                                           @Nonnull Builder<Translation> translations,
                                                           @Nonnull Builder<Edge> edges) {
    var rangeTranslation = addDataRangeTranslation(range, translations);
    addRangeStructuralEdge(axiomNode, rangeTranslation, edges);
    return rangeTranslation;
  }

  private Translation addAnnotationSubjectTranslationAndStructuralEdge(@Nonnull OWLAnnotationSubject subject,
                                                                       @Nonnull Node axiomNode,
                                                                       @Nonnull Builder<Translation> translations,
                                                                       @Nonnull Builder<Edge> edges) {
    var annotationSubjectTranslation = addAnnotationSubjectTranslation(subject, translations);
    addAnnotationSubjectStructuralEdge(axiomNode, annotationSubjectTranslation, edges);
    return annotationSubjectTranslation;
  }

  private Translation addAnnotationPropertyTranslationAndStructuralEdge(@Nonnull OWLAnnotationProperty property,
                                                                        @Nonnull Node axiomNode,
                                                                        @Nonnull Builder<Translation> translations,
                                                                        @Nonnull Builder<Edge> edges) {
    var annotationPropertyTranslation = addAnnotationPropertyTranslation(property, translations);
    addAnnotationPropertyStructuralEdge(axiomNode, annotationPropertyTranslation, edges);
    return annotationPropertyTranslation;
  }

  private Translation addAnnotationValueTranslationAndStructuralEdge(@Nonnull OWLAnnotationValue value,
                                                                     @Nonnull Node axiomNode,
                                                                     @Nonnull Builder<Translation> translations,
                                                                     @Nonnull Builder<Edge> edges) {
    Translation annotationValueTranslation = addAnnotationValueTranslation(value, translations);
    addAnnotationValueStructuralEdge(axiomNode, annotationValueTranslation, edges);
    return annotationValueTranslation;
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
    var edge = structuralEdgeFactory.getAxiomAnnotationEdge(axiomNode, axiomAnnotationNode);
    edges.add(edge);
  }

  /**
   * Helper methods to create and add translations
   */

  @Nonnull
  private Translation addIriTranslation(IRI iri, Builder<Translation> translations) {
    return addAnnotationValueTranslation(iri, translations);
  }

  @Nonnull
  private Translation addAnnotationValueTranslation(OWLAnnotationValue value, Builder<Translation> translations) {
    var annotationValueTranslation = annotationValueTranslator.translate(value);
    translations.add(annotationValueTranslation);
    return annotationValueTranslation;
  }

  @Nonnull
  private Translation addEntityTranslation(OWLEntity entity, Builder<Translation> translations) {
    var entityTranslation = entityTranslator.translate(entity);
    translations.add(entityTranslation);
    return entityTranslation;
  }

  @Nonnull
  private Translation addClassExpressionTranslation(OWLClassExpression classExpr, Builder<Translation> translations) {
    var classExprTranslation = classExprTranslator.translate(classExpr);
    translations.add(classExprTranslation);
    return classExprTranslation;
  }

  @Nonnull
  private Translation addObjectPropertyExpressionTranslation(OWLObjectPropertyExpression propertyExpr, Builder<Translation> translations) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    return propertyExprTranslation;
  }

  @Nonnull
  private Translation addDataPropertyExpressionTranslation(OWLDataPropertyExpression propertyExpr, Builder<Translation> translations) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    return propertyExprTranslation;
  }

  @Nonnull
  private Translation addAnnotationPropertyTranslation(OWLAnnotationProperty annotationProperty, Builder<Translation> translations) {
    var propertyTranslation = propertyExprTranslator.translate(annotationProperty);
    translations.add(propertyTranslation);
    return propertyTranslation;
  }

  @Nonnull
  private Translation addIndividualTranslation(OWLIndividual individual, Builder<Translation> translations) {
    var individualTranslation = individualTranslator.translate(individual);
    translations.add(individualTranslation);
    return individualTranslation;
  }

  @Nonnull
  private Translation addLiteralTranslation(OWLLiteral literal, Builder<Translation> translations) {
    var literalTranslation = literalTranslator.translate(literal);
    translations.add(literalTranslation);
    return literalTranslation;
  }

  @Nonnull
  private Translation addDataTypeTranslation(OWLDatatype datatype, Builder<Translation> translations) {
    return addDataRangeTranslation(datatype, translations);
  }

  @Nonnull
  private Translation addDataRangeTranslation(OWLDataRange dataRange, Builder<Translation> translations) {
    var dataRangeTranslation = dataRangeTranslator.translate(dataRange);
    translations.add(dataRangeTranslation);
    return dataRangeTranslation;
  }

  @Nonnull
  private Translation addAnnotationSubjectTranslation(OWLAnnotationSubject subject, Builder<Translation> translations) {
    var annotationSubjectTranslation = annotationSubjectTranslator.translate(subject);
    translations.add(annotationSubjectTranslation);
    return annotationSubjectTranslation;
  }

  /**
   * Helper methods to create and add structural edges
   */

  private void addClassStructuralEdge(Node axiomNode, Translation classTranslation, Builder<Edge> edges) {
    var classNode = classTranslation.getMainNode();
    var edge = structuralEdgeFactory.getClassEdge(axiomNode, classNode);
    edges.add(edge);
  }

  private void addEntityStructuralEdge(Node axiomNode, Translation entityTranslation, Builder<Edge> edges) {
    var entityNode = entityTranslation.getMainNode();
    var edge = structuralEdgeFactory.getEntityEdge(axiomNode, entityNode);
    edges.add(edge);
  }

  private void addClassExpressionStructuralEdge(Node axiomNode, Translation classExprTranslation, Builder<Edge> edges) {
    var classExprNode = classExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getClassExpressionEdge(axiomNode, classExprNode);
    edges.add(edge);
  }

  private void addDisjointClassExpressionStructuralEdge(Node axiomNode, Translation classExprTranslation, Builder<Edge> edges) {
    var classExprNode = classExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getDisjointClassExpressionEdge(axiomNode, classExprNode);
    edges.add(edge);
  }

  private void addSubClassExpressionStructuralEdge(Node axiomNode, Translation classExprTranslation, Builder<Edge> edges) {
    var classExprNode = classExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSubClassExpressionEdge(axiomNode, classExprNode);
    edges.add(edge);
  }

  private void addSuperClassExpressionStructuralEdge(Node axiomNode, Translation classExprTranslation, Builder<Edge> edges) {
    var classExprNode = classExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSuperClassExpressionEdge(axiomNode, classExprNode);
    edges.add(edge);
  }

  private void addObjectPropertyExpressionStructuralEdge(Node axiomNode, Translation propertyExprTranslation, Builder<Edge> edges) {
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getObjectPropertyExpressionEdge(axiomNode, propertyExprNode);
    edges.add(edge);
  }

  private void addInverseObjectPropertyExprStructuralEdge(Node mainNode, Translation propertyExprTranslation, Builder<Edge> edges) {
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getInverseObjectPropertyExpressionEdge(mainNode, propertyExprNode);
    edges.add(edge);
  }

  private void addSubObjectPropertyExpressionStructuralEdge(Node axiomNode, Translation propertyExprTranslation, Builder<Edge> edges) {
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSubObjectPropertyExpressionEdge(axiomNode, propertyExprNode);
    edges.add(edge);
  }

  private void addSuperObjectPropertyExpressionStructuralEdge(Node axiomNode, Translation propertyExprTranslation, Builder<Edge> edges) {
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSuperObjectPropertyExpressionEdge(axiomNode, propertyExprNode);
    edges.add(edge);
  }

  private void addDataPropertyExpressionStructuralEdge(Node axiomNode, Translation propertyExprTranslation, Builder<Edge> edges) {
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getDataPropertyExpressionEdge(axiomNode, propertyExprNode);
    edges.add(edge);
  }

  private void addSubDataPropertyExpressionStructuralEdge(Node axiomNode, Translation propertyExprTranslation, Builder<Edge> edges) {
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSubDataPropertyExpressionEdge(axiomNode, propertyExprNode);
    edges.add(edge);
  }

  private void addSuperDataPropertyExpressionStructuralEdge(Node axiomNode, Translation propertyExprTranslation, Builder<Edge> edges) {
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSuperDataPropertyExpressionEdge(axiomNode, propertyExprNode);
    edges.add(edge);
  }

  private void addDataTypeStructuralEdge(Node axiomNode, Translation dataTypeTranslation, Builder<Edge> edges) {
    var dataTypeNode = dataTypeTranslation.getMainNode();
    var edge = structuralEdgeFactory.getDataTypeEdge(axiomNode, dataTypeNode);
    edges.add(edge);
  }

  private void addAnnotationPropertyStructuralEdge(Node axiomNode, Translation annotationPropertyTranslation, Builder<Edge> edges) {
    var annotationPropertyNode = annotationPropertyTranslation.getMainNode();
    var edge = structuralEdgeFactory.getAnnotationPropertyEdge(axiomNode, annotationPropertyNode);
    edges.add(edge);
  }

  private void addSubAnnotationPropertyStructuralEdge(Node axiomNode, Translation propertyExprTranslation, Builder<Edge> edges) {
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSubAnnotationPropertyEdge(axiomNode, propertyExprNode);
    edges.add(edge);
  }

  private void addSuperAnnotationPropertyStructuralEdge(Node axiomNode, Translation propertyExprTranslation, Builder<Edge> edges) {
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSuperAnnotationPropertyEdge(axiomNode, propertyExprNode);
    edges.add(edge);
  }

  private void addDataRangeStructuralEdge(Node axiomNode, Translation dataRangeTranslation, Builder<Edge> edges) {
    var dataRangeNode = dataRangeTranslation.getMainNode();
    var edge = structuralEdgeFactory.getDataRangeEdge(axiomNode, dataRangeNode);
    edges.add(edge);
  }

  private void addDomainStructuralEdge(Node axiomNode, Translation domainTranslation, Builder<Edge> edges) {
    var domainNode = domainTranslation.getMainNode();
    var edge = structuralEdgeFactory.getDomainEdge(axiomNode, domainNode);
    edges.add(edge);
  }

  private void addRangeStructuralEdge(Node axiomNode, Translation rangeTranslation, Builder<Edge> edges) {
    var rangeNode = rangeTranslation.getMainNode();
    var edge = structuralEdgeFactory.getRangeEdge(axiomNode, rangeNode);
    edges.add(edge);
  }

  private void addIndividualStructuralEdge(Node axiomNode, Translation individualTranslation, Builder<Edge> edges) {
    var individualNode = individualTranslation.getMainNode();
    var edge = structuralEdgeFactory.getIndividualEdge(axiomNode, individualNode);
    edges.add(edge);
  }

  private void addSourceIndividualStructuralEdge(Node axiomNode, Translation individualTranslation, Builder<Edge> edges) {
    var individualNode = individualTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSourceIndividualEdge(axiomNode, individualNode);
    edges.add(edge);
  }

  private void addTargetValueStructuralEdge(Node axiomNode, Translation literalTranslation, Builder<Edge> edges) {
    var literalNode = literalTranslation.getMainNode();
    var edge = structuralEdgeFactory.getTargetValueEdge(axiomNode, literalNode);
    edges.add(edge);
  }

  private void addTargetIndividualStructuralEdge(Node axiomNode, Translation individualTranslation, Builder<Edge> edges) {
    var individualNode = individualTranslation.getMainNode();
    var edge = structuralEdgeFactory.getTargetIndividualEdge(axiomNode, individualNode);
    edges.add(edge);
  }

  private void addAnnotationSubjectStructuralEdge(Node axiomNode, Translation annotationSubjectTranslation, Builder<Edge> edges) {
    var annotationSubjectNode = annotationSubjectTranslation.getMainNode();
    var edge = structuralEdgeFactory.getAnnotationSubjectEdge(axiomNode, annotationSubjectNode);
    edges.add(edge);
  }

  private void addAnnotationValueStructuralEdge(Node axiomNode, Translation annotationValueTranslation, Builder<Edge> edges) {
    var annotationValueNode = annotationValueTranslation.getMainNode();
    var edge = structuralEdgeFactory.getAnnotationValueEdge(axiomNode, annotationValueNode);
    edges.add(edge);
  }

  private void addNextObjectPropertyExpressionStructuralEdge(Node propertyChainNode, Translation propertyExprTranslation, int position, Builder<Edge> edges) {
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getNextObjectPropertyExpressionEdge(propertyChainNode, propertyExprNode, position);
    edges.add(edge);
  }

  /**
   * Helper methods to create and add augmented edges
   */

  private void addInAxiomSignatureAugmentedEdge(Translation translation, Node axiomNode, Builder<Edge> edges) {
    translation.nodes()
        .filter(node -> node.getLabels().isa(ENTITY))
        .map(entityNode -> augmentedEdgeFactory.getInAxiomSignatureEdge(entityNode, axiomNode))
        .forEach(entityNode -> entityNode.ifPresent(edges::add));
  }

  private void addInAxiomSignatureAugmentedEdges(List<Translation> translations, Node axiomNode, Builder<Edge> edges) {
    translations.forEach(translation -> addInAxiomSignatureAugmentedEdge(translation, axiomNode, edges));
  }

  private void addAxiomSubjectAugmentedEdge(Node axiomNode, Translation subjectTranslation, Builder<Edge> edges) {
    var subjectNode = subjectTranslation.getMainNode();
    augmentedEdgeFactory.getAxiomSubjectEdge(axiomNode, subjectNode).ifPresent(edges::add);
  }

  private void addAxiomSubjectAugmentedEdges(Node axiomNode, List<Translation> subjectTranslations, Builder<Edge> edges) {
    subjectTranslations.forEach(subjectTranslation -> addAxiomSubjectAugmentedEdge(axiomNode, subjectTranslation, edges));
  }

  private void addSubClassOfAugmentedEdge(Translation subClassTranslation, Translation superClassTranslation, Builder<Edge> edges) {
    var subClassNode = subClassTranslation.getMainNode();
    var superClassNode = superClassTranslation.getMainNode();
    augmentedEdgeFactory.getSubClassOfEdge(subClassNode, superClassNode).ifPresent(edges::add);
  }

  private void addSymmetricalSubClassOfAugmentedEdges(List<Translation> classTranslations, Builder<Edge> edges) {
    var classNodes = classTranslations.stream()
        .map(Translation::getMainNode)
        .collect(ImmutableList.toImmutableList());
    augmentedEdgeFactory.getSymmetricalSubClassOfEdges(classNodes).ifPresent(edges::addAll);
  }

  private void addSubObjectPropertyOfAugmentedEdge(Translation subPropertyTranslation, Translation superPropertyTranslation, Builder<Edge> edges) {
    var subPropertyNode = subPropertyTranslation.getMainNode();
    var superPropertyNode = superPropertyTranslation.getMainNode();
    augmentedEdgeFactory.getSubObjectPropertyOfEdge(subPropertyNode, superPropertyNode).ifPresent(edges::add);
  }

  private void addSymmetricalSubObjectPropertyOfAugmentedEdges(List<Translation> objectPropertyTranslations, Builder<Edge> edges) {
    var objectPropertyNodes = objectPropertyTranslations.stream()
        .map(Translation::getMainNode)
        .collect(ImmutableList.toImmutableList());
    augmentedEdgeFactory.getSymmetricalSubObjectPropertyOfEdges(objectPropertyNodes).ifPresent(edges::addAll);
  }

  private void addSubDataPropertyOfAugmentedEdge(Translation subPropertyTranslation, Translation superPropertyTranslation, Builder<Edge> edges) {
    var subPropertyNode = subPropertyTranslation.getMainNode();
    var superPropertyNode = superPropertyTranslation.getMainNode();
    augmentedEdgeFactory.getSubDataPropertyOfEdge(subPropertyNode, superPropertyNode).ifPresent(edges::add);
  }

  private void addSymmetricalSubDataPropertyOfAugmentedEdges(List<Translation> dataPropertyTranslations, Builder<Edge> edges) {
    var dataPropertyNodes = dataPropertyTranslations.stream()
        .map(Translation::getMainNode)
        .collect(ImmutableList.toImmutableList());
    augmentedEdgeFactory.getSymmetricalSubDataPropertyOfEdges(dataPropertyNodes).ifPresent(edges::addAll);
  }

  private void addSubAnnotationPropertyOfAugmentedEdge(Translation subPropertyTranslation, Translation superPropertyTranslation, Builder<Edge> edges) {
    var subPropertyNode = subPropertyTranslation.getMainNode();
    var superPropertyNode = superPropertyTranslation.getMainNode();
    augmentedEdgeFactory.getSubAnnotationPropertyOfEdge(subPropertyNode, superPropertyNode).ifPresent(edges::add);
  }

  private void addHasDomainAugmentedEdge(Translation propertyTranslation, Translation domainTranslation, Builder<Edge> edges) {
    var domainNode = domainTranslation.getMainNode();
    var propertyNode = propertyTranslation.getMainNode();
    augmentedEdgeFactory.getHasDomainEdge(propertyNode, domainNode).ifPresent(edges::add);
  }

  private void addRangeAugmentedEdge(Translation propertyTranslation, Translation rangeTranslation, Builder<Edge> edges) {
    var propertyNode = propertyTranslation.getMainNode();
    var rangeNode = rangeTranslation.getMainNode();
    augmentedEdgeFactory.getHasRangeEdge(propertyNode, rangeNode).ifPresent(edges::add);
  }

  private void addInverseOfAugmentedEdge(Translation nodeTranslation1, Translation nodeTranslation2, Builder<Edge> edges) {
    var node1 = nodeTranslation1.getMainNode();
    var node2 = nodeTranslation2.getMainNode();
    augmentedEdgeFactory.getInverseOfEdge(node1, node2).ifPresent(edges::add);
  }

  private void addSameIndividualAugmentedEdges(List<Translation> individualTranslations, Builder<Edge> edges) {
    var individualNodes = individualTranslations.stream()
        .map(Translation::getMainNode)
        .collect(ImmutableList.toImmutableList());
    augmentedEdgeFactory.getSameIndividualEdges(individualNodes).ifPresent(edges::addAll);
  }

  private void addRelatedToAugmentedEdges(Translation subClassTranslation, Translation superClassTranslation, Builder<Edge> edges) {
    var subClassNode = subClassTranslation.getMainNode();
    var superClassExpression = (OWLClassExpression) superClassTranslation.getTranslatedObject();
    augmentedEdgeFactory.getRelatedToEdges(subClassNode, superClassExpression).ifPresent(edges::addAll);
  }

  private void addRelatedToAugmentedEdge(Translation subjectTranslation, Translation fillerTranslation, Translation propertyTranslation, Builder<Edge> edges) {
    var subjectNode = subjectTranslation.getMainNode();
    var fillerNode = fillerTranslation.getMainNode();
    var propertyExpr = (OWLPropertyExpression) propertyTranslation.getTranslatedObject();
    augmentedEdgeFactory.getRelatedToEdge(subjectNode, fillerNode, propertyExpr).ifPresent(edges::add);
  }

  private void addTypeAugmentedEdge(Translation subjectTranslation, Translation fillerTranslation, Builder<Edge> edges) {
    var subjectNode = subjectTranslation.getMainNode();
    var fillerNode = fillerTranslation.getMainNode();
    augmentedEdgeFactory.getTypeEdge(subjectNode, fillerNode).ifPresent(edges::add);
  }
}
