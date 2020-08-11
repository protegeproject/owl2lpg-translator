package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
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

  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final VersioningContextNodeFactory versioningContextNodeFactory;

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

  @Inject
  public AxiomVisitor(@Nonnull OntologyDocumentId ontoDocId,
                      @Nonnull NodeFactory nodeFactory,
                      @Nonnull VersioningContextNodeFactory versioningContextNodeFactory,
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
                      @Nonnull AnnotationValueTranslator annotationValueTranslator) {
    this.ontoDocId = checkNotNull(ontoDocId);
    this.nodeFactory = checkNotNull(nodeFactory);
    this.versioningContextNodeFactory = checkNotNull(versioningContextNodeFactory);
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
  }

  @Nonnull
  private Node createOntologyDocumentNode() {
    return versioningContextNodeFactory.createOntologyDocumentNode(ontoDocId);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDeclarationAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DECLARATION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var entityNode = addEntityTranslationAndStructuralEdge(axiom.getEntity(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, entityNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DATATYPE_DEFINITION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var dataTypeNode = addDataTypeTranslationAndStructuralEdge(axiom.getDatatype(),
        axiomNode, translations, edges);
    addDataRangeTranslationAndStructuralEdge(axiom.getDataRange(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, dataTypeNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubClassOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, SUB_CLASS_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subClassNode = addSubClassExprTranslationAndStructuralEdge(axiom.getSubClass(),
        axiomNode, translations, edges);
    var superClassNode = addSuperClassExprTranslationAndStructuralEdge(axiom.getSuperClass(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subClassNode, edges);
    addSubClassOfAugmentedEdge(subClassNode, superClassNode, edges);
    addRelatedToAugmentedEdges(subClassNode, axiom.getSuperClass(), edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NEGATIVE_OBJECT_PROPERTY_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subjectNode = addSourceIndividualTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, translations, edges);
    addObjectPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    addTargetIndividualTranslationAndStructuralEdge(axiom.getObject(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subjectNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
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
    var propertyNode = addObjectPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointClassesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DISJOINT_CLASSES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var classExprNodes = addClassExprTranslationsAndStructuralEdges(axiom.getClassExpressions(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, classExprNodes, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DATA_PROPERTY_DOMAIN);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNode = addDataPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var domainNode = addDomainTranslationAndStructuralEdge(axiom.getDomain(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addDomainAugmentedEdge(propertyExprNode, domainNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, OBJECT_PROPERTY_DOMAIN);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNode = addObjectPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var domainNode = addDomainTranslationAndStructuralEdge(axiom.getDomain(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addDomainAugmentedEdge(propertyExprNode, domainNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, EQUIVALENT_OBJECT_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNodes = addObjectPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprNodes, edges);
    addSymmetricalSubObjectPropertyOfAugmentedEdges(propertyExprNodes, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NEGATIVE_DATA_PROPERTY_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subjectNode = addSourceIndividualTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, translations, edges);
    addDataPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    addTargetValueTranslationAndStructuralEdge(axiom.getObject(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subjectNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DIFFERENT_INDIVIDUALS);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualNodes = addIndividualTranslationsAndStructuralEdges(axiom.getIndividuals(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, individualNodes, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DISJOINT_DATA_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNodes = addDataPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprNodes, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DISJOINT_OBJECT_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNodes = addObjectPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprNodes, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, OBJECT_PROPERTY_RANGE);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNode = addObjectPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var rangeNode = addRangeTranslationAndStructuralEdge(axiom.getRange(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRangeAugmentedEdge(propertyExprNode, rangeNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, OBJECT_PROPERTY_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualSubjectNode = addSourceIndividualTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, translations, edges);
    addObjectPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var individualObjectNode = addTargetIndividualTranslationAndStructuralEdge(axiom.getObject(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRelatedToAugmentedEdge(individualSubjectNode, individualObjectNode, axiom.getProperty(), edges);
    addAxiomSubjectAugmentedEdge(axiomNode, individualSubjectNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subPropertyNode = addSubObjectPropertyExprTranslationAndStructuralEdge(axiom.getSubProperty(),
        axiomNode, translations, edges);
    var superPropertyNode = addSuperObjectPropertyExprTranslationAndStructuralEdge(axiom.getSuperProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addSubObjectPropertyOfAugmentedEdge(subPropertyNode, superPropertyNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subPropertyNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointUnionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DISJOINT_UNION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    addClassTranslationAndStructuralEdge(axiom.getOWLClass(),
        axiomNode, translations, edges);
    var classExprNodes = addDisjointClassExprTranslationsAndStructuralEdges(axiom.getClassExpressions(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, classExprNodes, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DATA_PROPERTY_RANGE);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNode = addDataPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var rangeNode = addRangeTranslationAndStructuralEdge(axiom.getRange(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRangeAugmentedEdge(propertyExprNode, rangeNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyExprNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, FUNCTIONAL_DATA_PROPERTY);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyNode = addDataPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, EQUIVALENT_DATA_PROPERTIES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyExprNodes = addDataPropertyExprTranslationsAndStructuralEdges(axiom.getProperties(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, propertyExprNodes, edges);
    addSymmetricalSubDataPropertyOfAugmentedEdges(propertyExprNodes, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClassAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, CLASS_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualNode = addIndividualTranslationAndStructuralEdge(axiom.getIndividual(),
        axiomNode, translations, edges);
    var classExprNode = addClassExprTranslationAndStructuralEdge(axiom.getClassExpression(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addTypeAugmentedEdge(individualNode, classExprNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, individualNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, EQUIVALENT_CLASSES);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var classNodes = addClassExprTranslationsAndStructuralEdges(axiom.getClassExpressions(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, classNodes, edges);
    addSymmetricalSubClassOfAugmentedEdges(classNodes, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, DATA_PROPERTY_ASSERTION);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individualNode = addSourceIndividualTranslationAndStructuralEdge(axiom.getSubject(),
        axiomNode, translations, edges);
    addDataPropertyExprTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var literalNode = addTargetValueTranslationAndStructuralEdge(axiom.getObject(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRelatedToAugmentedEdge(individualNode, literalNode, axiom.getProperty(), edges);
    addAxiomSubjectAugmentedEdge(axiomNode, individualNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_DATA_PROPERTY_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subPropertyNode = addSubDataPropertyExprTranslationAndStructuralEdge(axiom.getSubProperty(),
        axiomNode, translations, edges);
    var superPropertyNode = addSuperDataPropertyExprTranslationAndStructuralEdge(axiom.getSuperProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addSubDataPropertyOfAugmentedEdge(subPropertyNode, superPropertyNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subPropertyNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSameIndividualAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SAME_INDIVIDUAL);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var individuals = addIndividualTranslationsAndStructuralEdges(axiom.getIndividuals(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdges(axiomNode, individuals, edges);
    addSameIndividualAugmentedEdges(individuals, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
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
    addSuperObjectPropertyExprTranslationAndStructuralEdge(axiom.getSuperProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, firstChainNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  private Node addPropertyChainTranslationAndEdge(@Nonnull List<OWLObjectPropertyExpression> propertyChain,
                                                  @Nonnull Node axiomNode,
                                                  @Nonnull Builder<Translation> translations,
                                                  @Nonnull Builder<Edge> edges) {
    var firstChainNode = addSubObjectPropertyExprTranslationAndStructuralEdge(propertyChain.get(0),
        axiomNode, translations, edges);
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
      var nextMainNode = addNextTranslationAndStructuralEdge(propertyChain.get(0),
          mainNode, translations, edges);
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
    var ope1Node = addObjectPropertyExprTranslationAndStructuralEdge(axiom.getFirstProperty(),
        axiomNode, translations, edges);
    var ope2Node = addInverseObjectPropertyExprTranslationAndStructuralEdge(axiom.getSecondProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addInverseOfAugmentedEdge(ope1Node, ope2Node, edges);
    addInverseOfAugmentedEdge(ope2Node, ope1Node, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, ope1Node, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLHasKeyAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, HAS_KEY);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var classExprNode = addClassExprTranslationAndStructuralEdge(axiom.getClassExpression(),
        axiomNode, translations, edges);
    addObjectPropertyExprTranslationsAndStructuralEdges(axiom.getObjectPropertyExpressions(),
        axiomNode, translations, edges);
    addDataPropertyExprTranslationsAndStructuralEdges(axiom.getDataPropertyExpressions(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, classExprNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
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
    addAnnotationPropertyTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var annotationValueNode = addAnnotationValueTranslationAndStructuralEdge(axiom.getValue(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRelatedToAugmentedEdge(annotationSubjectNode, annotationValueNode, axiom.getProperty(), edges);
    addAxiomSubjectAugmentedEdge(axiomNode, annotationSubjectNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_ANNOTATION_PROPERTY_OF);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var subPropertyNode = addSubAnnotationPropertyTranslationAndStructuralEdge(axiom.getSubProperty(),
        axiomNode, translations, edges);
    var superPropertyNode = addSuperAnnotationPropertyTranslationAndStructuralEdge(axiom.getSuperProperty(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addSubAnnotationPropertyOfAugmentedEdge(subPropertyNode, superPropertyNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, subPropertyNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, ANNOTATION_PROPERTY_DOMAIN);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyNode = addAnnotationPropertyTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var domainNode = addDomainTranslationAndStructuralEdge(axiom.getDomain(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addDomainAugmentedEdge(propertyNode, domainNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
    return buildTranslation(axiom, axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
    var axiomNode = createAxiomNode(axiom, ANNOTATION_PROPERTY_RANGE);
    var translations = newTranslationBuilder();
    var edges = newEdgesBuilder();
    var propertyNode = addAnnotationPropertyTranslationAndStructuralEdge(axiom.getProperty(),
        axiomNode, translations, edges);
    var rangeNode = addRangeTranslationAndStructuralEdge(axiom.getRange(),
        axiomNode, translations, edges);
    addAxiomAnnotationTranslationsAndStructuralEdges(axiom.getAnnotations(),
        axiomNode, translations, edges);
    addRangeAugmentedEdge(propertyNode, rangeNode, edges);
    addAxiomSubjectAugmentedEdge(axiomNode, propertyNode, edges);
    addAxiomOfAugmentedEdge(axiomNode, createOntologyDocumentNode(), edges);
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
  private static Translation buildTranslation(OWLAxiom axiom, Node mainNode, Builder<Translation> translations, Builder<Edge> edges) {
    return Translation.create(axiom, mainNode, edges.build(), translations.build());
  }

  /**
   * Methods to create translations and structural edges
   */

  private void addClassTranslationAndStructuralEdge(@Nonnull OWLClass cls,
                                                    @Nonnull Node axiomNode,
                                                    @Nonnull Builder<Translation> translations,
                                                    @Nonnull Builder<Edge> edges) {
    var classTranslation = entityTranslator.translate(cls);
    translations.add(classTranslation);
    var classNode = classTranslation.getMainNode();
    var edge = structuralEdgeFactory.getClassEdge(axiomNode, classNode);
    edges.add(edge);
  }

  private Node addEntityTranslationAndStructuralEdge(@Nonnull OWLEntity entity,
                                                     @Nonnull Node axiomNode,
                                                     @Nonnull Builder<Translation> translations,
                                                     @Nonnull Builder<Edge> edges) {
    var entityTranslation = entityTranslator.translate(entity);
    translations.add(entityTranslation);
    var entityNode = entityTranslation.getMainNode();
    var edge = structuralEdgeFactory.getEntityEdge(axiomNode, entityNode);
    edges.add(edge);
    return entityNode;
  }

  private List<Node> addIndividualTranslationsAndStructuralEdges(@Nonnull Set<OWLIndividual> individuals,
                                                                 @Nonnull Node axiomNode,
                                                                 @Nonnull Builder<Translation> translations,
                                                                 @Nonnull Builder<Edge> edges) {
    return individuals.stream()
        .map(individual ->
            addIndividualTranslationAndStructuralEdge(individual, axiomNode, translations, edges))
        .collect(ImmutableList.toImmutableList());
  }

  private Node addIndividualTranslationAndStructuralEdge(@Nonnull OWLIndividual individual,
                                                         @Nonnull Node axiomNode,
                                                         @Nonnull Builder<Translation> translations,
                                                         @Nonnull Builder<Edge> edges) {
    var individualTranslation = individualTranslator.translate(individual);
    translations.add(individualTranslation);
    var individualNode = individualTranslation.getMainNode();
    var edge = structuralEdgeFactory.getIndividualEdge(axiomNode, individualNode);
    edges.add(edge);
    return individualNode;
  }

  private Node addSourceIndividualTranslationAndStructuralEdge(@Nonnull OWLIndividual individual,
                                                               @Nonnull Node axiomNode,
                                                               @Nonnull Builder<Translation> translations,
                                                               @Nonnull Builder<Edge> edges) {
    var individualTranslation = individualTranslator.translate(individual);
    translations.add(individualTranslation);
    var individualNode = individualTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSourceIndividualEdge(axiomNode, individualNode);
    edges.add(edge);
    return individualNode;
  }

  private Node addTargetIndividualTranslationAndStructuralEdge(@Nonnull OWLIndividual individual,
                                                               @Nonnull Node axiomNode,
                                                               @Nonnull Builder<Translation> translations,
                                                               @Nonnull Builder<Edge> edges) {
    var individualTranslation = individualTranslator.translate(individual);
    translations.add(individualTranslation);
    var individualNode = individualTranslation.getMainNode();
    var edge = structuralEdgeFactory.getTargetIndividualEdge(axiomNode, individualNode);
    edges.add(edge);
    return individualNode;
  }

  private void addDataRangeTranslationAndStructuralEdge(@Nonnull OWLDataRange dataRange,
                                                        @Nonnull Node axiomNode,
                                                        @Nonnull Builder<Translation> translations,
                                                        @Nonnull Builder<Edge> edges) {
    var dataRangeTranslation = dataRangeTranslator.translate(dataRange);
    translations.add(dataRangeTranslation);
    var dataRangeNode = dataRangeTranslation.getMainNode();
    var edge = structuralEdgeFactory.getDataRangeEdge(axiomNode, dataRangeNode);
    edges.add(edge);
  }

  private Node addDataTypeTranslationAndStructuralEdge(@Nonnull OWLDatatype datatype,
                                                       @Nonnull Node axiomNode,
                                                       @Nonnull Builder<Translation> translations,
                                                       @Nonnull Builder<Edge> edges) {
    var dataTypeTranslation = dataRangeTranslator.translate(datatype);
    translations.add(dataTypeTranslation);
    var dataTypeNode = dataTypeTranslation.getMainNode();
    var edge = structuralEdgeFactory.getDataTypeEdge(axiomNode, dataTypeNode);
    edges.add(edge);
    return dataTypeNode;
  }

  private Node addTargetValueTranslationAndStructuralEdge(@Nonnull OWLLiteral literal,
                                                          @Nonnull Node axiomNode,
                                                          @Nonnull Builder<Translation> translations,
                                                          @Nonnull Builder<Edge> edges) {
    var literalTranslation = literalTranslator.translate(literal);
    translations.add(literalTranslation);
    var literalNode = literalTranslation.getMainNode();
    var edge = structuralEdgeFactory.getTargetValueEdge(axiomNode, literalNode);
    edges.add(edge);
    return literalNode;
  }

  private List<Node> addClassExprTranslationsAndStructuralEdges(@Nonnull Set<OWLClassExpression> classExprs,
                                                                @Nonnull Node axiomNode,
                                                                @Nonnull Builder<Translation> translations,
                                                                @Nonnull Builder<Edge> edges) {
    return classExprs.stream()
        .map(classExpr ->
            addClassExprTranslationAndStructuralEdge(classExpr, axiomNode, translations, edges))
        .collect(ImmutableList.toImmutableList());
  }

  private Node addClassExprTranslationAndStructuralEdge(@Nonnull OWLClassExpression classExpr,
                                                        @Nonnull Node axiomNode,
                                                        @Nonnull Builder<Translation> translations,
                                                        @Nonnull Builder<Edge> edges) {
    var classExprTranslation = classExprTranslator.translate(classExpr);
    translations.add(classExprTranslation);
    var classExprNode = classExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getClassExpressionEdge(axiomNode, classExprNode);
    edges.add(edge);
    return classExprNode;
  }

  private Node addSubClassExprTranslationAndStructuralEdge(@Nonnull OWLClassExpression classExpr,
                                                           @Nonnull Node axiomNode,
                                                           @Nonnull Builder<Translation> translations,
                                                           @Nonnull Builder<Edge> edges) {
    var classExprTranslation = classExprTranslator.translate(classExpr);
    translations.add(classExprTranslation);
    var classExprNode = classExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSubClassExpressionEdge(axiomNode, classExprNode);
    edges.add(edge);
    return classExprNode;
  }

  private Node addSuperClassExprTranslationAndStructuralEdge(@Nonnull OWLClassExpression classExpr,
                                                             @Nonnull Node axiomNode,
                                                             @Nonnull Builder<Translation> translations,
                                                             @Nonnull Builder<Edge> edges) {
    var classExprTranslation = classExprTranslator.translate(classExpr);
    translations.add(classExprTranslation);
    var classExprNode = classExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSuperClassExpressionEdge(axiomNode, classExprNode);
    edges.add(edge);
    return classExprNode;
  }

  private Node addSubDataPropertyExprTranslationAndStructuralEdge(@Nonnull OWLDataPropertyExpression propertyExpr,
                                                                  @Nonnull Node axiomNode,
                                                                  @Nonnull Builder<Translation> translations,
                                                                  @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSubDataPropertyExpressionEdge(axiomNode, propertyExprNode);
    edges.add(edge);
    return propertyExprNode;
  }

  private Node addSuperDataPropertyExprTranslationAndStructuralEdge(@Nonnull OWLDataPropertyExpression propertyExpr,
                                                                    @Nonnull Node axiomNode,
                                                                    @Nonnull Builder<Translation> translations,
                                                                    @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSuperDataPropertyExpressionEdge(axiomNode, propertyExprNode);
    edges.add(edge);
    return propertyExprNode;
  }

  private Node addSubObjectPropertyExprTranslationAndStructuralEdge(@Nonnull OWLObjectPropertyExpression propertyExpr,
                                                                    @Nonnull Node axiomNode,
                                                                    @Nonnull Builder<Translation> translations,
                                                                    @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSubObjectPropertyExpressionEdge(axiomNode, propertyExprNode);
    edges.add(edge);
    return propertyExprNode;
  }

  private Node addSuperObjectPropertyExprTranslationAndStructuralEdge(@Nonnull OWLObjectPropertyExpression propertyExpr,
                                                                      @Nonnull Node axiomNode,
                                                                      @Nonnull Builder<Translation> translations,
                                                                      @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSuperObjectPropertyExpressionEdge(axiomNode, propertyExprNode);
    edges.add(edge);
    return propertyExprNode;
  }

  private Node addSubAnnotationPropertyTranslationAndStructuralEdge(@Nonnull OWLAnnotationProperty propertyExpr,
                                                                    @Nonnull Node axiomNode,
                                                                    @Nonnull Builder<Translation> translations,
                                                                    @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSubAnnotationPropertyEdge(axiomNode, propertyExprNode);
    edges.add(edge);
    return propertyExprNode;
  }

  private Node addSuperAnnotationPropertyTranslationAndStructuralEdge(@Nonnull OWLAnnotationProperty propertyExpr,
                                                                      @Nonnull Node axiomNode,
                                                                      @Nonnull Builder<Translation> translations,
                                                                      @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getSuperAnnotationPropertyEdge(axiomNode, propertyExprNode);
    edges.add(edge);
    return propertyExprNode;
  }

  private List<Node> addDisjointClassExprTranslationsAndStructuralEdges(@Nonnull Set<OWLClassExpression> classExprs,
                                                                        @Nonnull Node axiomNode,
                                                                        @Nonnull Builder<Translation> translations,
                                                                        @Nonnull Builder<Edge> edges) {
    return classExprs.stream()
        .map(classExpr ->
            addDisjointClassExprTranslationAndStructuralEdge(classExpr, axiomNode, translations, edges))
        .collect(ImmutableList.toImmutableList());
  }

  private Node addDisjointClassExprTranslationAndStructuralEdge(@Nonnull OWLClassExpression classExpr,
                                                                @Nonnull Node axiomNode,
                                                                @Nonnull Builder<Translation> translations,
                                                                @Nonnull Builder<Edge> edges) {
    var classExprTranslation = classExprTranslator.translate(classExpr);
    translations.add(classExprTranslation);
    var classExprNode = classExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getDisjointClassExpressionEdge(axiomNode, classExprNode);
    edges.add(edge);
    return classExprNode;
  }

  private List<Node> addObjectPropertyExprTranslationsAndStructuralEdges(@Nonnull Set<OWLObjectPropertyExpression> propertyExprs,
                                                                         @Nonnull Node axiomNode,
                                                                         @Nonnull Builder<Translation> translations,
                                                                         @Nonnull Builder<Edge> edges) {
    return propertyExprs.stream()
        .map(propertyExpr ->
            addObjectPropertyExprTranslationAndStructuralEdge(propertyExpr, axiomNode, translations, edges))
        .collect(ImmutableList.toImmutableList());
  }

  private Node addObjectPropertyExprTranslationAndStructuralEdge(@Nonnull OWLObjectPropertyExpression propertyExpr,
                                                                 @Nonnull Node axiomNode,
                                                                 @Nonnull Builder<Translation> translations,
                                                                 @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getObjectPropertyExpressionEdge(axiomNode, propertyExprNode);
    edges.add(edge);
    return propertyExprNode;
  }

  private List<Node> addDataPropertyExprTranslationsAndStructuralEdges(@Nonnull Set<OWLDataPropertyExpression> propertyExprs,
                                                                       @Nonnull Node axiomNode,
                                                                       @Nonnull Builder<Translation> translations,
                                                                       @Nonnull Builder<Edge> edges) {
    return propertyExprs.stream()
        .map(propertyExpr ->
            addDataPropertyExprTranslationAndStructuralEdge(propertyExpr, axiomNode, translations, edges))
        .collect(ImmutableList.toImmutableList());
  }

  private Node addDataPropertyExprTranslationAndStructuralEdge(@Nonnull OWLDataPropertyExpression propertyExpr,
                                                               @Nonnull Node axiomNode,
                                                               @Nonnull Builder<Translation> translations,
                                                               @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getDataPropertyExpressionEdge(axiomNode, propertyExprNode);
    edges.add(edge);
    return propertyExprNode;
  }

  private Node addInverseObjectPropertyExprTranslationAndStructuralEdge(@Nonnull OWLObjectPropertyExpression propertyExpr,
                                                                        @Nonnull Node mainNode,
                                                                        @Nonnull Builder<Translation> translations,
                                                                        @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getInverseObjectPropertyExpressionEdge(mainNode, propertyExprNode);
    edges.add(edge);

    return propertyExprNode;
  }

  private Node addNextTranslationAndStructuralEdge(@Nonnull OWLObjectPropertyExpression propertyExpr,
                                                   @Nonnull Node mainNode,
                                                   @Nonnull Builder<Translation> translations,
                                                   @Nonnull Builder<Edge> edges) {
    var propertyExprTranslation = propertyExprTranslator.translate(propertyExpr);
    translations.add(propertyExprTranslation);
    var propertyExprNode = propertyExprTranslation.getMainNode();
    var edge = structuralEdgeFactory.getNextEdge(mainNode, propertyExprNode);
    edges.add(edge);
    return propertyExprNode;
  }

  private Node addDomainTranslationAndStructuralEdge(@Nonnull IRI domain,
                                                     @Nonnull Node axiomNode,
                                                     @Nonnull Builder<Translation> translations,
                                                     @Nonnull Builder<Edge> edges) {
    var domainTranslation = annotationValueTranslator.translate(domain);
    addDomainTranslationAndStructuralEdge(domainTranslation, axiomNode, translations, edges);
    return domainTranslation.getMainNode();
  }

  private Node addDomainTranslationAndStructuralEdge(@Nonnull OWLClassExpression domain,
                                                     @Nonnull Node axiomNode,
                                                     @Nonnull Builder<Translation> translations,
                                                     @Nonnull Builder<Edge> edges) {
    var domainTranslation = classExprTranslator.translate(domain);
    addDomainTranslationAndStructuralEdge(domainTranslation, axiomNode, translations, edges);
    return domainTranslation.getMainNode();
  }

  private void addDomainTranslationAndStructuralEdge(@Nonnull Translation translation,
                                                     @Nonnull Node axiomNode,
                                                     @Nonnull Builder<Translation> translations,
                                                     @Nonnull Builder<Edge> edges) {
    var domainNode = translation.getMainNode();
    var edge = structuralEdgeFactory.getDomainEdge(axiomNode, domainNode);
    translations.add(translation);
    edges.add(edge);
  }

  private Node addRangeTranslationAndStructuralEdge(@Nonnull OWLClassExpression range,
                                                    @Nonnull Node axiomNode,
                                                    @Nonnull Builder<Translation> translations,
                                                    @Nonnull Builder<Edge> edges) {
    var rangeTranslation = classExprTranslator.translate(range);
    addRangeTranslationAndStructuralEdge(rangeTranslation, axiomNode, translations, edges);
    return rangeTranslation.getMainNode();
  }

  private Node addRangeTranslationAndStructuralEdge(@Nonnull IRI range,
                                                    @Nonnull Node axiomNode,
                                                    @Nonnull Builder<Translation> translations,
                                                    @Nonnull Builder<Edge> edges) {
    var rangeTranslation = annotationValueTranslator.translate(range);
    addRangeTranslationAndStructuralEdge(rangeTranslation, axiomNode, translations, edges);
    return rangeTranslation.getMainNode();
  }

  private Node addRangeTranslationAndStructuralEdge(@Nonnull OWLDataRange range,
                                                    @Nonnull Node axiomNode,
                                                    @Nonnull Builder<Translation> translations,
                                                    @Nonnull Builder<Edge> edges) {
    var rangeTranslation = dataRangeTranslator.translate(range);
    addRangeTranslationAndStructuralEdge(rangeTranslation, axiomNode, translations, edges);
    return rangeTranslation.getMainNode();
  }

  private void addRangeTranslationAndStructuralEdge(@Nonnull Translation translation,
                                                    @Nonnull Node axiomNode,
                                                    @Nonnull Builder<Translation> translations,
                                                    @Nonnull Builder<Edge> edges) {
    var rangeNode = translation.getMainNode();
    var edge = structuralEdgeFactory.getRangeEdge(axiomNode, rangeNode);
    translations.add(translation);
    edges.add(edge);
  }

  private Node addAnnotationSubjectTranslationAndStructuralEdge(@Nonnull OWLAnnotationSubject subject,
                                                                @Nonnull Node axiomNode,
                                                                @Nonnull Builder<Translation> translations,
                                                                @Nonnull Builder<Edge> edges) {
    var annotationSubjectTranslation = annotationSubjectTranslator.translate(subject);
    translations.add(annotationSubjectTranslation);
    var annotationSubjectNode = annotationSubjectTranslation.getMainNode();
    var edge = structuralEdgeFactory.getAnnotationSubjectEdge(axiomNode, annotationSubjectNode);
    edges.add(edge);
    return annotationSubjectNode;
  }

  private Node addAnnotationPropertyTranslationAndStructuralEdge(@Nonnull OWLAnnotationProperty property,
                                                                 @Nonnull Node axiomNode,
                                                                 @Nonnull Builder<Translation> translations,
                                                                 @Nonnull Builder<Edge> edges) {
    var annotationPropertyTranslation = propertyExprTranslator.translate(property);
    translations.add(annotationPropertyTranslation);
    var annotationPropertyNode = annotationPropertyTranslation.getMainNode();
    var edge = structuralEdgeFactory.getAnnotationPropertyEdge(axiomNode, annotationPropertyNode);
    edges.add(edge);
    return annotationPropertyNode;
  }

  private Node addAnnotationValueTranslationAndStructuralEdge(@Nonnull OWLAnnotationValue value,
                                                              @Nonnull Node axiomNode,
                                                              @Nonnull Builder<Translation> translations,
                                                              @Nonnull Builder<Edge> edges) {
    var annotationValueTranslation = annotationValueTranslator.translate(value);
    translations.add(annotationValueTranslation);
    var annotationValueNode = annotationValueTranslation.getMainNode();
    var edge = structuralEdgeFactory.getAnnotationValueEdge(axiomNode, annotationValueNode);
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
    var edge = structuralEdgeFactory.getAxiomAnnotationEdge(axiomNode, axiomAnnotationNode);
    edges.add(edge);
  }

  /**
   * Methods to create augmented edges
   */

  private void addAxiomOfAugmentedEdge(Node axiomNode, Node ontologyDocumentNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getAxiomOfEdge(axiomNode, ontologyDocumentNode).ifPresent(edges::add);
  }

  private void addAxiomSubjectAugmentedEdge(Node axiomNode, Node subjectNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getAxiomSubjectEdge(axiomNode, subjectNode).ifPresent(edges::add);
  }

  private void addAxiomSubjectAugmentedEdges(Node axiomNode, List<Node> subjectNodes, Builder<Edge> edges) {
    augmentedEdgeFactory.getAxiomSubjectEdges(axiomNode, subjectNodes).ifPresent(edges::addAll);
  }

  private void addSubClassOfAugmentedEdge(Node subClassNode, Node superClassNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getSubClassOfEdge(subClassNode, superClassNode).ifPresent(edges::add);
  }

  private void addSymmetricalSubClassOfAugmentedEdges(List<Node> classNodes, Builder<Edge> edges) {
    augmentedEdgeFactory.getSymmetricalSubClassOfEdges(classNodes).ifPresent(edges::addAll);
  }

  private void addSubObjectPropertyOfAugmentedEdge(Node subPropertyNode, Node superPropertyOf, Builder<Edge> edges) {
    augmentedEdgeFactory.getSubObjectPropertyOfEdge(subPropertyNode, superPropertyOf).ifPresent(edges::add);
  }

  private void addSymmetricalSubObjectPropertyOfAugmentedEdges(List<Node> objectPropertyNodes, Builder<Edge> edges) {
    augmentedEdgeFactory.getSymmetricalSubObjectPropertyOfEdges(objectPropertyNodes).ifPresent(edges::addAll);
  }

  private void addSubDataPropertyOfAugmentedEdge(Node subPropertyNode, Node superPropertyOf, Builder<Edge> edges) {
    augmentedEdgeFactory.getSubDataPropertyOfEdge(subPropertyNode, superPropertyOf).ifPresent(edges::add);
  }

  private void addSymmetricalSubDataPropertyOfAugmentedEdges(List<Node> dataPropertyNodes, Builder<Edge> edges) {
    augmentedEdgeFactory.getSymmetricalSubDataPropertyOfEdges(dataPropertyNodes).ifPresent(edges::addAll);
  }

  private void addSubAnnotationPropertyOfAugmentedEdge(Node subPropertyNode, Node superPropertyNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getSubAnnotationPropertyOfEdge(subPropertyNode, superPropertyNode).ifPresent(edges::add);
  }

  private void addDomainAugmentedEdge(Node propertyNode, Node domainNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getHasDomainEdge(propertyNode, domainNode).ifPresent(edges::add);
  }

  private void addRangeAugmentedEdge(Node propertyNode, Node rangeNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getHasRangeEdge(propertyNode, rangeNode).ifPresent(edges::add);
  }

  private void addInverseOfAugmentedEdge(Node node1, Node node2, Builder<Edge> edges) {
    augmentedEdgeFactory.getInverseOfEdge(node1, node2).ifPresent(edges::add);
  }

  private void addSameIndividualAugmentedEdges(List<Node> individualNodes, Builder<Edge> edges) {
    augmentedEdgeFactory.getSameIndividualEdges(individualNodes).ifPresent(edges::addAll);
  }

  private void addRelatedToAugmentedEdges(Node subClassNode, OWLClassExpression superClassExpresion, Builder<Edge> edges) {
    augmentedEdgeFactory.getRelatedToEdges(subClassNode, superClassExpresion).ifPresent(edges::addAll);
  }

  private void addRelatedToAugmentedEdge(Node subjectNode, Node fillerNode, OWLPropertyExpression propertyExpr, Builder<Edge> edges) {
    augmentedEdgeFactory.getRelatedToEdge(subjectNode, fillerNode, propertyExpr).ifPresent(edges::add);
  }

  private void addTypeAugmentedEdge(Node subjectNode, Node fillerNode, Builder<Edge> edges) {
    augmentedEdgeFactory.getTypeEdge(subjectNode, fillerNode).ifPresent(edges::add);
  }
}
