package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;

import static edu.stanford.owl2lpg.model.GraphFactory.Edge;
import static edu.stanford.owl2lpg.model.GraphFactory.Node;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.IRI;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Datatype;

@RunWith(MockitoJUnitRunner.class)
public class DataVisitorTest {

  private DataVisitor visitor;

  @Mock
  private IRI datatypeIri;

  @Before
  public void setUp() throws Exception {
    when(datatypeIri.toString()).thenReturn("http://example.org/mock");
    visitor = new DataVisitor(new EntityVisitor());
  }

  @Test
  public void shouldTranslateDatatype() {
    var dt = Datatype(datatypeIri);
    var result = visitor.visit(dt);

    var mainNode = result.getMainNode();
    var edges = result.getEdges();

    var datatypeNode = Node(NodeLabels.DATATYPE);
    var iriNode = Node(NodeLabels.IRI, Properties(IRI, "http://example.org/mock"));
    var iriEdge = Edge(datatypeNode, iriNode, EdgeLabels.ENTITY_IRI);

    assertThat(mainNode, equalTo(mainNode));
    assertThat(edges, hasItem(iriEdge));
  }
}