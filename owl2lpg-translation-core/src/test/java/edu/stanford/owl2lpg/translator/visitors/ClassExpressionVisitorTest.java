package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.*;

import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClassExpressionVisitorTest {

  private ClassExpressionVisitor visitor;

  // @formatter:off
  @Mock private NodeIdMapper nodeIdMapper;
  @Mock private VisitorFactory visitorFactory;
  @Mock private EntityVisitor entityVisitor;

  @Mock private OWLClassExpression anyClassExpression;
  @Mock private Set<OWLClassExpression> classExpressions;
  @Mock private OWLObjectPropertyExpression anyObjectPropertyExpression;
  @Mock private OWLDataPropertyExpression anyDataPropertyExpression;
  @Mock private OWLIndividual anyIndividual;
  @Mock private Set<OWLIndividual> individuals;
  @Mock private OWLDataRange anyDataRange;
  @Mock private OWLLiteral anyLiteral;

  @Mock private NodeId nodeId;
  @Mock private Translation nestedTranslation;
  @Mock private Node nestedTranslationMainNode;
  // @formatter:off

  @Before
  public void setUp() {
    visitor = spy(new ClassExpressionVisitor(nodeIdMapper, visitorFactory));
    when(visitorFactory.createEntityVisitor()).thenReturn(entityVisitor);
    when(visitor.getTranslation(anyClassExpression)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyObjectPropertyExpression)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyDataPropertyExpression)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyIndividual)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyDataRange)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyLiteral)).thenReturn(nestedTranslation);
    when(nestedTranslation.getMainNode()).thenReturn(nestedTranslationMainNode);
  }

  @Test
  public void shouldVisitClass() {
    var c = mock(OWLClass.class);
    visitor.visit(c);
    verify(visitor).visit(c);
    verify(visitorFactory).createEntityVisitor();
    verify(entityVisitor).visit(c);
  }

  @Test
  public void shouldVisitObjectIntersectionOf() {
    var ce = mock(OWLObjectIntersectionOf.class);
    when(ce.getOperands()).thenReturn(classExpressions);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createNode(ce, NodeLabels.OBJECT_INTERSECTION_OF);
    verify(visitor).createEdges(ce.getOperands(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslations(ce.getOperands());
  }

  @Test
  public void shouldVisitObjectUnionOf() {
    var ce = mock(OWLObjectUnionOf.class);
    when(ce.getOperands()).thenReturn(classExpressions);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createNode(ce, NodeLabels.OBJECT_UNION_OF);
    verify(visitor).createEdges(ce.getOperands(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslations(ce.getOperands());
  }

  @Test
  public void shouldVisitObjectComplementOf() {
    var ce = mock(OWLObjectComplementOf.class);
    when(ce.getOperand()).thenReturn(anyClassExpression);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createNode(ce, NodeLabels.OBJECT_COMPLEMENT_OF);
    verify(visitor).createEdge(ce.getOperand(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getOperand());
  }

  @Test
  public void shouldVisitObjectSomeValuesFrom() {
    var ce = mock(OWLObjectSomeValuesFrom.class);
    when(ce.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(ce.getFiller()).thenReturn(anyClassExpression);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createNode(ce, NodeLabels.OBJECT_SOME_VALUES_FROM);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
    verify(visitor).createEdge(ce.getFiller(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getFiller());
  }

  @Test
  public void shouldVisitObjectAllValuesFrom() {
    var ce = mock(OWLObjectAllValuesFrom.class);
    when(ce.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(ce.getFiller()).thenReturn(anyClassExpression);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createNode(ce, NodeLabels.OBJECT_ALL_VALUES_FROM);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
    verify(visitor).createEdge(ce.getFiller(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getFiller());
  }

  @Test
  public void shouldVisitObjectHasValue() {
    var ce = mock(OWLObjectHasValue.class);
    when(ce.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(ce.getFiller()).thenReturn(anyIndividual);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createNode(ce, NodeLabels.OBJECT_HAS_VALUE);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
    verify(visitor).createEdge(ce.getFiller(), EdgeLabels.INDIVIDUAL);
    verify(visitor).createNestedTranslation(ce.getFiller());
  }

  @Test
  public void shouldVisitObjectMinCardinality() {
    var ce = mock(OWLObjectMinCardinality.class);
    when(ce.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(ce.getFiller()).thenReturn(anyClassExpression);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createCardinalityNode(ce, NodeLabels.OBJECT_MIN_CARDINALITY);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
    verify(visitor).createEdge(ce.getFiller(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getFiller());
  }

  @Test
  public void shouldVisitObjectExactCardinality() {
    var ce = mock(OWLObjectExactCardinality.class);
    when(ce.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(ce.getFiller()).thenReturn(anyClassExpression);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createCardinalityNode(ce, NodeLabels.OBJECT_EXACT_CARDINALITY);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
    verify(visitor).createEdge(ce.getFiller(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getFiller());
  }

  @Test
  public void shouldVisitObjectMaxCardinality() {
    var ce = mock(OWLObjectMaxCardinality.class);
    when(ce.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(ce.getFiller()).thenReturn(anyClassExpression);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createCardinalityNode(ce, NodeLabels.OBJECT_MAX_CARDINALITY);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
    verify(visitor).createEdge(ce.getFiller(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getFiller());
  }

  @Test
  public void shouldVisitObjectHasSelf() {
    var ce = mock(OWLObjectHasSelf.class);
    when(ce.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createNode(ce, NodeLabels.OBJECT_HAS_SELF);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
  }

  @Test
  public void shouldVisitObjectOneOf() {
    var ce = mock(OWLObjectOneOf.class);
    when(ce.getIndividuals()).thenReturn(individuals);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).createNode(ce, NodeLabels.OBJECT_ONE_OF);
    verify(visitor).createEdges(ce.getIndividuals(), EdgeLabels.INDIVIDUAL);
    verify(visitor).createNestedTranslations(ce.getIndividuals());
  }

  @Test
  public void shouldVisitDataSomeValuesFrom() {
    var ce = mock(OWLDataSomeValuesFrom.class);
    when(ce.getProperty()).thenReturn(anyDataPropertyExpression);
    when(ce.getFiller()).thenReturn(anyDataRange);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).createNode(ce, NodeLabels.DATA_SOME_VALUES_FROM);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
    verify(visitor).createEdge(ce.getFiller(), EdgeLabels.DATA_RANGE);
    verify(visitor).createNestedTranslation(ce.getFiller());
  }

  @Test
  public void shouldVisitDataAllValuesFrom() {
    var ce = mock(OWLDataAllValuesFrom.class);
    when(ce.getProperty()).thenReturn(anyDataPropertyExpression);
    when(ce.getFiller()).thenReturn(anyDataRange);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).createNode(ce, NodeLabels.DATA_ALL_VALUES_FROM);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
    verify(visitor).createEdge(ce.getFiller(), EdgeLabels.DATA_RANGE);
    verify(visitor).createNestedTranslation(ce.getFiller());
  }

  @Test
  public void shouldVisitDataHasValue() {
    var ce = mock(OWLDataHasValue.class);
    when(ce.getProperty()).thenReturn(anyDataPropertyExpression);
    when(ce.getFiller()).thenReturn(anyLiteral);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).createNode(ce, NodeLabels.DATA_HAS_VALUE);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
    verify(visitor).createEdge(ce.getFiller(), EdgeLabels.LITERAL);
    verify(visitor).createNestedTranslation(ce.getFiller());
  }

  @Test
  public void shouldVisitDataMinCardinality() {
    var ce = mock(OWLDataMinCardinality.class);
    when(ce.getProperty()).thenReturn(anyDataPropertyExpression);
    when(ce.getFiller()).thenReturn(anyDataRange);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createCardinalityNode(ce, NodeLabels.DATA_MIN_CARDINALITY);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
    verify(visitor).createEdge(ce.getFiller(), EdgeLabels.DATA_RANGE);
    verify(visitor).createNestedTranslation(ce.getFiller());
  }

  @Test
  public void shouldVisitDataExactCardinality() {
    var ce = mock(OWLDataExactCardinality.class);
    when(ce.getProperty()).thenReturn(anyDataPropertyExpression);
    when(ce.getFiller()).thenReturn(anyDataRange);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createCardinalityNode(ce, NodeLabels.DATA_EXACT_CARDINALITY);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
    verify(visitor).createEdge(ce.getFiller(), EdgeLabels.DATA_RANGE);
    verify(visitor).createNestedTranslation(ce.getFiller());
  }

  @Test
  public void shouldVisitDataMaxCardinality() {
    var ce = mock(OWLDataMaxCardinality.class);
    when(ce.getProperty()).thenReturn(anyDataPropertyExpression);
    when(ce.getFiller()).thenReturn(anyDataRange);
    when(nodeIdMapper.get(ce)).thenReturn(nodeId);

    visitor.visit(ce);
    verify(visitor).visit(ce);
    verify(visitor).createCardinalityNode(ce, NodeLabels.DATA_MAX_CARDINALITY);
    verify(visitor).createEdge(ce.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(ce.getProperty());
    verify(visitor).createEdge(ce.getFiller(), EdgeLabels.DATA_RANGE);
    verify(visitor).createNestedTranslation(ce.getFiller());
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenNodeIdMapperNull() {
    NodeIdMapper nullIdMapper = null;
    new ClassExpressionVisitor(nullIdMapper, visitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenVisitorFactoryNull() {
    VisitorFactory nullVisitorFactory = null;
    new ClassExpressionVisitor(nodeIdMapper, nullVisitorFactory);
  }
}