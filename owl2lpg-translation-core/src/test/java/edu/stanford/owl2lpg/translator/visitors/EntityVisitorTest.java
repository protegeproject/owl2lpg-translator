package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EntityVisitorTest {

  @Spy
  private EntityVisitor visitor = new EntityVisitor();

  @Test
  public void shouldVisitClass() {
    var classIri = mock(IRI.class);
    var cls = mock(OWLClass.class);
    when(cls.getIRI()).thenReturn(classIri);

    visitor.visit(cls);
    verify(visitor).visit(cls);
    verify(visitor).createEntityNode(cls, NodeLabels.CLASS);
    verify(visitor).createEdge(cls.getIRI(), EdgeLabels.ENTITY_IRI);
    verify(visitor).createNestedTranslation(cls.getIRI());
  }

  @Test
  public void shouldVisitDatatype() {
    var datatypeIri = mock(IRI.class);
    var dt = mock(OWLDatatype.class);
    when(dt.getIRI()).thenReturn(datatypeIri);

    visitor.visit(dt);
    verify(visitor).visit(dt);
    verify(visitor).createEntityNode(dt, NodeLabels.DATATYPE);
    verify(visitor).createEdge(dt.getIRI(), EdgeLabels.ENTITY_IRI);
    verify(visitor).createNestedTranslation(dt.getIRI());
  }

  @Test
  public void shouldVisitObjectProperty() {
    var propertyIri = mock(IRI.class);
    var op = mock(OWLObjectProperty.class);
    when(op.getIRI()).thenReturn(propertyIri);

    visitor.visit(op);
    verify(visitor).visit(op);
    verify(visitor).createEntityNode(op, NodeLabels.OBJECT_PROPERTY);
    verify(visitor).createEdge(op.getIRI(), EdgeLabels.ENTITY_IRI);
    verify(visitor).createNestedTranslation(op.getIRI());
  }

  @Test
  public void shouldVisitDataProperty() {
    var propertyIri = mock(IRI.class);
    var dp = mock(OWLDataProperty.class);
    when(dp.getIRI()).thenReturn(propertyIri);

    visitor.visit(dp);
    verify(visitor).visit(dp);
    verify(visitor).createEntityNode(dp, NodeLabels.DATA_PROPERTY);
    verify(visitor).createEdge(dp.getIRI(), EdgeLabels.ENTITY_IRI);
    verify(visitor).createNestedTranslation(dp.getIRI());
  }

  @Test
  public void shouldVisitAnnotationProperty() {
    var propertyIri = mock(IRI.class);
    var ap = mock(OWLAnnotationProperty.class);
    when(ap.getIRI()).thenReturn(propertyIri);

    visitor.visit(ap);
    verify(visitor).visit(ap);
    verify(visitor).createEntityNode(ap, NodeLabels.ANNOTATION_PROPERTY);
    verify(visitor).createEdge(ap.getIRI(), EdgeLabels.ENTITY_IRI);
    verify(visitor).createNestedTranslation(ap.getIRI());
  }

  @Test
  public void shouldVisitNamedIndividual() {
    var individualIri = mock(IRI.class);
    var a = mock(OWLNamedIndividual.class);
    when(a.getIRI()).thenReturn(individualIri);

    visitor.visit(a);
    verify(visitor).visit(a);
    verify(visitor).createEntityNode(a, NodeLabels.NAMED_INDIVIDUAL);
    verify(visitor).createEdge(a.getIRI(), EdgeLabels.ENTITY_IRI);
    verify(visitor).createNestedTranslation(a.getIRI());
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenEntityNull() {
    OWLClass nullClass = null;
    visitor.visit(nullClass);
  }
}