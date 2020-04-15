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
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

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

  private Node mainNode;

  @Inject
  public AxiomVisitor(@Nonnull OWLEntityVisitorEx<Translation> entityVisitor,
                      @Nonnull OWLClassExpressionVisitorEx<Translation> classExpressionVisitor,
                      @Nonnull OWLPropertyExpressionVisitorEx<Translation> propertyExpressionVisitor,
                      @Nonnull OWLIndividualVisitorEx<Translation> individualVisitor,
                      @Nonnull OWLDataVisitorEx<Translation> dataVisitor,
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
    mainNode = createNode(axiom, NodeLabels.DECLARATION);
    var entityEdge = createEdge(axiom.getEntity(), EdgeLabels.ENTITY);
    var entityTranslation = createNestedTranslation(axiom.getEntity());
    return Translation.create(mainNode,
        ImmutableList.of(entityEdge),
        ImmutableList.of(entityTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DATATYPE_DEFINITION);
    var datatypeEdge = createEdge(axiom.getDatatype(), EdgeLabels.DATATYPE);
    var datatypeTranslation = createNestedTranslation(axiom.getDatatype());
    var dataRangeEdge = createEdge(axiom.getDataRange(), EdgeLabels.DATA_RANGE);
    var dataRangeTranslation = createNestedTranslation(axiom.getDataRange());
    return Translation.create(mainNode,
        ImmutableList.of(datatypeEdge, dataRangeEdge),
        ImmutableList.of(datatypeTranslation, dataRangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubClassOfAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SUB_CLASS_OF);
    var subClassEdge = createEdge(axiom.getSubClass(), EdgeLabels.SUB_CLASS_EXPRESSION);
    var subClassTranslation = createNestedTranslation(axiom.getSubClass());
    var superClassEdge = createEdge(axiom.getSuperClass(), EdgeLabels.SUPER_CLASS_EXPRESSION);
    var superClassTranslation = createNestedTranslation(axiom.getSuperClass());
    return Translation.create(mainNode,
        ImmutableList.of(subClassEdge, superClassEdge),
        ImmutableList.of(subClassTranslation, superClassTranslation));
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
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, sourceIndividualEdge, targetIndividualEdge),
        ImmutableList.of(propertyExprTranslation, sourceIndividualTranslation, targetIndividualTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.ASYMMETRIC_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge),
        ImmutableList.of(propertyExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.REFLEXIVE_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge),
        ImmutableList.of(propertyExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointClassesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DISJOINT_CLASSES);
    var classExprEdges = createEdges(axiom.getClassExpressions(), EdgeLabels.CLASS_EXPRESSION);
    var classExprTranslations = createNestedTranslations(axiom.getClassExpressions());
    return Translation.create(mainNode,
        ImmutableList.copyOf(classExprEdges),
        ImmutableList.copyOf(classExprTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DATA_PROPERTY_DOMAIN);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var domainEdge = createEdge(axiom.getDomain(), EdgeLabels.DOMAIN);
    var domainTranslation = createNestedTranslation(axiom.getDomain());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, domainEdge),
        ImmutableList.of(propertyExprTranslation, domainTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.OBJECT_PROPERTY_DOMAIN);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var domainEdge = createEdge(axiom.getDomain(), EdgeLabels.DOMAIN);
    var domainTranslation = createNestedTranslation(axiom.getDomain());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, domainEdge),
        ImmutableList.of(propertyExprTranslation, domainTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.EQUIVALENT_OBJECT_PROPERTIES);
    var propertyExprEdges = createEdges(axiom.getProperties(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslations = createNestedTranslations(axiom.getProperties());
    return Translation.create(mainNode,
        ImmutableList.copyOf(propertyExprEdges),
        ImmutableList.copyOf(propertyExprTranslations));
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
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, sourceIndividualEdge, targetValueEdge),
        ImmutableList.of(propertyExprTranslation, sourceIndividualTranslation, targetValueTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DIFFERENT_INDIVIDUALS);
    var individualEdges = createEdges(axiom.getIndividuals(), EdgeLabels.INDIVIDUAL);
    var individualTranslations = createNestedTranslations(axiom.getIndividuals());
    return Translation.create(mainNode,
        ImmutableList.copyOf(individualEdges),
        ImmutableList.copyOf(individualTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DISJOINT_DATA_PROPERTIES);
    var propertyExprEdges = createEdges(axiom.getProperties(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslations = createNestedTranslations(axiom.getProperties());
    return Translation.create(mainNode,
        ImmutableList.copyOf(propertyExprEdges),
        ImmutableList.copyOf(propertyExprTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DISJOINT_OBJECT_PROPERTIES);
    var propertyExprEdges = createEdges(axiom.getProperties(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslations = createNestedTranslations(axiom.getProperties());
    return Translation.create(mainNode,
        ImmutableList.copyOf(propertyExprEdges),
        ImmutableList.copyOf(propertyExprTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.OBJECT_PROPERTY_RANGE);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var rangeEdge = createEdge(axiom.getRange(), EdgeLabels.RANGE);
    var rangeTranslation = createNestedTranslation(axiom.getRange());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, rangeEdge),
        ImmutableList.of(propertyExprTranslation, rangeTranslation));
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
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, sourceIndividualEdge, targetIndividualEdge),
        ImmutableList.of(propertyExprTranslation, sourceIndividualTranslation, targetIndividualTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.FUNCTIONAL_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge),
        ImmutableList.of(propertyExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    var subPropertyEdge = createEdge(axiom.getSubProperty(), EdgeLabels.SUB_OBJECT_PROPERTY_EXPRESSION);
    var subPropertyTranslation = createNestedTranslation(axiom.getSubProperty());
    var superPropertyEdge = createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_OBJECT_PROPERTY_EXPRESSION);
    var superPropertyTranslation = createNestedTranslation(axiom.getSuperProperty());
    return Translation.create(mainNode,
        ImmutableList.of(subPropertyEdge, superPropertyEdge),
        ImmutableList.of(subPropertyTranslation, superPropertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointUnionAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DISJOINT_UNION);
    var classEdge = createEdge(axiom.getOWLClass(), EdgeLabels.CLASS);
    var classTranslation = createNestedTranslation(axiom.getOWLClass());
    var disjointClassExprEdges = createEdges(axiom.getClassExpressions(), EdgeLabels.DISJOINT_CLASS_EXPRESSION);
    var disjointClassExprTranslations = createNestedTranslations(axiom.getClassExpressions());
    var allEdges = Lists.newArrayList(classEdge);
    allEdges.addAll(disjointClassExprEdges);
    var allTranslations = Lists.newArrayList(classTranslation);
    allTranslations.addAll(disjointClassExprTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SYMMETRIC_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge),
        ImmutableList.of(propertyExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.DATA_PROPERTY_RANGE);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    var rangeEdge = createEdge(axiom.getRange(), EdgeLabels.RANGE);
    var rangeTranslation = createNestedTranslation(axiom.getRange());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, rangeEdge),
        ImmutableList.of(propertyExprTranslation, rangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.FUNCTIONAL_DATA_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge),
        ImmutableList.of(propertyExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.EQUIVALENT_DATA_PROPERTIES);
    var propertyExprEdges = createEdges(axiom.getProperties(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslations = createNestedTranslations(axiom.getProperties());
    return Translation.create(mainNode,
        ImmutableList.copyOf(propertyExprEdges),
        ImmutableList.copyOf(propertyExprTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClassAssertionAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.CLASS_ASSERTION);
    var classExprEdge = createEdge(axiom.getClassExpression(), EdgeLabels.CLASS_EXPRESSION);
    var classExprTranslation = createNestedTranslation(axiom.getClassExpression());
    var individualEdge = createEdge(axiom.getIndividual(), EdgeLabels.INDIVIDUAL);
    var individualTranslation = createNestedTranslation(axiom.getIndividual());
    return Translation.create(mainNode,
        ImmutableList.of(classExprEdge, individualEdge),
        ImmutableList.of(classExprTranslation, individualTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.EQUIVALENT_CLASSES);
    var classExprEdges = createEdges(axiom.getClassExpressions(), EdgeLabels.CLASS_EXPRESSION);
    var classExprTranslations = createNestedTranslations(axiom.getClassExpressions());
    return Translation.create(mainNode,
        ImmutableList.copyOf(classExprEdges),
        ImmutableList.copyOf(classExprTranslations));
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
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, sourceIndividualEdge, targetValueEdge),
        ImmutableList.of(propertyExprTranslation, sourceIndividualTranslation, targetValueTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.TRANSITIVE_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge),
        ImmutableList.of(propertyExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.IRREFLEXIVE_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge),
        ImmutableList.of(propertyExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SUB_DATA_PROPERTY_OF);
    var subPropertyEdge = createEdge(axiom.getSubProperty(), EdgeLabels.SUB_DATA_PROPERTY_EXPRESSION);
    var subPropertyTranslation = createNestedTranslation(axiom.getSubProperty());
    var superPropertyEdge = createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_DATA_PROPERTY_EXPRESSION);
    var superPropertyTranslation = createNestedTranslation(axiom.getSuperProperty());
    return Translation.create(mainNode,
        ImmutableList.of(subPropertyEdge, superPropertyEdge),
        ImmutableList.of(subPropertyTranslation, superPropertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
    var propertyExprEdge = createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(axiom.getProperty());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge),
        ImmutableList.of(propertyExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSameIndividualAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SAME_INDIVIDUAL);
    var individualEdges = createEdges(axiom.getIndividuals(), EdgeLabels.INDIVIDUAL);
    var individualTranslations = createNestedTranslations(axiom.getIndividuals());
    return Translation.create(mainNode,
        ImmutableList.copyOf(individualEdges),
        ImmutableList.copyOf(individualTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    var subPropertyEdge = createEdge(axiom.getPropertyChain().get(0), EdgeLabels.SUB_OBJECT_PROPERTY_EXPRESSION);
    var subPropertyTranslation = createChainTranslation(axiom.getPropertyChain());
    var superPropertyEdge = createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_OBJECT_PROPERTY_EXPRESSION);
    var superPropertyTranslation = createNestedTranslation(axiom.getSuperProperty());
    return Translation.create(mainNode,
        ImmutableList.of(subPropertyEdge, superPropertyEdge),
        ImmutableList.of(subPropertyTranslation, superPropertyTranslation));
  }

  protected Translation createChainTranslation(List<OWLObjectPropertyExpression> chain) {
    if (chain.size() == 1) {
      return createNestedTranslation(chain.get(0));
    } else {
      var headChainNode = createNode(chain.get(0), NodeLabels.OBJECT_PROPERTY);
      return translateChainRecursively(headChainNode, chain.subList(1, chain.size()));
    }
  }

  private Translation translateChainRecursively(Node headNode, List<OWLObjectPropertyExpression> chain) {
    var nextPropertyNode = createNode(chain.get(0), NodeLabels.OBJECT_PROPERTY);
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
    return Translation.create(mainNode,
        ImmutableList.of(firstPropertyEdge, secondPropertyEdge),
        ImmutableList.of(firstPropertyTranslation, secondPropertyTranslation));
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
    var allEdges = Lists.newArrayList(classEdge);
    allEdges.addAll(dataPropertyExprEdges);
    allEdges.addAll(objectPropertyExprEdges);
    var allTranslations = Lists.newArrayList(classTranslation);
    allTranslations.addAll(objectPropertyExprTranslations);
    allTranslations.addAll(dataPropertyExprTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allTranslations));
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
    return Translation.create(mainNode,
        ImmutableList.of(annotationPropertyEdge, annotationSubjectEdge, annotationValueEdge),
        ImmutableList.of(annotationPropertyTranslation, annotationSubjectTranslation, annotationValueTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.SUB_ANNOTATION_PROPERTY_OF);
    var subPropertyEdge = createEdge(axiom.getSubProperty(), EdgeLabels.SUB_ANNOTATION_PROPERTY);
    var subPropertyTranslation = createNestedTranslation(axiom.getSubProperty());
    var superPropertyEdge = createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_ANNOTATION_PROPERTY);
    var superPropertyTranslation = createNestedTranslation(axiom.getSuperProperty());
    return Translation.create(mainNode,
        ImmutableList.of(subPropertyEdge, superPropertyEdge),
        ImmutableList.of(subPropertyTranslation, superPropertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.ANNOTATION_PROPERTY_DOMAIN);
    var annotationPropertyEdge = createEdge(axiom.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    var annotationPropertyTranslation = createNestedTranslation(axiom.getProperty());
    var domainEdge = createEdge(axiom.getDomain(), EdgeLabels.DOMAIN);
    var domainTranslation = createNestedTranslation(axiom.getDomain());
    return Translation.create(mainNode,
        ImmutableList.of(annotationPropertyEdge, domainEdge),
        ImmutableList.of(annotationPropertyTranslation, domainTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
    mainNode = createNode(axiom, NodeLabels.ANNOTATION_PROPERTY_RANGE);
    var annotationPropertyEdge = createEdge(axiom.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    var annotationPropertyTranslation = createNestedTranslation(axiom.getProperty());
    var rangeEdge = createEdge(axiom.getRange(), EdgeLabels.RANGE);
    var rangeTranslation = createNestedTranslation(axiom.getRange());
    return Translation.create(mainNode,
        ImmutableList.of(annotationPropertyEdge, rangeEdge),
        ImmutableList.of(annotationPropertyTranslation, rangeTranslation));
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
    if (anyObject instanceof OWLEntity) {
      return createEntityTranslation((OWLEntity) anyObject);
    } else if (anyObject instanceof OWLClassExpression) {
      return createClassExpressionTranslation((OWLClassExpression) anyObject);
    } else if (anyObject instanceof OWLPropertyExpression) {
      return createPropertyExpressionTranslation((OWLPropertyExpression) anyObject);
    } else if (anyObject instanceof OWLIndividual) {
      return createIndividualTranslation((OWLIndividual) anyObject);
    } else if (anyObject instanceof OWLLiteral) {
      return createLiteralTranslation((OWLLiteral) anyObject);
    } else if (anyObject instanceof OWLDataRange) {
      return createDataRangeTranslation((OWLDataRange) anyObject);
    } else if (anyObject instanceof OWLAnnotationSubject) {
      return createAnnotationSubjectTranslation((OWLAnnotationSubject) anyObject);
    } else if (anyObject instanceof OWLAnnotationValue) {
      return createAnnotationValueTranslation((OWLAnnotationValue) anyObject);
    }
    throw new IllegalArgumentException("Implementation error");
  }

  private Translation createEntityTranslation(OWLEntity entity) {
    return entity.accept(entityVisitor);
  }

  private Translation createClassExpressionTranslation(OWLClassExpression classExpression) {
    return classExpression.accept(classExpressionVisitor);
  }

  private Translation createPropertyExpressionTranslation(OWLPropertyExpression propertyExpression) {
    return propertyExpression.accept(propertyExpressionVisitor);
  }

  private Translation createIndividualTranslation(OWLIndividual individual) {
    return individual.accept(individualVisitor);
  }

  private Translation createLiteralTranslation(OWLLiteral literal) {
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
