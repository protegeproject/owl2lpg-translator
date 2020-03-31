package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.GraphFactory;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.Edge;
import static edu.stanford.owl2lpg.datastructure.GraphFactory.Node;
import static edu.stanford.owl2lpg.translator.PropertiesFactory.Properties;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.IRI;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.NODE_ID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.AnonymousIndividual;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.NamedIndividual;

public class IndividualTranslatorTest {

  private IndividualTranslator translator;

  @Before
  public void createTranslator() {
    translator = new IndividualTranslator();
  }

  @Test
  public void shouldTranslateNamedIndividual() {
    final OWLNamedIndividual a = NamedIndividual(IRI("http://example.org/ind"));
    Graph actualGraph = (Graph) a.accept(translator);
    Graph expectedGraph = GraphFactory.Graph(
        Edge(
            Node(NodeLabels.NAMED_INDIVIDUAL),
            Node(NodeLabels.IRI, Properties(IRI, "http://example.org/ind")),
            EdgeLabels.ENTITY_IRI));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateAnnonymousIndividual() {
    final OWLAnonymousIndividual a = AnonymousIndividual("ind");
    Node actualNode = (Node) a.accept(translator);
    Node expectedNode = Node(NodeLabels.ANONYMOUS_INDIVIDUAL, Properties(NODE_ID, "_:ind"));
    assertThat(actualNode, equalTo(expectedNode));
  }
}