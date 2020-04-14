package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationValueVisitorTest {

  private AnnotationValueVisitor visitor;

  @Mock
  private DataVisitor dataVisitor;

  @Before
  public void setUp() {
    visitor = spy(new AnnotationValueVisitor(dataVisitor));
  }

  @Test
  public void shouldVisitIri() {
    var iri = mock(IRI.class);
    visitor.visit(iri);

    verify(visitor).visit(iri);
    verify(visitor).createIriNode(iri, NodeLabels.IRI);
  }

  @Test
  public void shouldVisitAnonymousIndividual() {
    var individual = mock(OWLAnonymousIndividual.class);
    when(individual.getID()).thenReturn(NodeID.getNodeID());
    visitor.visit(individual);

    verify(visitor).visit(individual);
    verify(visitor).createAnonymousIndividualNode(individual,
        NodeLabels.ANONYMOUS_INDIVIDUAL);
  }

  @Test
  public void shouldVisitLiteral() {
    var lt = mock(OWLLiteral.class);
    visitor.visit(lt);

    verify(visitor).visit(lt);
    verify(dataVisitor).visit(lt);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenDataVisitorNull() {
    DataVisitor nullDataVisitor = null;
    new AnnotationValueVisitor(nullDataVisitor);
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