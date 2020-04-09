package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.*;
import static edu.stanford.owl2lpg.translator.Translation.MainNode;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.CARDINALITY;

/**
 * A visitor that contains the implementation to translate the OWL 2 literals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassExpressionVisitor implements OWLClassExpressionVisitorEx<Translation> {

  @Nonnull
  private final OWLEntityVisitorEx<Translation> entityVisitor;

  @Nonnull
  private final OWLPropertyExpressionVisitorEx<Translation> propertyExpressionVisitor;

  @Nonnull
  private final OWLIndividualVisitorEx<Translation> individualVisitor;

  @Nonnull
  private final OWLDataVisitorEx<Translation> dataVisitor;

  @Inject
  public ClassExpressionVisitor(@Nonnull OWLEntityVisitorEx<Translation> entityVisitor,
                                @Nonnull OWLPropertyExpressionVisitorEx<Translation> propertyExpressionVisitor,
                                @Nonnull OWLIndividualVisitorEx<Translation> individualVisitor,
                                @Nonnull OWLDataVisitorEx<Translation> dataVisitor) {
    this.entityVisitor = checkNotNull(entityVisitor);
    this.propertyExpressionVisitor = checkNotNull(propertyExpressionVisitor);
    this.individualVisitor = checkNotNull(individualVisitor);
    this.dataVisitor = checkNotNull(dataVisitor);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClass c) {
    return entityVisitor.visit(c);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectIntersectionOf ce) {
    var intersectionNode = Node(NodeLabels.OBJECT_INTERSECTION_OF, withIdentifierFrom(ce));
    var translations = createClassExpressionTranslations(ce.getOperands());
    var edges = translations.stream()
        .map(translation -> Edge(intersectionNode, MainNode(translation), EdgeLabels.CLASS_EXPRESSION))
        .collect(Collectors.toList());
    return Translation.create(intersectionNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectUnionOf ce) {
    var unionNode = Node(NodeLabels.OBJECT_UNION_OF, withIdentifierFrom(ce));
    var translations = createClassExpressionTranslations(ce.getOperands());
    var edges = translations.stream()
        .map(translation -> Edge(unionNode, MainNode(translation), EdgeLabels.CLASS_EXPRESSION))
        .collect(Collectors.toList());
    return Translation.create(unionNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectComplementOf ce) {
    var complementNode = Node(NodeLabels.OBJECT_COMPLEMENT_OF, withIdentifierFrom(ce));
    var operandTranslation = createClassExpressionTranslation(ce.getOperand());
    return Translation.create(complementNode,
        ImmutableList.of(
            Edge(complementNode, MainNode(operandTranslation), EdgeLabels.CLASS_EXPRESSION)),
        ImmutableList.of(
            operandTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectSomeValuesFrom ce) {
    var qualifierNode = Node(NodeLabels.OBJECT_SOME_VALUES_FROM, withIdentifierFrom(ce));
    var propertyTranslation = createPropertyExpressionTranslation(ce.getProperty());
    var fillerTranslation = createClassExpressionTranslation(ce.getFiller());
    return Translation.create(qualifierNode,
        ImmutableList.of(
            Edge(qualifierNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
            Edge(qualifierNode, MainNode(fillerTranslation), EdgeLabels.CLASS_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectAllValuesFrom ce) {
    var qualifierNode = Node(NodeLabels.OBJECT_ALL_VALUES_FROM, withIdentifierFrom(ce));
    var propertyTranslation = createPropertyExpressionTranslation(ce.getProperty());
    var fillerTranslation = createClassExpressionTranslation(ce.getFiller());
    return Translation.create(qualifierNode,
        ImmutableList.of(
            Edge(qualifierNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
            Edge(qualifierNode, MainNode(fillerTranslation), EdgeLabels.CLASS_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectHasValue ce) {
    var qualifierNode = Node(NodeLabels.OBJECT_HAS_VALUE, withIdentifierFrom(ce));
    var propertyTranslation = createPropertyExpressionTranslation(ce.getProperty());
    var fillerTranslation = createIndividualTranslation(ce.getFiller());
    return Translation.create(qualifierNode,
        ImmutableList.of(
            Edge(qualifierNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
            Edge(qualifierNode, MainNode(fillerTranslation), EdgeLabels.INDIVIDUAL)),
        ImmutableList.of(
            propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectMinCardinality ce) {
    var cardinalityNode = Node(NodeLabels.OBJECT_MIN_CARDINALITY,
        Properties(CARDINALITY, ce.getCardinality()),
        withIdentifierFrom(ce));
    var propertyTranslation = createPropertyExpressionTranslation(ce.getProperty());
    var fillerTranslation = createClassExpressionTranslation(ce.getFiller());
    return Translation.create(cardinalityNode,
        ImmutableList.of(
            Edge(cardinalityNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
            Edge(cardinalityNode, MainNode(fillerTranslation), EdgeLabels.CLASS_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectExactCardinality ce) {
    var cardinalityNode = Node(NodeLabels.OBJECT_EXACT_CARDINALITY,
        Properties(CARDINALITY, ce.getCardinality()),
        withIdentifierFrom(ce));
    var propertyTranslation = createPropertyExpressionTranslation(ce.getProperty());
    var fillerTranslation = createClassExpressionTranslation(ce.getFiller());
    return Translation.create(cardinalityNode,
        ImmutableList.of(
            Edge(cardinalityNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
            Edge(cardinalityNode, MainNode(fillerTranslation), EdgeLabels.CLASS_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectMaxCardinality ce) {
    var cardinalityNode = Node(NodeLabels.OBJECT_MAX_CARDINALITY,
        Properties(CARDINALITY, ce.getCardinality()),
        withIdentifierFrom(ce));
    var propertyTranslation = createPropertyExpressionTranslation(ce.getProperty());
    var fillerTranslation = createClassExpressionTranslation(ce.getFiller());
    return Translation.create(cardinalityNode,
        ImmutableList.of(
            Edge(cardinalityNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
            Edge(cardinalityNode, MainNode(fillerTranslation), EdgeLabels.CLASS_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectHasSelf ce) {
    var restrictionNode = Node(NodeLabels.OBJECT_HAS_SELF, withIdentifierFrom(ce));
    var propertyTranslation = createPropertyExpressionTranslation(ce.getProperty());
    return Translation.create(restrictionNode,
        ImmutableList.of(
            Edge(restrictionNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY_EXPRESSION)),
        ImmutableList.of(
            propertyTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectOneOf ce) {
    var enumerationNode = Node(NodeLabels.OBJECT_ONE_OF, withIdentifierFrom(ce));
    var translations = createIndividualTranslations(ce.getIndividuals());
    var edges = translations.stream()
        .map(translation -> Edge(enumerationNode, MainNode(translation), EdgeLabels.INDIVIDUAL))
        .collect(Collectors.toList());
    return Translation.create(enumerationNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataSomeValuesFrom ce) {
    var qualifierNode = Node(NodeLabels.DATA_SOME_VALUES_FROM, withIdentifierFrom(ce));
    var propertyTranslation = createPropertyExpressionTranslation(ce.getProperty());
    var fillerTranslation = createDataRangeTranslation(ce.getFiller());
    return Translation.create(qualifierNode,
        ImmutableList.of(
            Edge(qualifierNode, MainNode(propertyTranslation), EdgeLabels.DATA_PROPERTY_EXPRESSION),
            Edge(qualifierNode, MainNode(fillerTranslation), EdgeLabels.DATA_RANGE)),
        ImmutableList.of(
            propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataAllValuesFrom ce) {
    var qualifierNode = Node(NodeLabels.DATA_ALL_VALUES_FROM, withIdentifierFrom(ce));
    var propertyTranslation = createPropertyExpressionTranslation(ce.getProperty());
    var fillerTranslation = createDataRangeTranslation(ce.getFiller());
    return Translation.create(qualifierNode,
        ImmutableList.of(
            Edge(qualifierNode, MainNode(propertyTranslation), EdgeLabels.DATA_PROPERTY_EXPRESSION),
            Edge(qualifierNode, MainNode(fillerTranslation), EdgeLabels.DATA_RANGE)),
        ImmutableList.of(
            propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataHasValue ce) {
    var qualifierNode = Node(NodeLabels.DATA_HAS_VALUE, withIdentifierFrom(ce));
    var propertyTranslation = createPropertyExpressionTranslation(ce.getProperty());
    var fillerTranslation = createLiteralTranslation(ce.getFiller());
    return Translation.create(qualifierNode,
        ImmutableList.of(
            Edge(qualifierNode, MainNode(propertyTranslation), EdgeLabels.DATA_PROPERTY_EXPRESSION),
            Edge(qualifierNode, MainNode(fillerTranslation), EdgeLabels.LITERAL)),
        ImmutableList.of(
            propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataMinCardinality ce) {
    var cardinalityNode = Node(NodeLabels.DATA_MIN_CARDINALITY,
        Properties(CARDINALITY, ce.getCardinality()),
        withIdentifierFrom(ce));
    var propertyTranslation = createPropertyExpressionTranslation(ce.getProperty());
    var fillerTranslation = createDataRangeTranslation(ce.getFiller());
    return Translation.create(cardinalityNode,
        ImmutableList.of(
            Edge(cardinalityNode, MainNode(propertyTranslation), EdgeLabels.DATA_PROPERTY_EXPRESSION),
            Edge(cardinalityNode, MainNode(fillerTranslation), EdgeLabels.DATA_RANGE)),
        ImmutableList.of(
            propertyTranslation, fillerTranslation
        )
    );
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataExactCardinality ce) {
    var cardinalityNode = Node(NodeLabels.DATA_EXACT_CARDINALITY,
        Properties(CARDINALITY, ce.getCardinality()),
        withIdentifierFrom(ce));
    var propertyTranslation = createPropertyExpressionTranslation(ce.getProperty());
    var fillerTranslation = createDataRangeTranslation(ce.getFiller());
    return Translation.create(cardinalityNode,
        ImmutableList.of(
            Edge(cardinalityNode, MainNode(propertyTranslation), EdgeLabels.DATA_PROPERTY_EXPRESSION),
            Edge(cardinalityNode, MainNode(fillerTranslation), EdgeLabels.DATA_RANGE)),
        ImmutableList.of(
            propertyTranslation, fillerTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataMaxCardinality ce) {
    var cardinalityNode = Node(NodeLabels.DATA_MAX_CARDINALITY,
        Properties(CARDINALITY, ce.getCardinality()),
        withIdentifierFrom(ce));
    var propertyTranslation = ce.getProperty().accept(propertyExpressionVisitor);
    var fillerTranslation = createDataRangeTranslation(ce.getFiller());
    return Translation.create(cardinalityNode,
        ImmutableList.of(
            Edge(cardinalityNode, MainNode(propertyTranslation), EdgeLabels.DATA_PROPERTY_EXPRESSION),
            Edge(cardinalityNode, MainNode(fillerTranslation), EdgeLabels.DATA_RANGE)),
        ImmutableList.of(
            propertyTranslation, fillerTranslation));
  }

  private List<Translation> createClassExpressionTranslations(Set<? extends OWLClassExpression> classExpressions) {
    return classExpressions.stream()
        .map(this::createClassExpressionTranslation)
        .collect(Collectors.toList());
  }

  private Translation createClassExpressionTranslation(OWLClassExpression classExpression) {
    return classExpression.accept(this);
  }

  private Translation createPropertyExpressionTranslation(OWLPropertyExpression propertyExpression) {
    return propertyExpression.accept(propertyExpressionVisitor);
  }

  private List<Translation> createIndividualTranslations(Set<OWLIndividual> individuals) {
    return individuals.stream()
        .map(this::createIndividualTranslation)
        .collect(Collectors.toList());
  }

  private Translation createIndividualTranslation(OWLIndividual individual) {
    return individual.accept(individualVisitor);
  }

  private Translation createLiteralTranslation(@Nonnull OWLLiteral literal) {
    return literal.accept(dataVisitor);
  }

  private Translation createDataRangeTranslation(OWLDataRange dataRange) {
    return dataRange.accept(dataVisitor);
  }
}
