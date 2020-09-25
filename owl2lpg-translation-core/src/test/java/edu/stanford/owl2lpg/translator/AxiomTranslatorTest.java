package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.visitors.AxiomVisitor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import javax.inject.Provider;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.SubClassOf;

@RunWith(MockitoJUnitRunner.class)
public class AxiomTranslatorTest {

  private AxiomTranslator translator;

  private OWLSubClassOfAxiom axiom = SubClassOf(
      Class(IRI("http://example.org/A")),
      Class(IRI("http://example.org/B")));

  @Mock
  private Provider<AxiomVisitor> axiomVisitorProvider;

  @Mock
  private AxiomVisitor axiomVisitor;

  @Mock
  private Translation translation;

  @Before
  public void setUp() {
    when(axiomVisitorProvider.get()).thenReturn(axiomVisitor);
    when(axiomVisitor.visit(axiom)).thenReturn(translation);
    translator = new AxiomTranslator(axiomVisitorProvider);
  }

  @Test
  public void shouldTranslateLiteral() {
    var result = translator.translate(axiom);
    assertThat(result, equalTo(translation));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenVisitorNull() {
    new AxiomTranslator(null);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenLiteralNull() {
    translator.translate(null);
  }
}