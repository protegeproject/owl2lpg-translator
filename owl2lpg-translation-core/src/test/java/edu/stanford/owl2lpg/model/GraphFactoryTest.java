package edu.stanford.owl2lpg.model;

import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(MockitoJUnitRunner.class)
public class GraphFactoryTest {

  @Mock
  private Properties properties;

  @Mock
  private Node fromNode, toNode;

  @Mock
  private NodeIdProvider nodeIdProvider;

  @Test
  public void shouldCreateNode() {
    var node = GraphFactory.Node(NodeLabels.CLASS, properties, nodeIdProvider);
    assertNodeMatches(node, properties);
  }

  @Test
  public void shouldCreateNodeWithEmptyProperties() {
    var node = GraphFactory.Node(NodeLabels.CLASS, nodeIdProvider);
    assertNodeMatches(node, Properties.empty());
  }

  private static void assertNodeMatches(Node node, Properties properties) {
    assertThat(node.getLabels(), equalTo(NodeLabels.CLASS));
    assertThat(node.getProperties(), equalTo(properties));
  }

  @Test
  public void shouldCreateEdge() {
    var edge = GraphFactory.Edge(
        fromNode, toNode,
        EdgeLabels.ENTITY_IRI,
        properties);
    assertEdgeMatches(edge, fromNode, toNode, properties);
  }

  @Test
  public void shouldCreateEdgeWithEmptyProperties() {
    var edge = GraphFactory.Edge(
        fromNode, toNode,
        EdgeLabels.ENTITY_IRI);
    assertEdgeMatches(edge, fromNode, toNode, Properties.empty());
  }

  private static void assertEdgeMatches(Edge edge, Node fromNode, Node toNode, Properties properties) {
    assertThat(edge.getFromNode(), equalTo(fromNode));
    assertThat(edge.getToNode(), equalTo(toNode));
    assertThat(edge.getLabel(), equalTo(EdgeLabels.ENTITY_IRI));
    assertThat(edge.getProperties(), equalTo(properties));
  }
}