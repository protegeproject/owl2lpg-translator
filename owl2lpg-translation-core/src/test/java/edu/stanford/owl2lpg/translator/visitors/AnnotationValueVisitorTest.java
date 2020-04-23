package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationValueVisitorTest {

  private AnnotationValueVisitor visitor;

  // @formatter:off
  @Mock private NodeIdMapper nodeIdMapper;
  @Mock private VisitorFactory visitorFactory;
  @Mock private IndividualVisitor individualVisitor;
  @Mock private DataVisitor dataVisitor;
  @Mock private NodeId nodeId;
  // @formatter:off

  @Before
  public void setUp() {
    visitor = spy(new AnnotationValueVisitor(nodeIdMapper, visitorFactory));
    when(visitorFactory.createIndividualVisitor()).thenReturn(individualVisitor);
    when(visitorFactory.createDataVisitor()).thenReturn(dataVisitor);
  }

  @Test
  public void shouldVisitIri() {
    var iri = mock(IRI.class);
    when(nodeIdMapper.get(iri)).thenReturn(nodeId);

    visitor.visit(iri);
    verify(visitor).visit(iri);
    verify(visitor).createIriNode(iri, NodeLabels.IRI);
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
    var lt = mock(OWLLiteral.class);
    visitor.visit(lt);
    verify(visitor).visit(lt);
    verify(visitorFactory).createDataVisitor();
    verify(dataVisitor).visit(lt);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenNodeIdMapperNull() {
    NodeIdMapper nullIdMapper = null;
    new AnnotationValueVisitor(nullIdMapper, visitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenVisitorFactoryNull() {
    VisitorFactory nullVisitorFactory = null;
    new AnnotationValueVisitor(nodeIdMapper, nullVisitorFactory);
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

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenLiteralNull() {
    OWLLiteral nullLiteral = null;
    visitor.visit(nullLiteral);
  }
}