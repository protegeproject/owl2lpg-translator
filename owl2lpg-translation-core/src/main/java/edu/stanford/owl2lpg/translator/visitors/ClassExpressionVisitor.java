package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.*;
import edu.stanford.owl2lpg.translator.*;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.LITERAL;
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

  @Inject
  public ClassExpressionVisitor(@Nonnull NodeFactory nodeFactory,
                                @Nonnull EdgeFactory edgeFactory,
                                @Nonnull EntityTranslator entityTranslator,
                                @Nonnull ClassExpressionTranslator classExprTranslator,
                                @Nonnull PropertyExpressionTranslator propertyExprTranslator,
                                @Nonnull DataRangeTranslator dataRangeTranslator,
                                @Nonnull LiteralTranslator literalTranslator,
                                @Nonnull IndividualTranslator individualTranslator) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
    this.entityTranslator = checkNotNull(entityTranslator);
    this.classExprTranslator = checkNotNull(classExprTranslator);
    this.propertyExprTranslator = checkNotNull(propertyExprTranslator);
    this.dataRangeTranslator = checkNotNull(dataRangeTranslator);
    this.literalTranslator = checkNotNull(literalTranslator);
    this.individualTranslator = checkNotNull(individualTranslator);
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

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectUnionOf ce) {
    return translateNaryClassExpression(ce, OBJECT_UNION_OF);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectComplementOf ce) {
    var mainNode = nodeFactory.createNode(ce, NodeLabels.OBJECT_COMPLEMENT_OF);
    var classExprTranslation = classExprTranslator.translate(ce.getOperand());
    var classExprEdge = edgeFactory.createEdge(mainNode,
        classExprTranslation.getMainNode(),
        CLASS_EXPRESSION);
    return Translation.create(mainNode,
        ImmutableList.of(classExprEdge),
        ImmutableList.of(classExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectSomeValuesFrom ce) {
    return translateObjectRestriction(ce, OBJECT_SOME_VALUES_FROM
    );
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectAllValuesFrom ce) {
    return translateObjectRestriction(ce, OBJECT_ALL_VALUES_FROM
    );
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectHasValue ce) {
    var mainNode = nodeFactory.createNode(ce, OBJECT_HAS_VALUE);
    var propertyTranslation = propertyExprTranslator.translate(ce.getProperty());
    var fillerTranslation = individualTranslator.translate(ce.getFiller());
    var edges = ImmutableList.<Edge>builder();
    edges.add(edgeFactory.createEdge(mainNode,
        propertyTranslation.getMainNode(),
        OBJECT_PROPERTY_EXPRESSION));
    edges.add(edgeFactory.createEdge(mainNode,
        fillerTranslation.getMainNode(),
        INDIVIDUAL));
    return Translation.create(mainNode,
        edges.build(),
        ImmutableList.of(propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectMinCardinality ce) {
    return translateObjectRestriction(ce, OBJECT_MIN_CARDINALITY
    );
  }


  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectExactCardinality ce) {
    return translateObjectRestriction(ce, OBJECT_EXACT_CARDINALITY
    );
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectMaxCardinality ce) {
    return translateObjectRestriction(ce, OBJECT_MAX_CARDINALITY
    );
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectHasSelf ce) {
    var mainNode = nodeFactory.createNode(ce, NodeLabels.OBJECT_HAS_SELF);
    var propertyExprTranslation = propertyExprTranslator.translate(ce.getProperty());
    var propertyExprEdge = edgeFactory.createEdge(mainNode,
        propertyExprTranslation.getMainNode(),
        OBJECT_PROPERTY_EXPRESSION);
    return Translation.create(mainNode,
        ImmutableList.of(propertyExprEdge),
        ImmutableList.of(propertyExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectOneOf ce) {
    var mainNode = nodeFactory.createNode(ce, NodeLabels.OBJECT_ONE_OF);
    var translations = ImmutableList.<Translation>builder();
    var edges = ImmutableList.<Edge>builder();
    var individuals = ce.getIndividuals();
    for (var a : individuals) {
      var translation = individualTranslator.translate(a);
      translations.add(translation);
      edges.add(edgeFactory.createEdge(mainNode,
          translation.getMainNode(),
          INDIVIDUAL));
    }
    return Translation.create(mainNode,
        edges.build(),
        translations.build());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataSomeValuesFrom ce) {
    return translateDataRestriction(ce, DATA_SOME_VALUES_FROM);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataAllValuesFrom ce) {
    return translateDataRestriction(ce, DATA_ALL_VALUES_FROM);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataHasValue ce) {
    var mainNode = nodeFactory.createNode(ce, NodeLabels.DATA_HAS_VALUE);
    var propertyExprTranslation = propertyExprTranslator.translate(ce.getProperty());
    var fillerTranslation = literalTranslator.translate(ce.getFiller());
    var edges = ImmutableList.<Edge>builder();
    edges.add(edgeFactory.createEdge(mainNode,
        propertyExprTranslation.getMainNode(),
        DATA_PROPERTY_EXPRESSION));
    edges.add(edgeFactory.createEdge(mainNode,
        fillerTranslation.getMainNode(),
        LITERAL));
    return Translation.create(mainNode,
        edges.build(),
        ImmutableList.of(propertyExprTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataMinCardinality ce) {
    return translateDataRestriction(ce, DATA_MIN_CARDINALITY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataExactCardinality ce) {
    return translateDataRestriction(ce, DATA_EXACT_CARDINALITY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataMaxCardinality ce) {
    return translateDataRestriction(ce, DATA_MAX_CARDINALITY);
  }

  private Translation translateNaryClassExpression(@Nonnull OWLNaryBooleanClassExpression ce,
                                                   @Nonnull ImmutableList<String> labels) {
    var mainNode = nodeFactory.createNode(ce, labels);
    var translations = ImmutableList.<Translation>builder();
    var edges = ImmutableList.<Edge>builder();
    var operands = ce.getOperands();
    for (var op : operands) {
      var translation = classExprTranslator.translate(op);
      translations.add(translation);
      edges.add(edgeFactory.createEdge(mainNode,
          translation.getMainNode(),
          CLASS_EXPRESSION, Properties.empty()));
    }
    return Translation.create(mainNode,
        edges.build(),
        translations.build());
  }

  private Translation translateObjectRestriction(@Nonnull OWLQuantifiedObjectRestriction ce,
                                                 @Nonnull ImmutableList<String> labels) {
    var mainNode = getMainNode(ce, labels);
    var propertyTranslation = propertyExprTranslator.translate(ce.getProperty());
    var fillerTranslation = classExprTranslator.translate(ce.getFiller());
    var edges = ImmutableList.<Edge>builder();
    edges.add(edgeFactory.createEdge(mainNode,
        propertyTranslation.getMainNode(),
        OBJECT_PROPERTY_EXPRESSION));
    edges.add(edgeFactory.createEdge(mainNode,
        fillerTranslation.getMainNode(),
        CLASS_EXPRESSION));
    return Translation.create(mainNode,
        edges.build(),
        ImmutableList.of(propertyTranslation, fillerTranslation));
  }

  private Translation translateDataRestriction(@Nonnull OWLQuantifiedDataRestriction ce,
                                               @Nonnull ImmutableList<String> nodeLabels) {
    var mainNode = getMainNode(ce, nodeLabels);
    var propertyExprTranslation = propertyExprTranslator.translate(ce.getProperty());
    var fillerTranslation = dataRangeTranslator.translate(ce.getFiller());
    var edges = ImmutableList.<Edge>builder();
    edges.add(edgeFactory.createEdge(mainNode,
        propertyExprTranslation.getMainNode(),
        DATA_PROPERTY_EXPRESSION));
    edges.add(edgeFactory.createEdge(mainNode,
        fillerTranslation.getMainNode(),
        DATA_RANGE));
    return Translation.create(mainNode,
        edges.build(),
        ImmutableList.of(propertyExprTranslation, fillerTranslation));
  }

  private Node getMainNode(@Nonnull OWLRestriction restriction,
                           @Nonnull ImmutableList<String> labels) {
    if (restriction instanceof HasCardinality) {
      var cardinality = ((HasCardinality) restriction).getCardinality();
      return nodeFactory.createNode(restriction, labels,
          Properties.of(PropertyFields.CARDINALITY, cardinality));
    } else {
      return nodeFactory.createNode(restriction, labels);
    }
  }
}
