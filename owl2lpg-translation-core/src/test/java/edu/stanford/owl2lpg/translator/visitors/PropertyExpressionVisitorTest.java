package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
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

  @Mock
  private EntityVisitor entityVisitor;

  @Mock
  private OWLObjectPropertyExpression objectPropertyExpression;

  @Mock
  private Translation nestedTranslation;

  @Mock
  private Node nestedTranslationMainNode;

  private PropertyExpressionVisitor visitor;

  @Before
  public void setUp() {
    visitor = spy(new PropertyExpressionVisitor(entityVisitor));
    when(objectPropertyExpression.accept(visitor)).thenReturn(nestedTranslation);
    when(nestedTranslation.getMainNode()).thenReturn(nestedTranslationMainNode);
  }

  @Test
  public void shouldVisitDataProperty() {
    var dp = mock(OWLDataProperty.class);
    visitor.visit(dp);
    verify(visitor).visit(dp);
    verify(entityVisitor).visit(dp);
  }

  @Test
  public void shouldVisitObjectProperty() {
    var op = mock(OWLObjectProperty.class);
    visitor.visit(op);
    verify(visitor).visit(op);
    verify(entityVisitor).visit(op);
  }

  @Test
  public void shouldVisitAnnotationProperty() {
    var ap = mock(OWLAnnotationProperty.class);
    visitor.visit(ap);
    verify(visitor).visit(ap);
    verify(entityVisitor).visit(ap);
  }

  @Test
  public void shouldVisitObjectInverseOf() {
    var ope = mock(OWLObjectInverseOf.class);
    when(ope.getInverseProperty()).thenReturn(objectPropertyExpression);

    visitor.visit(ope);
    verify(visitor).visit(ope);
    verify(visitor).createNode(ope, NodeLabels.OBJECT_INVERSE_OF);
    verify(visitor).createEdge(ope.getInverseProperty(), EdgeLabels.OBJECT_PROPERTY);
    verify(visitor).createTranslation(ope.getInverseProperty());
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenPropertyNull() {
    OWLObjectProperty nullProperty = null;
    visitor.visit(nullProperty);
  }
}