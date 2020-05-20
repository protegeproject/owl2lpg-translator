package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.*;
import edu.stanford.owl2lpg.translator.ClassExpressionTranslator;
import edu.stanford.owl2lpg.translator.EntityTranslator;
import edu.stanford.owl2lpg.translator.PropertyExpressionTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.*;

/**
 * A visitor that contains the implementation to translate the OWL 2 literals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassExpressionVisitor implements OWLClassExpressionVisitorEx<Translation> {

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final ClassExpressionTranslator ceTranslator;

  @Nonnull
  private final EntityTranslator entityTranslator;

  @Nonnull
  private final PropertyExpressionTranslator propertyExpressionTranslator;


  @Inject
  public ClassExpressionVisitor(@Nonnull EdgeFactory edgeFactory, @Nonnull NodeFactory nodeFactory, @Nonnull ClassExpressionTranslator ceTranslator, @Nonnull EntityTranslator entityTranslator, @Nonnull PropertyExpressionTranslator propertyExpressionTranslator) {
    this.entityTranslator = entityTranslator;
    this.edgeFactory = checkNotNull(edgeFactory);
    this.nodeFactory = checkNotNull(nodeFactory);
    this.ceTranslator = checkNotNull(ceTranslator);
    this.propertyExpressionTranslator = propertyExpressionTranslator;
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClass c) {
    return entityTranslator.translate(c);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectIntersectionOf ce) {
    return translateNaryClassExpression(ce, OBJECT_INTERSECTION_OF);
  }

  private Translation translateNaryClassExpression(@Nonnull OWLNaryBooleanClassExpression ce, ImmutableList<String> labels) {
    var mainNode = nodeFactory.createNode(ce, labels);
    var translations = ImmutableList.<Translation>builder();
    var edges = ImmutableList.<Edge>builder();
    var operands = ce.getOperands();
    for(var op : operands) {
      var translation = ceTranslator.translate(op);
      translations.add(translation);
      var edge = edgeFactory.createEdge(mainNode,
                                        translation.getMainNode(),
                                        CLASS_EXPRESSION, Properties.empty());
      edges.add(edge);
    }
    return Translation.create(mainNode,
        edges.build(),
        translations.build());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectUnionOf ce) {
    return translateNaryClassExpression(ce, OBJECT_UNION_OF);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectComplementOf ce) {
    return translateNaryClassExpression(ce, )
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectSomeValuesFrom ce) {
    return translateRestriction(ce,
                                OBJECT_SOME_VALUES_FROM,
                                OBJECT_PROPERTY_EXPRESSION,
                                CLASS_EXPRESSION);
  }

  private Translation translateRestriction(@Nonnull OWLQuantifiedObjectRestriction ce, ImmutableList<String> labels, EdgeLabel propertyEdgeLabel, EdgeLabel fillerEdgeLabel) {
    var mainNode = nodeFactory.createNode(ce, labels);
    var propertyTranslation = propertyExpressionTranslator.translate(ce.getProperty());
    var fillerTranslation = ceTranslator.translate(ce.getFiller());
    var edges = ImmutableList.<Edge>builder();
    edges.add(edgeFactory.createEdge(mainNode, propertyTranslation.getMainNode(),
                                     propertyEdgeLabel, Properties.empty()));
    edges.add(edgeFactory.createEdge(mainNode, fillerTranslation.getMainNode(), fillerEdgeLabel, Properties.empty()));
    return Translation.create(mainNode,
                              edges.build(),
                              ImmutableList.of(propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectAllValuesFrom ce) {
    return translateRestriction(ce,
                                OBJECT_ALL_VALUES_FROM,
                                OBJECT_PROPERTY_EXPRESSION,
                                CLASS_EXPRESSION);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectHasValue ce) {
    return translateRestriction(ce,
                                OBJECT_HAS_VALUE,
                                OBJECT_PROPERTY_EXPRESSION,
                                INDIVIDUAL);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectMinCardinality ce) {
    mainNode = createCardinalityNode(ce, NodeLabels.OBJECT_MIN_CARDINALITY);
    var propertyExprEdge = createEdge(ce.getProperty(), OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(ce.getProperty());
    var classExprEdge = createEdge(ce.getFiller(), CLASS_EXPRESSION);
    var classExprTranslation = createNestedTranslation(ce.getFiller());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, classExprEdge),
        ImmutableList.of(propertyExprTranslation, classExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectExactCardinality ce) {
    mainNode = createCardinalityNode(ce, NodeLabels.OBJECT_EXACT_CARDINALITY);
    var propertyExprEdge = createEdge(ce.getProperty(), OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(ce.getProperty());
    var classExprEdge = createEdge(ce.getFiller(), CLASS_EXPRESSION);
    var classExprTranslation = createNestedTranslation(ce.getFiller());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, classExprEdge),
        ImmutableList.of(propertyExprTranslation, classExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectMaxCardinality ce) {
    mainNode = createCardinalityNode(ce, NodeLabels.OBJECT_MAX_CARDINALITY);
    var propertyExprEdge = createEdge(ce.getProperty(), OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(ce.getProperty());
    var classExprEdge = createEdge(ce.getFiller(), CLASS_EXPRESSION);
    var classExprTranslation = createNestedTranslation(ce.getFiller());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, classExprEdge),
        ImmutableList.of(propertyExprTranslation, classExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectHasSelf ce) {
    mainNode = nodeFactory.createNode(ce, NodeLabels.OBJECT_HAS_SELF);
    var propertyExprEdge = createEdge(ce.getProperty(), OBJECT_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(ce.getProperty());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge),
        ImmutableList.of(propertyExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectOneOf ce) {
    mainNode = nodeFactory.createNode(ce, NodeLabels.OBJECT_ONE_OF);
    var individualEdges = createEdges(ce.getIndividuals(), INDIVIDUAL);
    var individualTranslations = createNestedTranslations(ce.getIndividuals());
    return Translation.create(mainNode,
        ImmutableList.copyOf(individualEdges),
        ImmutableList.copyOf(individualTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataSomeValuesFrom ce) {
    mainNode = nodeFactory.createNode(ce, NodeLabels.DATA_SOME_VALUES_FROM);
    var propertyExprEdge = createEdge(ce.getProperty(), DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(ce.getProperty());
    var dataRangeEdge = createEdge(ce.getFiller(), DATA_RANGE);
    var dataRangeTranslation = createNestedTranslation(ce.getFiller());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, dataRangeEdge),
        ImmutableList.of(propertyExprTranslation, dataRangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataAllValuesFrom ce) {
    mainNode = nodeFactory.createNode(ce, NodeLabels.DATA_ALL_VALUES_FROM);
    var propertyExprEdge = createEdge(ce.getProperty(), DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(ce.getProperty());
    var dataRangeEdge = createEdge(ce.getFiller(), DATA_RANGE);
    var dataRangeTranslation = createNestedTranslation(ce.getFiller());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, dataRangeEdge),
        ImmutableList.of(propertyExprTranslation, dataRangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataHasValue ce) {
    mainNode = nodeFactory.createNode(ce, NodeLabels.DATA_HAS_VALUE);
    var propertyExprEdge = createEdge(ce.getProperty(), DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(ce.getProperty());
    var literalEdge = createEdge(ce.getFiller(), LITERAL);
    var literalTranslation = createNestedTranslation(ce.getFiller());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, literalEdge),
        ImmutableList.of(propertyExprTranslation, literalTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataMinCardinality ce) {
    mainNode = createCardinalityNode(ce, NodeLabels.DATA_MIN_CARDINALITY);
    var propertyExprEdge = createEdge(ce.getProperty(), DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(ce.getProperty());
    var dataRangeEdge = createEdge(ce.getFiller(), DATA_RANGE);
    var dataRangeTranslation = createNestedTranslation(ce.getFiller());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, dataRangeEdge),
        ImmutableList.of(propertyExprTranslation, dataRangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataExactCardinality ce) {
    mainNode = createCardinalityNode(ce, NodeLabels.DATA_EXACT_CARDINALITY);
    var propertyExprEdge = createEdge(ce.getProperty(), DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(ce.getProperty());
    var dataRangeEdge = createEdge(ce.getFiller(), DATA_RANGE);
    var dataRangeTranslation = createNestedTranslation(ce.getFiller());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, dataRangeEdge),
        ImmutableList.of(propertyExprTranslation, dataRangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataMaxCardinality ce) {
    mainNode = createCardinalityNode(ce, NodeLabels.DATA_MAX_CARDINALITY);
    var propertyExprEdge = createEdge(ce.getProperty(), DATA_PROPERTY_EXPRESSION);
    var propertyExprTranslation = createNestedTranslation(ce.getProperty());
    var dataRangeEdge = createEdge(ce.getFiller(), DATA_RANGE);
    var dataRangeTranslation = createNestedTranslation(ce.getFiller());
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge, dataRangeEdge),
        ImmutableList.of(propertyExprTranslation, dataRangeTranslation));
  }
}
