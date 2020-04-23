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

@RunWith(MockitoJUnitRunner.Silent.class)
public class AnnotationObjectVisitorTest {

  private AnnotationObjectVisitor visitor;

  // @formatter:off
  @Mock private NodeIdMapper nodeIdMapper;
  @Mock private VisitorFactory visitorFactory;

  @Mock private AnnotationValueVisitor annotationValueVisitor;
  @Mock private IndividualVisitor individualVisitor;
  @Mock private DataVisitor dataVisitor;

  @Mock private OWLAnnotationProperty annotationProperty;
  @Mock private OWLAnnotationValue annotationValue;
  @Mock private OWLAnnotation annotationAnnotation;
  @Mock private Set<OWLAnnotation> annotationAnnotations;

  @Mock private NodeId nodeId;
  @Mock private Translation nestedTranslation;
  @Mock private Node nestedTranslationMainNode;
  // @formatter:off

  @Before
  public void setUp() {
    visitor = spy(new AnnotationObjectVisitor(nodeIdMapper, visitorFactory));
    when(visitor.getTranslation(annotationProperty)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(annotationValue)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(annotationAnnotation)).thenReturn(nestedTranslation);
    when(visitorFactory.createAnnotationValueVisitor()).thenReturn(annotationValueVisitor);
    when(visitorFactory.createIndividualVisitor()).thenReturn(individualVisitor);
    when(visitorFactory.createDataVisitor()).thenReturn(dataVisitor);
    when(nestedTranslation.getMainNode()).thenReturn(nestedTranslationMainNode);
  }

  @Test
  public void shouldVisitAnnotation() {
    var annotation = mock(OWLAnnotation.class);
    when(annotation.getProperty()).thenReturn(annotationProperty);
    when(annotation.getValue()).thenReturn(annotationValue);
    when(annotation.getAnnotations()).thenReturn(annotationAnnotations);
    when(nodeIdMapper.get(annotation)).thenReturn(nodeId);

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
    verify(visitorFactory).createAnnotationValueVisitor();
    verify(annotationValueVisitor).visit(iri);
  }

  @Test
  public void shouldVisitAnonymousIndividual() {
    var individual = mock(OWLAnonymousIndividual.class);
    visitor.visit(individual);
    verify(visitor).visit(individual);
    verify(visitorFactory).createIndividualVisitor();
    verify(individualVisitor).visit(individual);
  }

  @Test
  public void shouldVisitLiteral() {
    var literal = mock(OWLLiteral.class);
    visitor.visit(literal);
    verify(visitor).visit(literal);
    verify(visitorFactory).createDataVisitor();
    verify(dataVisitor).visit(literal);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenNodeIdMapperNull() {
    NodeIdMapper nullIdMapper = null;
    new AnnotationObjectVisitor(nullIdMapper, visitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenVisitorFactoryNull() {
    VisitorFactory nullVisitorFactory = null;
    new AnnotationObjectVisitor(nodeIdMapper, nullVisitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenAnnotationNull() {
    OWLAnnotation nullAnnotation = null;
    visitor.visit(nullAnnotation);
  }
}