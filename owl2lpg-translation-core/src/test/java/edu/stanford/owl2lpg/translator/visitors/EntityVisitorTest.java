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
    verify(visitor).createMainNode(cls, NodeLabels.CLASS);
    verify(visitor).createEntityIriEdge(cls, EdgeLabels.ENTITY_IRI);
    verify(visitor).createIriTranslation(cls);
  }

  @Test
  public void shouldVisitDatatype() {
    var datatypeIri = mock(IRI.class);
    var dt = mock(OWLDatatype.class);
    when(dt.getIRI()).thenReturn(datatypeIri);

    visitor.visit(dt);
    verify(visitor).visit(dt);
    verify(visitor).createMainNode(dt, NodeLabels.DATATYPE);
    verify(visitor).createEntityIriEdge(dt, EdgeLabels.ENTITY_IRI);
    verify(visitor).createIriTranslation(dt);
  }

  @Test
  public void shouldVisitObjectProperty() {
    var propertyIri = mock(IRI.class);
    var op = mock(OWLObjectProperty.class);
    when(op.getIRI()).thenReturn(propertyIri);

    visitor.visit(op);
    verify(visitor).visit(op);
    verify(visitor).createMainNode(op, NodeLabels.OBJECT_PROPERTY);
    verify(visitor).createEntityIriEdge(op, EdgeLabels.ENTITY_IRI);
    verify(visitor).createIriTranslation(op);
  }

  @Test
  public void shouldVisitDataProperty() {
    var propertyIri = mock(IRI.class);
    var dp = mock(OWLDataProperty.class);
    when(dp.getIRI()).thenReturn(propertyIri);

    visitor.visit(dp);
    verify(visitor).visit(dp);
    verify(visitor).createMainNode(dp, NodeLabels.DATA_PROPERTY);
    verify(visitor).createEntityIriEdge(dp, EdgeLabels.ENTITY_IRI);
    verify(visitor).createIriTranslation(dp);
  }

  @Test
  public void shouldVisitAnnotationProperty() {
    var propertyIri = mock(IRI.class);
    var ap = mock(OWLAnnotationProperty.class);
    when(ap.getIRI()).thenReturn(propertyIri);

    visitor.visit(ap);
    verify(visitor).visit(ap);
    verify(visitor).createMainNode(ap, NodeLabels.ANNOTATION_PROPERTY);
    verify(visitor).createEntityIriEdge(ap, EdgeLabels.ENTITY_IRI);
    verify(visitor).createIriTranslation(ap);
  }

  @Test
  public void shouldVisitNamedIndividual() {
    var individualIri = mock(IRI.class);
    var a = mock(OWLNamedIndividual.class);
    when(a.getIRI()).thenReturn(individualIri);

    visitor.visit(a);
    verify(visitor).visit(a);
    verify(visitor).createMainNode(a, NodeLabels.NAMED_INDIVIDUAL);
    verify(visitor).createEntityIriEdge(a, EdgeLabels.ENTITY_IRI);
    verify(visitor).createIriTranslation(a);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenEntityNull() {
    OWLClass nullClass = null;
    visitor.visit(nullClass);
  }
}