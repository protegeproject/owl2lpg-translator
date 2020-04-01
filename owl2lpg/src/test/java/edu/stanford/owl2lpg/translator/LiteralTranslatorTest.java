package edu.stanford.owl2lpg.translator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLLiteral;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Literal;

@RunWith(MockitoJUnitRunner.class)
public class LiteralTranslatorTest {

  private LiteralTranslator translator;

  @Mock
  private OWLDataVisitorEx<Translation> dataVisitor;

  private OWLLiteral literal = Literal("abc");

  @Mock
  private Translation translation;

  @Before
  public void setup() {
    when(dataVisitor.visit(literal)).thenReturn(translation);
    translator = new LiteralTranslator(dataVisitor);
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

//  @Test
//  public void shouldTranslatePlainLiteralWithLanguage() {
//    OWLLiteral lt = Literal("abc", "en");
//    Graph actualGraph = lt.accept(translator);
//    Graph expectedGraph = Graph(
//        Edge(
//            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "abc")),
//            Graph(
//                Edge(
//                    Node(NodeLabels.DATATYPE),
//                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral")),
//                    EdgeLabels.ENTITY_IRI)),
//            EdgeLabels.DATATYPE),
//        Edge(
//            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "abc")),
//            Node(NodeLabels.LANGUAGE_TAG, Properties(PropertyNames.LANGUAGE, "en")),
//            EdgeLabels.LANGUAGE_TAG));
//    assertThat(actualGraph, equalTo(expectedGraph));
//  }
//
//  @Test
//  public void shouldTranslateStringTypedLiteral() {
//    OWLLiteral lt = Literal("abc");
//    Graph actualGraph = lt.accept(translator);
//    Graph expectedGraph = Graph(
//        Edge(
//            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "abc")),
//            Graph(
//                Edge(
//                    Node(NodeLabels.DATATYPE),
//                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#string")),
//                    EdgeLabels.ENTITY_IRI)),
//            EdgeLabels.DATATYPE));
//    assertThat(actualGraph, equalTo(expectedGraph));
//  }
//
//  @Test
//  public void shouldTranslateIntegerTypedLiteral() {
//    OWLLiteral lt = Literal(22);
//    Graph actualGraph = lt.accept(translator);
//    Graph expectedGraph = Graph(
//        Edge(
//            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "22")),
//            Graph(
//                Edge(
//                    Node(NodeLabels.DATATYPE),
//                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#integer")),
//                    EdgeLabels.ENTITY_IRI)),
//            EdgeLabels.DATATYPE));
//    assertThat(actualGraph, equalTo(expectedGraph));
//  }
//
//  @Test
//  public void shouldTranslateFloatTypedLiteral() {
//    OWLLiteral lt = Literal(22.22f);
//    Graph actualGraph = lt.accept(translator);
//    Graph expectedGraph = Graph(
//        Edge(
//            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "22.22")),
//            Graph(
//                Edge(
//                    Node(NodeLabels.DATATYPE),
//                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#float")),
//                    EdgeLabels.ENTITY_IRI)),
//            EdgeLabels.DATATYPE));
//    assertThat(actualGraph, equalTo(expectedGraph));
//  }
//
//  @Test
//  public void shouldTranslateDoubleTypedLiteral() {
//    OWLLiteral lt = Literal(22.22d);
//    Graph actualGraph = lt.accept(translator);
//    Graph expectedGraph = Graph(
//        Edge(
//            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "22.22")),
//            Graph(
//                Edge(
//                    Node(NodeLabels.DATATYPE),
//                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#double")),
//                    EdgeLabels.ENTITY_IRI)),
//            EdgeLabels.DATATYPE));
//    assertThat(actualGraph, equalTo(expectedGraph));
//  }
//
//  @Test
//  public void shouldTranslateBooleanTypedLiteral() {
//    OWLLiteral lt = Literal(false);
//    Graph actualGraph = lt.accept(translator);
//    Graph expectedGraph = Graph(
//        Edge(
//            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "false")),
//            Graph(
//                Edge(
//                    Node(NodeLabels.DATATYPE),
//                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#boolean")),
//                    EdgeLabels.ENTITY_IRI)),
//            EdgeLabels.DATATYPE));
//    assertThat(actualGraph, equalTo(expectedGraph));
//  }
//
//  @Test
//  public void shouldTranslateAnyTypedLiteral() {
//    OWLLiteral lt = Literal("(650) 123-4567", Datatype(IRI("http://example.org/phoneNumber")));
//    Graph actualGraph = lt.accept(translator);
//    Graph expectedGraph = Graph(
//        Edge(
//            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "(650) 123-4567")),
//            Graph(
//                Edge(
//                    Node(NodeLabels.DATATYPE),
//                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/phoneNumber")),
//                    EdgeLabels.ENTITY_IRI)),
//            EdgeLabels.DATATYPE));
//    assertThat(actualGraph, equalTo(expectedGraph));
//  }
}