package edu.stanford.owl2lpg.translator.visitors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationSubjectVisitorTest {

  private AnnotationSubjectVisitor visitor;

  // @formatter:off
  @Mock private NodeIdMapper nodeIdMapper;
  @Mock private VisitorFactory visitorFactory;
  @Mock private AnnotationValueVisitor annotationValueVisitor;
  // @formatter:off

  @Before
  public void setUp() {
    when(visitorFactory.getNodeIdMapper()).thenReturn(nodeIdMapper);
    visitor = spy(new AnnotationSubjectVisitor(visitorFactory));
    when(visitorFactory.createAnnotationValueVisitor()).thenReturn(annotationValueVisitor);
  }

  @Test
  public void shouldVisitIri() {
    var iri = mock(IRI.class);
    visitor.visit(iri);
    verify(visitor).visit(iri);
    verify(visitorFactory).createAnnotationValueVisitor();
    verify(annotationValueVisitor).visit(iri);
  }

  @Test
  public void shouldVisitAnonymousIndividual() {
    var individual = mock(OWLAnonymousIndividual.class);
    visitor.visit(individual);
    verify(visitor).visit(individual);
    verify(visitorFactory).createAnnotationValueVisitor();
    verify(annotationValueVisitor).visit(individual);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenVisitorFactoryNull() {
    VisitorFactory nullVisitorFactory = null;
    new AnnotationSubjectVisitor(nullVisitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenIriNull() {
    IRI nullIri = null;
    visitor.visit(nullIri);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenAnonymousIndividualNull() {
    OWLAnonymousIndividual nullIndividual = null;
    visitor.visit(nullIndividual);
  }
}