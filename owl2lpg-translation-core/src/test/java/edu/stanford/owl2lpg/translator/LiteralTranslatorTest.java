package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.visitors.DataVisitor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.inject.Provider;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Literal;

@RunWith(MockitoJUnitRunner.class)
public class LiteralTranslatorTest {

  private LiteralTranslator translator;

  private OWLLiteral literal = Literal("abc");

  @Mock
  private Provider<DataVisitor> dataVisitorProvider;

  @Mock
  private DataVisitor dataVisitor;

  @Mock
  private Translation translation;

  @Before
  public void setUp() {
    when(dataVisitorProvider.get()).thenReturn(dataVisitor);
    when(dataVisitor.visit(literal)).thenReturn(translation);
    translator = new LiteralTranslator(dataVisitorProvider);
  }

  @Test
  public void shouldTranslateLiteral() {
    var result = translator.translate(literal);
    assertThat(result, equalTo(translation));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenVisitorNull() {
    new LiteralTranslator(null);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenLiteralNull() {
    translator.translate(null);
  }
}