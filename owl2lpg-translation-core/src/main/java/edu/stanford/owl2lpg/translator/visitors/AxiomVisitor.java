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
import java.util.Set;
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
  public AxiomVisitor(@Nonnull VisitorFactory visitorFactory) {
    super(visitorFactory.getNodeIdMapper());
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
    var augmentedSubClassOfEdge = createAugmentedEdge(
        subClassTranslation.getMainNode(),
        superClassTranslation.getMainNode(),
        EdgeLabels.SUB_CLASS_OF);
    var augmentedIsSubjectOfEdge = createAugmentedEdge(
        subClassTranslation.getMainNode(),
        mainNode,
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
    return translatePropertyAssertion(axiom,
                                      NodeLabels.NEGATIVE_OBJECT_PROPERTY_ASSERTION,
                                      EdgeLabels.OBJECT_PROPERTY_EXPRESSION,
                                      EdgeLabels.TARGET_INDIVIDUAL);
  }

  private Translation translateUnaryArgsAxiom(@Nonnull OWLAxiom axiom,
                                               @Nonnull ImmutableList<String> axiomNodeLabels,
                                               @Nonnull String firstArgEdgeLabel,
                                               @Nonnull OWLObject firstArg) {
    mainNode = createNode(axiom, axiomNodeLabels);
    var firstEdge = createEdge(firstArg, firstArgEdgeLabel);
    var firstArgTrans = createNestedTranslation(firstArg);
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(firstEdge, annotationEdges);
    var allNestedTranslations = concatTranslations(firstArgTrans, annotationTranslations);
    return Translation.create(mainNode,
                              ImmutableList.copyOf(allEdges),
                              ImmutableList.copyOf(allNestedTranslations));
  }


  private Translation translateBinaryArgsAxiom(@Nonnull OWLAxiom axiom,
                                               @Nonnull ImmutableList<String> axiomNodeLabels,
                                               @Nonnull String firstArgEdgeLabel,
                                               @Nonnull OWLObject firstArg,
                                               @Nonnull String secondArgEdgeLabel,
                                               @Nonnull OWLObject secondArg) {
    mainNode = createNode(axiom, axiomNodeLabels);
    var firstEdge = createEdge(firstArg, firstArgEdgeLabel);
    var firstArgTrans = createNestedTranslation(firstArg);
    var secondEdge = createEdge(secondArg, secondArgEdgeLabel);
    var secondArgTrans = createNestedTranslation(secondArg);
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(concatEdges(firstEdge, secondEdge), annotationEdges);
    var allNestedTranslations = concatTranslations(
            concatTranslations(firstArgTrans, secondArgTrans), annotationTranslations);
    return Translation.create(mainNode,
                              ImmutableList.copyOf(allEdges),
                              ImmutableList.copyOf(allNestedTranslations));
  }

  private Translation translateTernaryArgsAxiom(@Nonnull OWLAxiom axiom,
                                                ImmutableList<String> axiomNodeLabels,
                                                String firstArgEdgeLabel,
                                                OWLObject firstArg,
                                                String secondArgEdgeLabel,
                                                OWLObject secondArg,
                                                String thirdArgEdgeLabel,
                                                OWLObject thirdArg) {
    mainNode = createNode(axiom, axiomNodeLabels);
    var firstArgEdge = createEdge(firstArg, firstArgEdgeLabel);
    var firstArgTrans = createNestedTranslation(firstArg);
    var secondArgEdge = createEdge(secondArg, secondArgEdgeLabel);
    var secondArgTrans = createNestedTranslation(secondArg);
    var thirdArgEdge = createEdge(thirdArg, thirdArgEdgeLabel);
    var thirdArgTrans = createNestedTranslation(thirdArg);
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(
            concatEdges(firstArgEdge, secondArgEdge, thirdArgEdge),
            annotationEdges);
    var allNestedTranslations = concatTranslations(
            concatTranslations(firstArgTrans, secondArgTrans, thirdArgTrans),
            annotationTranslations);
    return Translation.create(mainNode,
                              ImmutableList.copyOf(allEdges),
                              ImmutableList.copyOf(allNestedTranslations));
  }


  private Translation translateNaryArgsAxiom(@Nonnull OWLAxiom axiom,
                                             @Nonnull ImmutableList<String> axiomNodeLabels,
                                             @Nonnull Set<? extends OWLObject> naryArgs,
                                             @Nonnull String naryArgEdgeLabel) {
    mainNode = createNode(axiom, axiomNodeLabels);
    var classExprEdges = createEdges(naryArgs, naryArgEdgeLabel);
    var classExprTranslations = createNestedTranslations(naryArgs);
    var annotationEdges = createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    var annotationTranslations = createNestedTranslations(axiom.getAnnotations());
    var allEdges = concatEdges(classExprEdges, annotationEdges);
    var allNestedTranslations = concatTranslations(classExprTranslations, annotationTranslations);
    return Translation.create(mainNode,
                              ImmutableList.copyOf(allEdges),
                              ImmutableList.copyOf(allNestedTranslations));
  }


  private Translation translatePropertyAssertion(@Nonnull OWLPropertyAssertionAxiom<?, ?> axiom,
                                                 ImmutableList<String> axiomNodeLabels,
                                                 String propertyEdgeLabel,
                                                 String objectEdgeLabel) {
    return translateTernaryArgsAxiom(axiom,
                                     axiomNodeLabels,
                                     propertyEdgeLabel,
                                     axiom.getProperty(),
                                     EdgeLabels.SOURCE_INDIVIDUAL,
                                     axiom.getSubject(),
                                     objectEdgeLabel,
                                     axiom.getObject());
  }

  private Translation translateObjectPropertyCharacteristicAxiom(@Nonnull OWLObjectPropertyCharacteristicAxiom axiom,
                                                                 ImmutableList<String> axiomNodeLabels) {

    return translateUnaryArgsAxiom(axiom,
                                   axiomNodeLabels,
                                   EdgeLabels.OBJECT_PROPERTY_EXPRESSION,
                                   axiom.getProperty());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristicAxiom(axiom, NodeLabels.ASYMMETRIC_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
     return translateObjectPropertyCharacteristicAxiom(axiom, NodeLabels.REFLEXIVE_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointClassesAxiom axiom) {
    return translateNaryArgsAxiom(axiom,
                                  NodeLabels.DISJOINT_CLASSES,
                                  axiom.getClassExpressions(),
                                  EdgeLabels.CLASS_EXPRESSION);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
    return translateBinaryArgsAxiom(axiom,
                                    NodeLabels.DATA_PROPERTY_DOMAIN,
                                    EdgeLabels.DATA_PROPERTY_EXPRESSION,
                                    axiom.getProperty(),
                                    EdgeLabels.DOMAIN,
                                    axiom.getDomain());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
    return translateBinaryArgsAxiom(axiom,
                                    NodeLabels.OBJECT_PROPERTY_DOMAIN,
                                    EdgeLabels.OBJECT_PROPERTY_EXPRESSION,
                                    axiom.getProperty(),
                                    EdgeLabels.DOMAIN,
                                    axiom.getDomain());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
    return translateNaryArgsAxiom(axiom,
                                  NodeLabels.EQUIVALENT_OBJECT_PROPERTIES,
                                  axiom.getProperties(),
                                  EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
    return translatePropertyAssertion(axiom,
                                      NodeLabels.NEGATIVE_DATA_PROPERTY_ASSERTION,
                                      EdgeLabels.DATA_PROPERTY_EXPRESSION,
                                      EdgeLabels.TARGET_VALUE);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
    return translateNaryArgsAxiom(axiom,
                                  NodeLabels.DIFFERENT_INDIVIDUALS,
                                  axiom.getIndividuals(),
                                  EdgeLabels.INDIVIDUAL);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
    return translateNaryArgsAxiom(axiom,
                                  NodeLabels.DISJOINT_DATA_PROPERTIES,
                                  axiom.getProperties(),
                                  EdgeLabels.DATA_PROPERTY_EXPRESSION);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
    return translateNaryArgsAxiom(axiom,
                                  NodeLabels.DISJOINT_OBJECT_PROPERTIES,
                                  axiom.getProperties(),
                                  EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
    return translateBinaryArgsAxiom(axiom,
                                    NodeLabels.OBJECT_PROPERTY_RANGE,
                                    EdgeLabels.OBJECT_PROPERTY_EXPRESSION,
                                    axiom.getProperty(),
                                    EdgeLabels.RANGE,
                                    axiom.getRange());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
    return translatePropertyAssertion(axiom,
                                      NodeLabels.OBJECT_PROPERTY_ASSERTION,
                                      EdgeLabels.OBJECT_PROPERTY_EXPRESSION,
                                      EdgeLabels.TARGET_INDIVIDUAL);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristicAxiom(axiom, NodeLabels.FUNCTIONAL_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
    return translateBinaryArgsAxiom(axiom,
                             NodeLabels.SUB_OBJECT_PROPERTY_OF,
                             EdgeLabels.SUB_OBJECT_PROPERTY_EXPRESSION,
                             axiom.getSubProperty(),
                             EdgeLabels.SUPER_OBJECT_PROPERTY_EXPRESSION,
                             axiom.getSuperProperty());
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
    return translateObjectPropertyCharacteristicAxiom(axiom, NodeLabels.SYMMETRIC_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
    return translateBinaryArgsAxiom(axiom,
                                    NodeLabels.DATA_PROPERTY_RANGE,
                                    EdgeLabels.DATA_PROPERTY_EXPRESSION,
                                    axiom.getProperty(),
                                    EdgeLabels.RANGE,
                                    axiom.getRange());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
    return translateUnaryArgsAxiom(axiom,
                                   NodeLabels.FUNCTIONAL_DATA_PROPERTY,
                                   EdgeLabels.DATA_PROPERTY_EXPRESSION,
                                   axiom.getProperty());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
    return translateNaryArgsAxiom(axiom,
                                  NodeLabels.EQUIVALENT_DATA_PROPERTIES,
                                  axiom.getProperties(),
                                  EdgeLabels.DATA_PROPERTY_EXPRESSION);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClassAssertionAxiom axiom) {
    return translateBinaryArgsAxiom(axiom,
                                    NodeLabels.CLASS_ASSERTION,
                                    EdgeLabels.CLASS_EXPRESSION,
                                    axiom.getClassExpression(),
                                    EdgeLabels.INDIVIDUAL,
                                    axiom.getIndividual());
    }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
    return translateNaryArgsAxiom(axiom,
                                  NodeLabels.EQUIVALENT_CLASSES,
                                  axiom.getClassExpressions(),
                                  EdgeLabels.CLASS_EXPRESSION);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
    return translatePropertyAssertion(axiom,
                                      NodeLabels.DATA_PROPERTY_ASSERTION,
                                      EdgeLabels.DATA_PROPERTY_EXPRESSION,
                                      EdgeLabels.TARGET_VALUE);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristicAxiom(axiom, NodeLabels.TRANSITIVE_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristicAxiom(axiom, NodeLabels.IRREFLEXIVE_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
    return translateBinaryArgsAxiom(axiom,
                                    NodeLabels.SUB_DATA_PROPERTY_OF,
                                    EdgeLabels.SUB_DATA_PROPERTY_EXPRESSION,
                                    axiom.getSubProperty(),
                                    EdgeLabels.SUPER_DATA_PROPERTY_EXPRESSION,
                                    axiom.getSuperProperty());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
    return translateObjectPropertyCharacteristicAxiom(axiom,
                                                      NodeLabels.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSameIndividualAxiom axiom) {
    return translateNaryArgsAxiom(axiom,
                                  NodeLabels.SAME_INDIVIDUAL,
                                  axiom.getIndividuals(),
                                  EdgeLabels.INDIVIDUAL);
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
    return translateBinaryArgsAxiom(axiom,
                                    NodeLabels.INVERSE_OBJECT_PROPERTIES,
                                    EdgeLabels.OBJECT_PROPERTY_EXPRESSION,
                                    axiom.getFirstProperty(),
                                    EdgeLabels.OBJECT_PROPERTY_EXPRESSION,
                                    axiom.getSecondProperty());
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
    return translateTernaryArgsAxiom(axiom,
                                     NodeLabels.ANNOTATION_ASSERTION,
                                     EdgeLabels.ANNOTATION_PROPERTY,
                                     axiom.getProperty(),
                                     EdgeLabels.ANNOTATION_SUBJECT,
                                     axiom.getSubject(),
                                     EdgeLabels.ANNOTATION_VALUE,
                                     axiom.getValue());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
    return translateBinaryArgsAxiom(axiom,
                                    NodeLabels.SUB_ANNOTATION_PROPERTY_OF,
                                    EdgeLabels.SUB_ANNOTATION_PROPERTY,
                                    axiom.getSubProperty(),
                                    EdgeLabels.SUPER_ANNOTATION_PROPERTY,
                                    axiom.getSuperProperty());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
    return translateBinaryArgsAxiom(axiom,
                                    NodeLabels.ANNOTATION_PROPERTY_DOMAIN,
                                    EdgeLabels.ANNOTATION_PROPERTY,
                                    axiom.getProperty(),
                                    EdgeLabels.DOMAIN,
                                    axiom.getDomain());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
    return translateBinaryArgsAxiom(axiom,
                                    NodeLabels.ANNOTATION_PROPERTY_RANGE,
                                    EdgeLabels.ANNOTATION_PROPERTY,
                                    axiom.getProperty(),
                                    EdgeLabels.RANGE,
                                    axiom.getRange());
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
