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

  private IndividualVisitor visitor;

  // @formatter:off
  @Mock private NodeIdMapper nodeIdMapper;
  @Mock private VisitorFactory visitorFactory;
  @Mock private EntityVisitor entityVisitor;
  // @formatter:off

  @Before
  public void setUp() {
    visitor = spy(new IndividualVisitor(nodeIdMapper, visitorFactory));
    when(visitorFactory.createEntityVisitor()).thenReturn(entityVisitor);
  }

  @Test
  public void shouldVisitNamedIndividual() {
    var individual = mock(OWLNamedIndividual.class);
    visitor.visit(individual);
    verify(visitor).visit(individual);
    verify(visitorFactory).createEntityVisitor();
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
  public void shouldThrowNPEWhenNodeIdMapperNull() {
    NodeIdMapper nullIdMapper = null;
    new IndividualVisitor(nullIdMapper, visitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenVisitorFactoryNull() {
    VisitorFactory nullVisitorFactory = null;
    new IndividualVisitor(nodeIdMapper, nullVisitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenIndividualNull() {
    OWLAnonymousIndividual nullIndividual = null;
    visitor.visit(nullIndividual);
  }
}