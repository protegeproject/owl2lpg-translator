package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.model.StructuralEdgeFactory;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.ClassExpressionTranslator;
import edu.stanford.owl2lpg.translator.DataRangeTranslator;
import edu.stanford.owl2lpg.translator.EntityTranslator;
import edu.stanford.owl2lpg.translator.IndividualTranslator;
import edu.stanford.owl2lpg.translator.LiteralTranslator;
import edu.stanford.owl2lpg.translator.PropertyExpressionTranslator;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectDigester;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_ALL_VALUES_FROM;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_EXACT_CARDINALITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_MAX_CARDINALITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_MIN_CARDINALITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_SOME_VALUES_FROM;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_ALL_VALUES_FROM;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_COMPLEMENT_OF;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_EXACT_CARDINALITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_HAS_VALUE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_INTERSECTION_OF;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_MAX_CARDINALITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_MIN_CARDINALITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_SOME_VALUES_FROM;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_UNION_OF;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.CARDINALITY;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.DIGEST;

/**
 * A visitor that contains the implementation to translate the OWL 2 literals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassExpressionVisitor implements OWLClassExpressionVisitorEx<Translation> {

  @Nonnull
  private final StructuralEdgeFactory structuralEdgeFactory;

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
  private final OntologyObjectDigester ontologyObjectDigester;

  @Inject
  public ClassExpressionVisitor(@Nonnull StructuralEdgeFactory structuralEdgeFactory,
                                @Nonnull EntityTranslator entityTranslator,
                                @Nonnull ClassExpressionTranslator classExprTranslator,
                                @Nonnull PropertyExpressionTranslator propertyExprTranslator,
                                @Nonnull DataRangeTranslator dataRangeTranslator,
                                @Nonnull LiteralTranslator literalTranslator,
                                @Nonnull IndividualTranslator individualTranslator,
                                @Nonnull OntologyObjectDigester ontologyObjectDigester) {
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.entityTranslator = checkNotNull(entityTranslator);
    this.classExprTranslator = checkNotNull(classExprTranslator);
    this.propertyExprTranslator = checkNotNull(propertyExprTranslator);
    this.dataRangeTranslator = checkNotNull(dataRangeTranslator);
    this.literalTranslator = checkNotNull(literalTranslator);
    this.individualTranslator = checkNotNull(individualTranslator);
    this.ontologyObjectDigester = checkNotNull(ontologyObjectDigester);
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
    var mainNode = createClassExprNode(ce, OBJECT_COMPLEMENT_OF);
    var classExprTranslation = classExprTranslator.translate(ce.getOperand());
    var classExprEdge = structuralEdgeFactory.getClassExpressionEdge(mainNode, classExprTranslation.getMainNode());
    return Translation.create(ce, mainNode,
        ImmutableList.of(classExprEdge),
        ImmutableList.of(classExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectSomeValuesFrom ce) {
    return translateObjectRestriction(ce, OBJECT_SOME_VALUES_FROM);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectAllValuesFrom ce) {
    return translateObjectRestriction(ce, OBJECT_ALL_VALUES_FROM);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectHasValue ce) {
    var mainNode = createClassExprNode(ce, OBJECT_HAS_VALUE);
    var propertyTranslation = propertyExprTranslator.translate(ce.getProperty());
    var fillerTranslation = individualTranslator.translate(ce.getFiller());
    var edges = ImmutableList.<Edge>builder();
    edges.add(structuralEdgeFactory.getObjectPropertyExpressionEdge(mainNode, propertyTranslation.getMainNode()));
    edges.add(structuralEdgeFactory.getIndividualEdge(mainNode, fillerTranslation.getMainNode()));
    return Translation.create(ce, mainNode,
        edges.build(),
        ImmutableList.of(propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectMinCardinality ce) {
    return translateObjectRestriction(ce, OBJECT_MIN_CARDINALITY);
  }


  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectExactCardinality ce) {
    return translateObjectRestriction(ce, OBJECT_EXACT_CARDINALITY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectMaxCardinality ce) {
    return translateObjectRestriction(ce, OBJECT_MAX_CARDINALITY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectHasSelf ce) {
    var mainNode = createClassExprNode(ce, NodeLabels.OBJECT_HAS_SELF);
    var propertyExprTranslation = propertyExprTranslator.translate(ce.getProperty());
    var propertyExprEdge = structuralEdgeFactory.getObjectPropertyExpressionEdge(mainNode, propertyExprTranslation.getMainNode());
    return Translation.create(ce, mainNode,
        ImmutableList.of(propertyExprEdge),
        ImmutableList.of(propertyExprTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectOneOf ce) {
    var mainNode = createClassExprNode(ce, NodeLabels.OBJECT_ONE_OF);
    var translations = ImmutableList.<Translation>builder();
    var edges = ImmutableList.<Edge>builder();
    var individuals = ce.getIndividuals();
    for (var ind : individuals) {
      var translation = individualTranslator.translate(ind);
      translations.add(translation);
      edges.add(structuralEdgeFactory.getIndividualEdge(mainNode, translation.getMainNode()));
    }
    return Translation.create(ce, mainNode,
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
    var mainNode = createClassExprNode(ce, NodeLabels.DATA_HAS_VALUE);
    var propertyExprTranslation = propertyExprTranslator.translate(ce.getProperty());
    var fillerTranslation = literalTranslator.translate(ce.getFiller());
    var edges = ImmutableList.<Edge>builder();
    edges.add(structuralEdgeFactory.getDataPropertyExpressionEdge(mainNode, propertyExprTranslation.getMainNode()));
    edges.add(structuralEdgeFactory.getLiteralEdge(mainNode, fillerTranslation.getMainNode()));
    return Translation.create(ce, mainNode,
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
                                                   @Nonnull NodeLabels labels) {
    var mainNode = createClassExprNode(ce, labels);
    var translations = ImmutableList.<Translation>builder();
    var edges = ImmutableList.<Edge>builder();
    var operands = ce.getOperands();
    for (var op : operands) {
      var translation = classExprTranslator.translate(op);
      translations.add(translation);
      edges.add(structuralEdgeFactory.getClassExpressionEdge(mainNode, translation.getMainNode()));
    }
    return Translation.create(ce, mainNode,
        edges.build(),
        translations.build());
  }

  private Translation translateObjectRestriction(@Nonnull OWLQuantifiedObjectRestriction ce,
                                                 @Nonnull NodeLabels labels) {
    var mainNode = getMainNode(ce, labels);
    var propertyTranslation = propertyExprTranslator.translate(ce.getProperty());
    var fillerTranslation = classExprTranslator.translate(ce.getFiller());
    var edges = ImmutableList.<Edge>builder();
    edges.add(structuralEdgeFactory.getObjectPropertyExpressionEdge(mainNode, propertyTranslation.getMainNode()));
    edges.add(structuralEdgeFactory.getClassExpressionEdge(mainNode, fillerTranslation.getMainNode()));
    return Translation.create(ce, mainNode,
        edges.build(),
        ImmutableList.of(propertyTranslation, fillerTranslation));
  }

  private Translation translateDataRestriction(@Nonnull OWLQuantifiedDataRestriction ce,
                                               @Nonnull NodeLabels nodeLabels) {
    var mainNode = getMainNode(ce, nodeLabels);
    var propertyExprTranslation = propertyExprTranslator.translate(ce.getProperty());
    var fillerTranslation = dataRangeTranslator.translate(ce.getFiller());
    var edges = ImmutableList.<Edge>builder();
    edges.add(structuralEdgeFactory.getDataPropertyExpressionEdge(mainNode, propertyExprTranslation.getMainNode()));
    edges.add(structuralEdgeFactory.getDataRangeEdge(mainNode, fillerTranslation.getMainNode()));
    return Translation.create(ce, mainNode,
        edges.build(),
        ImmutableList.of(propertyExprTranslation, fillerTranslation));
  }

  @Nonnull
  private Node getMainNode(@Nonnull OWLRestriction restriction,
                           @Nonnull NodeLabels labels) {
    if (restriction instanceof HasCardinality) {
      var cardinality = ((HasCardinality) restriction).getCardinality();
      return createClassExprNode(restriction, labels, Properties.of(CARDINALITY, cardinality));
    } else {
      return createClassExprNode(restriction, labels);
    }
  }

  @Nonnull
  private Node createClassExprNode(OWLClassExpression ce, NodeLabels nodeLabels) {
    var digestString = ontologyObjectDigester.getDigest(ce);
    var nodeId = NodeId.create(digestString);
    return Node.create(nodeId, nodeLabels, Properties.of(DIGEST, digestString));
  }

  @Nonnull
  private Node createClassExprNode(OWLClassExpression ce, NodeLabels nodeLabels, Properties properties) {
    var digestString = ontologyObjectDigester.getDigest(ce);
    var nodeId = NodeId.create(digestString);
    return Node.create(nodeId, nodeLabels, properties.extend(Properties.of(DIGEST, digestString)));
  }
}
