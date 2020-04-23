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
public class EntityVisitorTest {

  private EntityVisitor visitor;

  // @formatter:off
  @Mock private NodeIdMapper nodeIdMapper;
  @Mock private VisitorFactory visitorFactory;

  @Mock private IRI anyIRI;

  @Mock private Translation nestedTranslation;
  @Mock private Node nestedTranslationMainNode;
  // @formatter:off

  @Before
  public void setUp() {
    visitor = spy(new EntityVisitor(nodeIdMapper, visitorFactory));
    when(visitor.getTranslation(anyIRI)).thenReturn(nestedTranslation);
    when(nestedTranslation.getMainNode()).thenReturn(nestedTranslationMainNode);
  }

  @Test
  public void shouldVisitClass() {
    var cls = mock(OWLClass.class);
    when(cls.getIRI()).thenReturn(anyIRI);

    visitor.visit(cls);
    verify(visitor).visit(cls);
    verify(visitor).createEntityNode(cls, NodeLabels.CLASS);
    verify(visitor).createEdge(cls.getIRI(), EdgeLabels.ENTITY_IRI);
    verify(visitor).createNestedTranslation(cls.getIRI());
  }

  @Test
  public void shouldVisitDatatype() {
    var dt = mock(OWLDatatype.class);
    when(dt.getIRI()).thenReturn(anyIRI);

    visitor.visit(dt);
    verify(visitor).visit(dt);
    verify(visitor).createEntityNode(dt, NodeLabels.DATATYPE);
    verify(visitor).createEdge(dt.getIRI(), EdgeLabels.ENTITY_IRI);
    verify(visitor).createNestedTranslation(dt.getIRI());
  }

  @Test
  public void shouldVisitObjectProperty() {
    var op = mock(OWLObjectProperty.class);
    when(op.getIRI()).thenReturn(anyIRI);

    visitor.visit(op);
    verify(visitor).visit(op);
    verify(visitor).createEntityNode(op, NodeLabels.OBJECT_PROPERTY);
    verify(visitor).createEdge(op.getIRI(), EdgeLabels.ENTITY_IRI);
    verify(visitor).createNestedTranslation(op.getIRI());
  }

  @Test
  public void shouldVisitDataProperty() {
    var dp = mock(OWLDataProperty.class);
    when(dp.getIRI()).thenReturn(anyIRI);

    visitor.visit(dp);
    verify(visitor).visit(dp);
    verify(visitor).createEntityNode(dp, NodeLabels.DATA_PROPERTY);
    verify(visitor).createEdge(dp.getIRI(), EdgeLabels.ENTITY_IRI);
    verify(visitor).createNestedTranslation(dp.getIRI());
  }

  @Test
  public void shouldVisitAnnotationProperty() {
    var ap = mock(OWLAnnotationProperty.class);
    when(ap.getIRI()).thenReturn(anyIRI);

    visitor.visit(ap);
    verify(visitor).visit(ap);
    verify(visitor).createEntityNode(ap, NodeLabels.ANNOTATION_PROPERTY);
    verify(visitor).createEdge(ap.getIRI(), EdgeLabels.ENTITY_IRI);
    verify(visitor).createNestedTranslation(ap.getIRI());
  }

  @Test
  public void shouldVisitNamedIndividual() {
    var a = mock(OWLNamedIndividual.class);
    when(a.getIRI()).thenReturn(anyIRI);

    visitor.visit(a);
    verify(visitor).visit(a);
    verify(visitor).createEntityNode(a, NodeLabels.NAMED_INDIVIDUAL);
    verify(visitor).createEdge(a.getIRI(), EdgeLabels.ENTITY_IRI);
    verify(visitor).createNestedTranslation(a.getIRI());
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenNodeIdMapperNull() {
    NodeIdMapper nullIdMapper = null;
    new EntityVisitor(nullIdMapper, visitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenVisitorFactoryNull() {
    VisitorFactory nullVisitorFactory = null;
    new EntityVisitor(nodeIdMapper, nullVisitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenEntityNull() {
    OWLClass nullClass = null;
    visitor.visit(nullClass);
  }
}