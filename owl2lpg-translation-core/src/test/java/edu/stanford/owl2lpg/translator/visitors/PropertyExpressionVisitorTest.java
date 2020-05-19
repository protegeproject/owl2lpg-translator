package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PropertyExpressionVisitorTest {

  private PropertyExpressionVisitor visitor;

  // @formatter:off
  @Mock private NodeIdMapper nodeIdMapper;
  @Mock private VisitorFactory visitorFactory;

  @Mock private EntityVisitor entityVisitor;
  @Mock private OWLObjectPropertyExpression objectPropertyExpression;

  @Mock private NodeId nodeId;
  @Mock private Translation nestedTranslation;
  @Mock private Node nestedTranslationMainNode;
  // @formatter:off

  @Before
  public void setUp() {
    when(visitorFactory.getNodeIdMapper()).thenReturn(nodeIdMapper);
    visitor = spy(new PropertyExpressionVisitor(visitorFactory));
    when(visitor.getTranslation(objectPropertyExpression)).thenReturn(nestedTranslation);
    when(visitorFactory.createEntityVisitor()).thenReturn(entityVisitor);
    when(nestedTranslation.getMainNode()).thenReturn(nestedTranslationMainNode);
  }

  @Test
  public void shouldVisitDataProperty() {
    var dp = mock(OWLDataProperty.class);
    visitor.visit(dp);
    verify(visitor).visit(dp);
    verify(visitorFactory).createEntityVisitor();
    verify(entityVisitor).visit(dp);
  }

  @Test
  public void shouldVisitObjectProperty() {
    var op = mock(OWLObjectProperty.class);
    visitor.visit(op);
    verify(visitor).visit(op);
    verify(visitorFactory).createEntityVisitor();
    verify(entityVisitor).visit(op);
  }

  @Test
  public void shouldVisitAnnotationProperty() {
    var ap = mock(OWLAnnotationProperty.class);
    visitor.visit(ap);
    verify(visitor).visit(ap);
    verify(visitorFactory).createEntityVisitor();
    verify(entityVisitor).visit(ap);
  }

  @Test
  public void shouldVisitObjectInverseOf() {
    var ope = mock(OWLObjectInverseOf.class);
    when(ope.getInverseProperty()).thenReturn(objectPropertyExpression);
    when(nodeIdMapper.get(ope)).thenReturn(nodeId);

    visitor.visit(ope);
    verify(visitor).visit(ope);
    verify(visitor).createNode(ope, NodeLabels.OBJECT_INVERSE_OF);
    verify(visitor).createEdge(ope.getInverseProperty(), EdgeLabel.OBJECT_PROPERTY);
    verify(visitor).createNestedTranslation(ope.getInverseProperty());
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenVisitorFactoryNull() {
    VisitorFactory nullVisitorFactory = null;
    new PropertyExpressionVisitor(nullVisitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenPropertyNull() {
    OWLObjectProperty nullProperty = null;
    visitor.visit(nullProperty);
  }
}