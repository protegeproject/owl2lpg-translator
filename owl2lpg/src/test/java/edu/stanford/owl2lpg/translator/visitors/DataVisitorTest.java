package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static edu.stanford.owl2lpg.model.GraphFactory.Edge;
import static edu.stanford.owl2lpg.model.GraphFactory.Node;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.IRI;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

@RunWith(MockitoJUnitRunner.class)
public class DataVisitorTest {

  private DataVisitor visitor;

  private Node datatypeNode = Node(NodeLabels.DATATYPE);

  @Before
  public void setUp() {
    visitor = new DataVisitor(new EntityVisitor());
  }

  @Test
  public void shouldTranslateDatatype() {
    var dt = Datatype(IRI("http://www.w3.org/2001/XMLSchema#string"));
    var result = visitor.visit(dt);

    var iriNode = Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#string"));
    var entityIriEdge = Edge(datatypeNode, iriNode, EdgeLabels.ENTITY_IRI);

    assertMainNodeMatches(result, datatypeNode);
    assertEdgesMatch(result, 1, entityIriEdge);
    assertNestedTranslationsMatch(result, 0, new Translation[]{});
  }

  @Test
  public void shouldTranslatePlainLiteral() {
    var lt = PlainLiteral("abc");
    var result = visitor.visit(lt);

    var literalNode = Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "abc"));
    var iriNode = Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral"));
    var languageTagNode = Node(NodeLabels.LANGUAGE_TAG, Properties(LANGUAGE, ""));
    var datatypeEdge = Edge(literalNode, datatypeNode, EdgeLabels.DATATYPE);
    var languageTagEdge = Edge(literalNode, languageTagNode, EdgeLabels.LANGUAGE_TAG);
    var entityIriEdge = Edge(datatypeNode, iriNode, EdgeLabels.ENTITY_IRI);
    var datatypeTranslation = Translation.create(datatypeNode, ImmutableList.of(entityIriEdge));

    assertMainNodeMatches(result, literalNode);
    assertEdgesMatch(result, 2, datatypeEdge, languageTagEdge);
    assertNestedTranslationsMatch(result, 1, datatypeTranslation);
  }

  @Test
  public void shouldTranslateTypedLiteral() {
    var lt = Literal(123);
    var result = visitor.visit(lt);

    var literalNode = Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "123"));
    var iriNode = Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#integer"));
    var datatypeEdge = Edge(literalNode, datatypeNode, EdgeLabels.DATATYPE);
    var entityIriEdge = Edge(datatypeNode, iriNode, EdgeLabels.ENTITY_IRI);
    var datatypeTranslation = Translation.create(datatypeNode, ImmutableList.of(entityIriEdge));

    assertMainNodeMatches(result, literalNode);
    assertEdgesMatch(result, 1, datatypeEdge);
    assertNestedTranslationsMatch(result, 1, datatypeTranslation);
  }

  @Test
  public void shouldTranslateDataComplementOf() {
    var dr = DataComplementOf(Datatype(IRI("http://www.w3.org/2001/XMLSchema#positiveInteger")));
    var result = visitor.visit(dr);

    var complementNode = Node(NodeLabels.DATA_COMPLEMENT_OF);
    var iriNode = Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#positiveInteger"));
    var datatypeEdge = Edge(complementNode, datatypeNode, EdgeLabels.DATA_RANGE);
    var entityIriEdge = Edge(datatypeNode, iriNode, EdgeLabels.ENTITY_IRI);
    var datatypeTranslation = Translation.create(datatypeNode, ImmutableList.of(entityIriEdge));

    assertMainNodeMatches(result, complementNode);
    assertEdgesMatch(result, 1, datatypeEdge);
    assertNestedTranslationsMatch(result, 1, datatypeTranslation);
  }

  @Test
  public void shouldTranslateDataOneOf() {
    var dr = DataOneOf(
        Literal("a"),
        Literal("b"),
        Literal("c"));
    var result = visitor.visit(dr);

    var enumerationNode = Node(NodeLabels.DATA_ONE_OF);
    var literalNode1 = Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "a"));
    var literalEdge1 = Edge(enumerationNode, literalNode1, EdgeLabels.LITERAL);
    var literalNode2 = Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "b"));
    var literalEdge2 = Edge(enumerationNode, literalNode2, EdgeLabels.LITERAL);
    var literalNode3 = Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "c"));
    var literalEdge3 = Edge(enumerationNode, literalNode3, EdgeLabels.LITERAL);

    var iriNode = Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#string"));
    var entityIriEdge = Edge(datatypeNode, iriNode, EdgeLabels.ENTITY_IRI);
    var datatypeTranslation = Translation.create(datatypeNode, ImmutableList.of(entityIriEdge));

    var datatypeEdge1 = Edge(literalNode1, datatypeNode, EdgeLabels.DATATYPE);
    var literalTranslation1 = Translation.create(literalNode1,
        ImmutableList.of(datatypeEdge1), ImmutableList.of(datatypeTranslation));
    var datatypeEdge2 = Edge(literalNode2, datatypeNode, EdgeLabels.DATATYPE);
    var literalTranslation2 = Translation.create(literalNode2,
        ImmutableList.of(datatypeEdge2), ImmutableList.of(datatypeTranslation));
    var datatypeEdge3 = Edge(literalNode3, datatypeNode, EdgeLabels.DATATYPE);
    var literalTranslation3 = Translation.create(literalNode3,
        ImmutableList.of(datatypeEdge3), ImmutableList.of(datatypeTranslation));

    assertMainNodeMatches(result, enumerationNode);
    assertEdgesMatch(result, 3, literalEdge1, literalEdge2, literalEdge3);
    assertNestedTranslationsMatch(result, 3, literalTranslation1, literalTranslation2, literalTranslation3);
  }

  private static void assertMainNodeMatches(Translation result, Node actualMainNode) {
    var mainNode = result.getMainNode();
    assertThat(mainNode, equalTo(actualMainNode));
  }

  private static void assertEdgesMatch(Translation result, int actualSize, Edge... actualEdges) {
    var edges = result.getEdges();
    assertThat(edges, hasSize(actualSize));
    assertThat(edges, containsInAnyOrder(actualEdges));
  }

  private static void assertNestedTranslationsMatch(Translation result, int actualSize,
                                                    Translation... actualTranslations) {
    var nestedTranslations = result.getNestedTranslations();
    assertThat(nestedTranslations, hasSize(actualSize));
    assertThat(nestedTranslations, containsInAnyOrder(actualTranslations));
  }
}