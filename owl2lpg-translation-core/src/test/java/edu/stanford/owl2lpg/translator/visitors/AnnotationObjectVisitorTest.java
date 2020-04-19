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

import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationObjectVisitorTest {

  private AnnotationObjectVisitor visitor;

  @Mock
  private EntityVisitor entityVisitor;

  @Mock
  private AnnotationValueVisitor annotationValueVisitor;

  @Mock
  private OWLAnnotationProperty annotationProperty;

  @Mock
  private OWLAnnotationValue annotationValue;

  @Mock
  private Set<OWLAnnotation> annotationAnnotations;

  @Mock
  private Translation nestedTranslation;

  @Mock
  private Node nestedTranslationMainNode;

  @Before
  public void setUp() {
    visitor = spy(new AnnotationObjectVisitor(entityVisitor, annotationValueVisitor));
    when(annotationProperty.accept(entityVisitor)).thenReturn(nestedTranslation);
    when(annotationValue.accept(visitor)).thenReturn(nestedTranslation);
    when(nestedTranslation.getMainNode()).thenReturn(nestedTranslationMainNode);
  }

  @Test
  public void shouldVisitTranslateAnnotation() {
    var annotation = mock(OWLAnnotation.class);
    when(annotation.getProperty()).thenReturn(annotationProperty);
    when(annotation.getValue()).thenReturn(annotationValue);
    when(annotation.getAnnotations()).thenReturn(annotationAnnotations);

    visitor.visit(annotation);
    verify(visitor).visit(annotation);
    verify(visitor).createNode(annotation, NodeLabels.ANNOTATION);
    verify(visitor).createEdge(annotation.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    verify(visitor).createNestedTranslation(annotation.getProperty());
    verify(visitor).createEdge(annotation.getValue(), EdgeLabels.ANNOTATION_VALUE);
    verify(visitor).createNestedTranslation(annotation.getValue());
    verify(visitor).createEdges(annotation.getAnnotations(), EdgeLabels.ANNOTATION_ANNOTATION);
    verify(visitor).createNestedTranslations(annotation.getAnnotations());
  }

  @Test
  public void shouldVisitIri() {
    var iri = mock(IRI.class);
    visitor.visit(iri);
    verify(visitor).visit(iri);
    verify(annotationValueVisitor).visit(iri);
  }

  @Test
  public void shouldVisitAnonymousIndividual() {
    var individual = mock(OWLAnonymousIndividual.class);
    visitor.visit(individual);
    verify(visitor).visit(individual);
    verify(annotationValueVisitor).visit(individual);
  }

  @Test
  public void shouldVisitLiteral() {
    var literal = mock(OWLLiteral.class);
    visitor.visit(literal);
    verify(visitor).visit(literal);
    verify(annotationValueVisitor).visit(literal);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenEntityVisitorNull() {
    EntityVisitor nulEntityVisitor = null;
    new AnnotationObjectVisitor(nulEntityVisitor, annotationValueVisitor);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenAnnotationValueVisitorNull() {
    AnnotationValueVisitor nullAnnotationValueVisitor = null;
    new AnnotationObjectVisitor(entityVisitor, nullAnnotationValueVisitor);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenAnnotationNull() {
    OWLAnnotation nullAnnotation = null;
    visitor.visit(nullAnnotation);
  }
}