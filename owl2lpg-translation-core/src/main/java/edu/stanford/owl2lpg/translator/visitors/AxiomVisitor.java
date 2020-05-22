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

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ANNOTATION_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.CLASS;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATATYPE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SAME_INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_CLASS_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_DATA_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_OBJECT_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.*;

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
                      @Nonnull AnnotationValueTranslator annotationValueTranslator) {
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
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDeclarationAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, DECLARATION);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    addEntityTranslationAndEdge(axiom.getEntity(),
        axiomNode, ENTITY, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, NodeLabels.DATATYPE_DEFINITION);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    addEntityTranslationAndEdge(axiom.getDatatype(),
        axiomNode, DATATYPE, translations, edges);
    addDataRangeTranslationAndEdge(axiom.getDataRange(),
        axiomNode, DATA_RANGE, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubClassOfAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, NodeLabels.SUB_CLASS_OF);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var subClassNode = addClassExprTranslationAndEdge(axiom.getSubClass(),
        axiomNode, SUB_CLASS_EXPRESSION, translations, edges);
    var superClassNode = addClassExprTranslationAndEdge(axiom.getSuperClass(),
        axiomNode, SUPER_CLASS_EXPRESSION, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    addAugmentedEdge(subClassNode, superClassNode, SUB_CLASS_OF, edges);
    addAugmentedEdge(subClassNode, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, NEGATIVE_OBJECT_PROPERTY_ASSERTION);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    addIndividualTranslationAndEdge(axiom.getSubject(),
        axiomNode, SOURCE_INDIVIDUAL, translations, edges);
    addPropertyExprTranslationAndEdge(axiom.getProperty(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addIndividualTranslationAndEdge(axiom.getObject(),
        axiomNode, TARGET_INDIVIDUAL, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, NodeLabels.FUNCTIONAL_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, NodeLabels.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, NodeLabels.SYMMETRIC_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, NodeLabels.ASYMMETRIC_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, NodeLabels.TRANSITIVE_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, NodeLabels.REFLEXIVE_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristic(axiom, NodeLabels.IRREFLEXIVE_OBJECT_PROPERTY);
  }

  private Translation
  translateObjectPropertyCharacteristic(@Nonnull OWLObjectPropertyCharacteristicAxiom axiom,
                                        @Nonnull ImmutableList<String> nodeLabels) {
    var axiomNode = nodeFactory.createNode(axiom, nodeLabels);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    addPropertyExprTranslationAndEdge(axiom.getProperty(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointClassesAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, DISJOINT_CLASSES);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    axiom.getClassExpressions()
        .forEach(ce -> addClassExprTranslationAndEdge(
            ce, axiomNode, CLASS_EXPRESSION, translations, edges));
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
    return translatePropertyDomain(axiom,
        DATA_PROPERTY_DOMAIN,
        DATA_PROPERTY_EXPRESSION);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
    return translatePropertyDomain(axiom,
        OBJECT_PROPERTY_DOMAIN,
        OBJECT_PROPERTY_EXPRESSION);
  }

  @SuppressWarnings("rawtypes")
  private Translation translatePropertyDomain(@Nonnull OWLPropertyDomainAxiom axiom,
                                              @Nonnull ImmutableList<String> nodeLabels,
                                              @Nonnull EdgeLabel propertyEdgeLabel) {
    var axiomNode = nodeFactory.createNode(axiom, nodeLabels);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var propertyExprNode = addPropertyExprTranslationAndEdge(axiom.getProperty(),
        axiomNode, propertyEdgeLabel, translations, edges);
    var domainNode = addClassExprTranslationAndEdge(axiom.getDomain(),
        axiomNode, DOMAIN, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    addAugmentedEdge(propertyExprNode, domainNode, DOMAIN, edges);
    addAugmentedEdge(propertyExprNode, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, EQUIVALENT_OBJECT_PROPERTIES);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var propertyExprNodes = axiom.getProperties()
        .stream()
        .map(ope -> addPropertyExprTranslationAndEdge(ope,
            axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges))
        .collect(Collectors.toList());
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    for (int i = 0; i < propertyExprNodes.size(); i++) {
      addAugmentedEdge(propertyExprNodes.get(i), axiomNode, IS_SUBJECT_OF, edges);
      for (int j = 0; j < propertyExprNodes.size(); j++) {
        if (i != j) {
          addAugmentedEdge(
              propertyExprNodes.get(i),
              propertyExprNodes.get(j),
              SUB_OBJECT_PROPERTY_EXPRESSION, edges);
        }
      }
    }
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, NEGATIVE_DATA_PROPERTY_ASSERTION);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    addIndividualTranslationAndEdge(axiom.getSubject(),
        axiomNode, SOURCE_INDIVIDUAL, translations, edges);
    addPropertyExprTranslationAndEdge(axiom.getProperty(),
        axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges);
    addLiteralTranslationAndEdge(axiom.getObject(),
        axiomNode, TARGET_VALUE, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, DIFFERENT_INDIVIDUALS);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    axiom.getIndividuals()
        .forEach(ind -> addIndividualTranslationAndEdge(ind,
            axiomNode, INDIVIDUAL, translations, edges));
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, DISJOINT_DATA_PROPERTIES);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    axiom.getProperties()
        .forEach(dpe -> addPropertyExprTranslationAndEdge(dpe,
            axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges));
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, DISJOINT_OBJECT_PROPERTIES);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    axiom.getProperties()
        .forEach(ope -> addPropertyExprTranslationAndEdge(ope,
            axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges));
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, OBJECT_PROPERTY_RANGE);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var propertyExprNode = addPropertyExprTranslationAndEdge(axiom.getProperty(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    var rangeNode = addClassExprTranslationAndEdge(axiom.getRange(),
        axiomNode, RANGE, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    addAugmentedEdge(propertyExprNode, rangeNode, RANGE, edges);
    addAugmentedEdge(propertyExprNode, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, OBJECT_PROPERTY_ASSERTION);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var sourceNode = addIndividualTranslationAndEdge(axiom.getSubject(),
        axiomNode, SOURCE_INDIVIDUAL, translations, edges);
    addPropertyExprTranslationAndEdge(axiom.getProperty(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    var targetNode = addIndividualTranslationAndEdge(axiom.getObject(),
        axiomNode, TARGET_INDIVIDUAL, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    if (axiom.getProperty().isNamed()) {
      var objectProperty = axiom.getProperty().asOWLObjectProperty();
      addAugmentedEdge(sourceNode, targetNode, RELATED_TO,
          Properties.create(ImmutableMap.of(
              PropertyFields.IRI, String.valueOf(objectProperty.getIRI()),
              PropertyFields.TYPE, objectProperty.getEntityType().getName())),
          edges);
    }
    addAugmentedEdge(sourceNode, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var subPropertyNode = addPropertyExprTranslationAndEdge(axiom.getSubProperty(),
        axiomNode, SUB_OBJECT_PROPERTY_EXPRESSION, translations, edges);
    var superPropertyNode = addPropertyExprTranslationAndEdge(axiom.getSuperProperty(),
        axiomNode, SUPER_OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    addAugmentedEdge(subPropertyNode, superPropertyNode, SUB_OBJECT_PROPERTY_OF, edges);
    addAugmentedEdge(subPropertyNode, axiomNode, SUB_OBJECT_PROPERTY_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointUnionAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, DISJOINT_UNION);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    addEntityTranslationAndEdge(axiom.getOWLClass(),
        axiomNode, CLASS, translations, edges);
    axiom.getClassExpressions()
        .forEach(ce -> addClassExprTranslationAndEdge(ce,
            axiomNode, DISJOINT_CLASS_EXPRESSION, translations, edges));
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, DATA_PROPERTY_RANGE);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var propertyExprNode = addPropertyExprTranslationAndEdge(axiom.getProperty(),
        axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges);
    var rangeNode = addDataRangeTranslationAndEdge(axiom.getRange(),
        axiomNode, RANGE, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    addAugmentedEdge(propertyExprNode, rangeNode, RANGE, edges);
    addAugmentedEdge(propertyExprNode, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, FUNCTIONAL_DATA_PROPERTY);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    addPropertyExprTranslationAndEdge(axiom.getProperty(),
        axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, EQUIVALENT_DATA_PROPERTIES);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var propertyExprList = axiom.getProperties()
        .stream()
        .map(dpe -> addPropertyExprTranslationAndEdge(dpe,
            axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges))
        .collect(Collectors.toList());
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    for (int i = 0; i < propertyExprList.size(); i++) {
      addAugmentedEdge(propertyExprList.get(i), axiomNode, IS_SUBJECT_OF, edges);
      for (int j = 0; j < propertyExprList.size(); i++) {
        if (i != j) {
          addAugmentedEdge(
              propertyExprList.get(i),
              propertyExprList.get(j),
              SUB_DATA_PROPERTY_OF, edges);
        }
      }
    }
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClassAssertionAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, CLASS_ASSERTION);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var individualNode = addIndividualTranslationAndEdge(axiom.getIndividual(),
        axiomNode, INDIVIDUAL, translations, edges);
    var classExprNode = addClassExprTranslationAndEdge(axiom.getClassExpression(),
        axiomNode, CLASS_EXPRESSION, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    addAugmentedEdge(individualNode, classExprNode, TYPE, edges);
    addAugmentedEdge(individualNode, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, EQUIVALENT_CLASSES);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var classList = axiom.getClassExpressions()
        .stream()
        .map(ce -> addClassExprTranslationAndEdge(ce,
            axiomNode, CLASS_EXPRESSION, translations, edges))
        .collect(Collectors.toList());
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    for (int i = 0; i < classList.size(); i++) {
      addAugmentedEdge(classList.get(i), axiomNode, IS_SUBJECT_OF, edges);
      for (int j = 0; j < classList.size(); i++) {
        if (i != j) {
          addAugmentedEdge(
              classList.get(i),
              classList.get(j),
              SUB_CLASS_OF, edges);
        }
      }
    }
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, DATA_PROPERTY_ASSERTION);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var sourceNode = addIndividualTranslationAndEdge(axiom.getSubject(),
        axiomNode, SOURCE_INDIVIDUAL, translations, edges);
    addPropertyExprTranslationAndEdge(axiom.getProperty(),
        axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges);
    var targetNode = addLiteralTranslationAndEdge(axiom.getObject(),
        axiomNode, TARGET_VALUE, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    if (axiom.getProperty().isNamed()) {
      var dataProperty = axiom.getProperty().asOWLDataProperty();
      addAugmentedEdge(sourceNode, targetNode, RELATED_TO,
          Properties.create(ImmutableMap.of(
              PropertyFields.IRI, String.valueOf(dataProperty.getIRI()),
              PropertyFields.TYPE, dataProperty.getEntityType().getName())),
          edges);
    }
    addAugmentedEdge(sourceNode, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, NodeLabels.SUB_DATA_PROPERTY_OF);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var subPropertyNode = addPropertyExprTranslationAndEdge(axiom.getSubProperty(),
        axiomNode, SUB_DATA_PROPERTY_EXPRESSION, translations, edges);
    var superPropertyNode = addPropertyExprTranslationAndEdge(axiom.getSuperProperty(),
        axiomNode, SUPER_OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    addAugmentedEdge(subPropertyNode, superPropertyNode, SUB_DATA_PROPERTY_OF, edges);
    addAugmentedEdge(subPropertyNode, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSameIndividualAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, NodeLabels.SAME_INDIVIDUAL);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var individualList = axiom.getIndividuals()
        .stream()
        .map(ind -> addIndividualTranslationAndEdge(ind,
            axiomNode, INDIVIDUAL, translations, edges))
        .collect(Collectors.toList());
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    for (int i = 0; i < individualList.size(); i++) {
      addAugmentedEdge(individualList.get(i), axiomNode, IS_SUBJECT_OF, edges);
      for (int j = 0; j < individualList.size(); i++) {
        if (i != j) {
          addAugmentedEdge(
              individualList.get(i),
              individualList.get(j),
              SAME_INDIVIDUAL, edges);
        }
      }
    }
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    addPropertyChainTranslationAndEdge(axiom.getPropertyChain(),
        axiomNode, SUB_OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addPropertyExprTranslationAndEdge(axiom.getSuperProperty(),
        axiomNode, SUPER_OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  private Node addPropertyChainTranslationAndEdge(@Nonnull List<OWLObjectPropertyExpression> propertyChain,
                                                  @Nonnull Node axiomNode,
                                                  @Nonnull EdgeLabel edgeLabel,
                                                  @Nonnull Builder<Translation> translations,
                                                  @Nonnull Builder<Edge> edges) {
    var firstChainNode = addPropertyExprTranslationAndEdge(propertyChain.get(0),
        axiomNode, edgeLabel, translations, edges);
    if (propertyChain.size() > 1) {
      addPropertyChainRecursively(propertyChain.subList(1, propertyChain.size()),
          firstChainNode, translations, edges);
    }
    return firstChainNode;
  }

  private void addPropertyChainRecursively(@Nonnull List<OWLObjectPropertyExpression> propertyChain,
                                           @Nonnull Node mainNode,
                                           @Nonnull Builder<Translation> translations,
                                           @Nonnull Builder<Edge> edges) {
    if (!propertyChain.isEmpty()) {
      var nextMainNode = addPropertyExprTranslationAndEdge(propertyChain.get(0),
          mainNode, NEXT, translations, edges);
      addPropertyChainRecursively(propertyChain.subList(1, propertyChain.size()),
          nextMainNode, translations, edges);
    }
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, INVERSE_OBJECT_PROPERTIES);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var ope1Node = addPropertyExprTranslationAndEdge(axiom.getFirstProperty(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    var ope2Node = addPropertyExprTranslationAndEdge(axiom.getSecondProperty(),
        axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    addAugmentedEdge(ope1Node, ope2Node, INVERSE_OF, edges);
    addAugmentedEdge(ope2Node, ope1Node, INVERSE_OF, edges);
    addAugmentedEdge(ope1Node, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLHasKeyAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, HAS_KEY);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    addClassExprTranslationAndEdge(axiom.getClassExpression(),
        axiomNode, CLASS_EXPRESSION, translations, edges);
    axiom.getObjectPropertyExpressions()
        .forEach(ope -> addPropertyExprTranslationAndEdge(ope,
            axiomNode, OBJECT_PROPERTY_EXPRESSION, translations, edges));
    axiom.getDataPropertyExpressions()
        .forEach(dpe -> addPropertyExprTranslationAndEdge(dpe,
            axiomNode, DATA_PROPERTY_EXPRESSION, translations, edges));
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, ANNOTATION_ASSERTION);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var sourceNode = addAnnotationSubjectTranslationAndEdge(axiom.getSubject(),
        axiomNode, ANNOTATION_SUBJECT, translations, edges);
    addPropertyExprTranslationAndEdge(axiom.getProperty(),
        axiomNode, ANNOTATION_PROPERTY, translations, edges);
    var targetNode = addAnnotationValueTranslationAndEdge(axiom.getValue(),
        axiomNode, ANNOTATION_VALUE, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    addAugmentedEdge(sourceNode, targetNode, RELATED_TO,
        Properties.create(ImmutableMap.of(
            PropertyFields.IRI, String.valueOf(axiom.getProperty().getIRI()),
            PropertyFields.TYPE, axiom.getProperty().getEntityType().getName())),
        edges);
    addAugmentedEdge(sourceNode, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, NodeLabels.SUB_ANNOTATION_PROPERTY_OF);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var subPropertyNode = addPropertyExprTranslationAndEdge(axiom.getSubProperty(),
        axiomNode, SUB_ANNOTATION_PROPERTY, translations, edges);
    var superPropertyNode = addPropertyExprTranslationAndEdge(axiom.getSuperProperty(),
        axiomNode, SUPER_ANNOTATION_PROPERTY, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    addAugmentedEdge(subPropertyNode, superPropertyNode, EdgeLabel.SUB_ANNOTATION_PROPERTY_OF, edges);
    addAugmentedEdge(subPropertyNode, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, ANNOTATION_PROPERTY_DOMAIN);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var propertyNode = addPropertyExprTranslationAndEdge(axiom.getProperty(),
        axiomNode, ANNOTATION_PROPERTY, translations, edges);
    var domainNode = addIriTranslationAndEdge(axiom.getDomain(),
        axiomNode, DOMAIN, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    addAugmentedEdge(propertyNode, domainNode, DOMAIN, edges);
    addAugmentedEdge(propertyNode, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
    var axiomNode = nodeFactory.createNode(axiom, ANNOTATION_PROPERTY_RANGE);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    var propertyExprTranslation = propertyExprTranslator.translate(axiom.getProperty());
    var propertyExprNode = propertyExprTranslation.getMainNode();
    translations.add(propertyExprTranslation);
    edges.add(edgeFactory.createEdge(axiomNode,
        propertyExprNode,
        ANNOTATION_PROPERTY));
    var rangeNode = addIriTranslationAndEdge(axiom.getRange(),
        axiomNode, RANGE, translations, edges);
    addAxiomAnnotations(axiom, axiomNode, translations, edges);
    addAugmentedEdge(propertyExprNode, rangeNode, RANGE, edges);
    addAugmentedEdge(propertyExprNode, axiomNode, IS_SUBJECT_OF, edges);
    return buildTranslation(axiomNode, translations, edges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull SWRLRule rule) {
    throw new UnsupportedOperationException("SWRLRule translation is not supported yet");
  }

  private Node addIriTranslationAndEdge(@Nonnull IRI iri,
                                        @Nonnull Node mainNode,
                                        @Nonnull EdgeLabel edgeLabel,
                                        @Nonnull Builder<Translation> translations,
                                        @Nonnull Builder<Edge> edges) {
    return addTranslationAndEdge(
        annotationValueTranslator.translate(iri),
        mainNode, edgeLabel, translations, edges);
  }

  private Node addEntityTranslationAndEdge(@Nonnull OWLEntity entity,
                                           @Nonnull Node mainNode,
                                           @Nonnull EdgeLabel edgeLabel,
                                           @Nonnull Builder<Translation> translations,
                                           @Nonnull Builder<Edge> edges) {
    return addTranslationAndEdge(
        entityTranslator.translate(entity),
        mainNode, edgeLabel, translations, edges);
  }

  private Node addIndividualTranslationAndEdge(@Nonnull OWLIndividual individual,
                                               @Nonnull Node mainNode,
                                               @Nonnull EdgeLabel edgeLabel,
                                               @Nonnull Builder<Translation> translations,
                                               @Nonnull Builder<Edge> edges) {
    return addTranslationAndEdge(
        individualTranslator.translate(individual),
        mainNode, edgeLabel, translations, edges);
  }

  private Node addLiteralTranslationAndEdge(@Nonnull OWLLiteral literal,
                                            @Nonnull Node mainNode,
                                            @Nonnull EdgeLabel edgeLabel,
                                            @Nonnull Builder<Translation> translations,
                                            @Nonnull Builder<Edge> edges) {
    return addTranslationAndEdge(
        literalTranslator.translate(literal),
        mainNode, edgeLabel, translations, edges);
  }

  private Node addClassExprTranslationAndEdge(@Nonnull OWLClassExpression classExpr,
                                              @Nonnull Node mainNode,
                                              @Nonnull EdgeLabel edgeLabel,
                                              @Nonnull Builder<Translation> translations,
                                              @Nonnull Builder<Edge> edges) {
    return addTranslationAndEdge(
        classExprTranslator.translate(classExpr),
        mainNode, edgeLabel, translations, edges);
  }

  private Node addPropertyExprTranslationAndEdge(@Nonnull OWLPropertyExpression propertyExpr,
                                                 @Nonnull Node mainNode,
                                                 @Nonnull EdgeLabel edgeLabel,
                                                 @Nonnull Builder<Translation> translations,
                                                 @Nonnull Builder<Edge> edges) {
    return addTranslationAndEdge(
        propertyExprTranslator.translate(propertyExpr),
        mainNode, edgeLabel, translations, edges);
  }

  private Node addDataRangeTranslationAndEdge(@Nonnull OWLDataRange dataRange,
                                              @Nonnull Node mainNode,
                                              @Nonnull EdgeLabel edgeLabel,
                                              @Nonnull Builder<Translation> translations,
                                              @Nonnull Builder<Edge> edges) {
    return addTranslationAndEdge(
        dataRangeTranslator.translate(dataRange),
        mainNode, edgeLabel, translations, edges);
  }

  private Node addAnnotationTranslationAndEdge(@Nonnull OWLAnnotation annotation,
                                               @Nonnull Node mainNode,
                                               @Nonnull EdgeLabel edgeLabel,
                                               @Nonnull Builder<Translation> translations,
                                               @Nonnull Builder<Edge> edges) {
    return addTranslationAndEdge(
        annotationTranslator.translate(annotation),
        mainNode, edgeLabel, translations, edges);
  }

  private Node addAnnotationSubjectTranslationAndEdge(@Nonnull OWLAnnotationSubject subject,
                                                      @Nonnull Node mainNode,
                                                      @Nonnull EdgeLabel edgeLabel,
                                                      @Nonnull Builder<Translation> translations,
                                                      @Nonnull Builder<Edge> edges) {
    return addTranslationAndEdge(
        annotationSubjectTranslator.translate(subject),
        mainNode, edgeLabel, translations, edges);
  }

  private Node addAnnotationValueTranslationAndEdge(@Nonnull OWLAnnotationValue value,
                                                    @Nonnull Node mainNode,
                                                    @Nonnull EdgeLabel edgeLabel,
                                                    @Nonnull Builder<Translation> translations,
                                                    @Nonnull Builder<Edge> edges) {
    return addTranslationAndEdge(
        annotationValueTranslator.translate(value),
        mainNode, edgeLabel, translations, edges);
  }

  private Node addTranslationAndEdge(@Nonnull Translation translation,
                                     @Nonnull Node mainNode,
                                     @Nonnull EdgeLabel edgeLabel,
                                     @Nonnull Builder<Translation> translations,
                                     @Nonnull Builder<Edge> edges) {
    var translationMainNode = translation.getMainNode();
    translations.add(translation);
    edges.add(edgeFactory.createEdge(mainNode,
        translationMainNode,
        edgeLabel));
    return translationMainNode;
  }

  private void addAxiomAnnotations(@Nonnull OWLAxiom axiom,
                                   @Nonnull Node mainNode,
                                   @Nonnull Builder<Translation> translations,
                                   @Nonnull Builder<Edge> edges) {
    axiom.getAnnotations()
        .forEach(ann -> addAnnotationTranslationAndEdge(
            ann, mainNode, AXIOM_ANNOTATION, translations, edges));
  }

  private void addAugmentedEdge(@Nonnull Node fromNode,
                                @Nonnull Node toNode,
                                @Nonnull EdgeLabel edgeLabel,
                                @Nonnull Properties edgeProperties,
                                @Nonnull Builder<Edge> edges) {
    edges.add(edgeFactory.createEdge(fromNode, toNode, edgeLabel, edgeProperties));
  }

  private void addAugmentedEdge(@Nonnull Node fromNode,
                                @Nonnull Node toNode,
                                @Nonnull EdgeLabel edgeLabel,
                                @Nonnull Builder<Edge> edges) {
    edges.add(edgeFactory.createEdge(fromNode, toNode, edgeLabel));
  }

  private Translation buildTranslation(@Nonnull Node mainNode,
                                       @Nonnull Builder<Translation> translations,
                                       @Nonnull Builder<Edge> edges) {
    return Translation.create(mainNode, edges.build(), translations.build());
  }
}
