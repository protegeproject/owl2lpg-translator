package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationSubjectVisitorTest {

  private AnnotationSubjectVisitor visitor;

  @Before
  public void setUp() {
    visitor = spy(new AnnotationSubjectVisitor());
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