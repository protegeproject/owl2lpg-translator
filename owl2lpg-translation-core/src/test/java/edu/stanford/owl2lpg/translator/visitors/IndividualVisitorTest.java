package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IndividualVisitorTest {

  @Mock
  private EntityVisitor entityVisitor;

  private IndividualVisitor visitor;

  @Before
  public void setUp() {
    visitor = spy(new IndividualVisitor(entityVisitor));
  }

  @Test
  public void shouldVisitNamedIndividual() {
    var individual = mock(OWLNamedIndividual.class);
    visitor.visit(individual);
    verify(visitor).visit(individual);
    verify(entityVisitor).visit(individual);
  }

  @Test
  public void shouldVisitAnonymousIndividual() {
    var individual = mock(OWLAnonymousIndividual.class);
    visitor.visit(individual);
    verify(visitor).visit(individual);
    verify(visitor).createAnonymousIndividualNode(individual, NodeLabels.ANONYMOUS_INDIVIDUAL);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenIndividualNull() {
    OWLAnonymousIndividual nullIndividual = null;
    visitor.visit(nullIndividual);
  }
}