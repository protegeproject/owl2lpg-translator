package edu.stanford.owl2lpg.translator.impl;

import edu.stanford.owl2lpg.datastructure.AnyNode;
import edu.stanford.owl2lpg.datastructure.Edge;
import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.EdgeLabels;
import edu.stanford.owl2lpg.translator.NodeLabels;
import edu.stanford.owl2lpg.translator.PropertyNames;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.*;

/**
 * The translator sub-module for translating the OWL 2 class expressions to
 * labelled property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassExpressionTranslator extends HasIriTranslator
    implements OWLClassExpressionVisitorEx<Graph> {

  @Override
  public Graph visit(@Nonnull OWLClass ce) {
    Node entityNode = Node(NodeLabels.CLASS);
    Node iriNode = createIriNode(ce);
    return Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLObjectIntersectionOf ce) {
    Node intersectionNode = Node(NodeLabels.OBJECT_INTERSECTION_OF);
    List<Edge> listOfEdges = ce.getOperandsAsList().stream()
        .map(operand -> Edge(
            intersectionNode, operand.accept(this), EdgeLabels.CLASS_EXPRESSION))
        .collect(Collectors.toList());
    return Graph(listOfEdges);
  }


  @Override
  public Graph visit(@Nonnull OWLObjectUnionOf ce) {
    Node unionNode = Node(NodeLabels.OBJECT_UNION_OF);
    List<Edge> listOfEdges = ce.getOperandsAsList().stream()
        .map(operand -> Edge(
            unionNode, operand.accept(this), EdgeLabels.CLASS_EXPRESSION))
        .collect(Collectors.toList());
    return Graph(listOfEdges);
  }

  @Override
  public Graph visit(@Nonnull OWLObjectComplementOf ce) {
    Node complementNode = Node(NodeLabels.OBJECT_COMPLEMENT_OF);
    Graph operandGraph = ce.getOperand().accept(this);
    return Graph(
        Edge(complementNode, operandGraph, EdgeLabels.CLASS_EXPRESSION)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLObjectSomeValuesFrom ce) {
    Node qualifierNode = Node(NodeLabels.OBJECT_SOME_VALUES_FROM);
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    Graph fillerGraph = ce.getFiller().accept(this);
    return Graph(
        Edge(qualifierNode, propertyGraph, EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
        Edge(qualifierNode, fillerGraph, EdgeLabels.CLASS_EXPRESSION)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLObjectAllValuesFrom ce) {
    Node qualifierNode = Node(NodeLabels.OBJECT_ALL_VALUES_FROM);
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    Graph fillerGraph = ce.getFiller().accept(this);
    return Graph(
        Edge(qualifierNode, propertyGraph, EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
        Edge(qualifierNode, fillerGraph, EdgeLabels.CLASS_EXPRESSION)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLObjectHasValue ce) {
    Node qualifierNode = Node(NodeLabels.OBJECT_HAS_VALUE);
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    AnyNode fillerGraph = ce.getFiller().accept(new IndividualTranslator());
    return Graph(
        Edge(qualifierNode, propertyGraph, EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
        Edge(qualifierNode, fillerGraph, EdgeLabels.INDIVIDUAL)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLObjectMinCardinality ce) {
    Node cardinalityNode = Node(NodeLabels.OBJECT_MIN_CARDINALITY,
        PropertiesBuilder.create()
            .set(PropertyNames.CARDINALITY, ce.getCardinality()).build());
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    Graph fillerNode = ce.getFiller().accept(this);
    return Graph(
        Edge(cardinalityNode, propertyGraph, EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
        Edge(cardinalityNode, fillerNode, EdgeLabels.CLASS_EXPRESSION)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLObjectExactCardinality ce) {
    Node cardinalityNode = Node(NodeLabels.OBJECT_EXACT_CARDINALITY,
        PropertiesBuilder.create()
            .set(PropertyNames.CARDINALITY, ce.getCardinality()).build());
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    Graph fillerNode = ce.getFiller().accept(this);
    return Graph(
        Edge(cardinalityNode, propertyGraph, EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
        Edge(cardinalityNode, fillerNode, EdgeLabels.CLASS_EXPRESSION)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLObjectMaxCardinality ce) {
    Node cardinalityNode = Node(NodeLabels.OBJECT_MAX_CARDINALITY,
        PropertiesBuilder.create()
            .set(PropertyNames.CARDINALITY, ce.getCardinality()).build());
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    Graph fillerNode = ce.getFiller().accept(this);
    return Graph(
        Edge(cardinalityNode, propertyGraph, EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
        Edge(cardinalityNode, fillerNode, EdgeLabels.CLASS_EXPRESSION)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLObjectHasSelf ce) {
    Node restrictionNode = Node(NodeLabels.OBJECT_HAS_SELF);
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    return Graph(
        Edge(restrictionNode, propertyGraph, EdgeLabels.OBJECT_PROPERTY_EXPRESSION)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLObjectOneOf ce) {
    Node enumerationNode = Node(NodeLabels.OBJECT_ONE_OF);
    List<Edge> listOfEdges = ce.getOperandsAsList().stream()
        .map(operand -> Edge(
            enumerationNode, operand.accept(new IndividualTranslator()), EdgeLabels.INDIVIDUAL))
        .collect(Collectors.toList());
    return Graph(listOfEdges);
  }

  @Override
  public Graph visit(@Nonnull OWLDataSomeValuesFrom ce) {
    Node qualifierNode = Node(NodeLabels.DATA_SOME_VALUES_FROM);
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    Graph dataRangeGraph = ce.getFiller().accept(new DataRangeTranslator());
    return Graph(
        Edge(qualifierNode, propertyGraph, EdgeLabels.DATA_PROPERTY_EXPRESSION),
        Edge(qualifierNode, dataRangeGraph, EdgeLabels.DATA_RANGE)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLDataAllValuesFrom ce) {
    Node qualifierNode = Node(NodeLabels.DATA_ALL_VALUES_FROM);
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    Graph dataRangeGraph = ce.getFiller().accept(new DataRangeTranslator());
    return Graph(
        Edge(qualifierNode, propertyGraph, EdgeLabels.DATA_PROPERTY_EXPRESSION),
        Edge(qualifierNode, dataRangeGraph, EdgeLabels.DATA_RANGE)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLDataHasValue ce) {
    Node qualifierNode = Node(NodeLabels.DATA_HAS_VALUE);
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    Graph literalGraph = ce.getFiller().accept(new LiteralTranslator());
    return Graph(
        Edge(qualifierNode, propertyGraph, EdgeLabels.DATA_PROPERTY_EXPRESSION),
        Edge(qualifierNode, literalGraph, EdgeLabels.LITERAL)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLDataMinCardinality ce) {
    Node cardinalityNode = Node(NodeLabels.DATA_MIN_CARDINALITY,
        PropertiesBuilder.create()
            .set(PropertyNames.CARDINALITY, ce.getCardinality()).build());
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    return Graph(
        Edge(cardinalityNode, propertyGraph, EdgeLabels.DATA_PROPERTY_EXPRESSION)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLDataExactCardinality ce) {
    Node cardinalityNode = Node(NodeLabels.DATA_EXACT_CARDINALITY,
        PropertiesBuilder.create()
            .set(PropertyNames.CARDINALITY, ce.getCardinality()).build());
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    return Graph(
        Edge(cardinalityNode, propertyGraph, EdgeLabels.DATA_PROPERTY_EXPRESSION)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLDataMaxCardinality ce) {
    Node cardinalityNode = Node(NodeLabels.DATA_MAX_CARDINALITY,
        PropertiesBuilder.create()
            .set(PropertyNames.CARDINALITY, ce.getCardinality()).build());
    Graph propertyGraph = ce.getProperty().accept(new PropertyExpressionTranslator());
    return Graph(
        Edge(cardinalityNode, propertyGraph, EdgeLabels.DATA_PROPERTY_EXPRESSION)
    );
  }
}
