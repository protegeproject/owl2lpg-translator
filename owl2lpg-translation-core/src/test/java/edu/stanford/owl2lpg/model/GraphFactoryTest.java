package edu.stanford.owl2lpg.model;

import com.google.common.collect.ImmutableList;
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
  private NodeId nodeId;

  @Mock
  private Properties properties;

  @Mock
  private Node fromNode, toNode;

  @Test
  public void shouldCreateNode() {
    var node = GraphFactory.Node(nodeId, NodeLabels.CLASS, properties);
    assertNodeMatches(node, nodeId, NodeLabels.CLASS, properties);
  }

  @Test
  public void shouldCreateNodeWithEmptyProperties() {
    var node = GraphFactory.Node(nodeId, NodeLabels.CLASS);
    assertNodeMatches(node, nodeId, NodeLabels.CLASS, Properties.empty());
  }

  private static void assertNodeMatches(Node actualNode,
                                        NodeId expectedNodeId,
                                        ImmutableList<String> expectedNodeLabels,
                                        Properties expectedProperties) {
    assertThat(actualNode.getNodeId(), equalTo(expectedNodeId));
    assertThat(actualNode.getLabels(), equalTo(expectedNodeLabels));
    assertThat(actualNode.getProperties(), equalTo(expectedProperties));
  }

  @Test
  public void shouldCreateEdge() {
    var edge = GraphFactory.Edge(
        fromNode, toNode,
        EdgeLabels.ENTITY_IRI,
        properties);
    assertEdgeMatches(edge, fromNode, toNode, EdgeLabels.ENTITY_IRI, properties);
  }

  @Test
  public void shouldCreateEdgeWithEmptyProperties() {
    var edge = GraphFactory.Edge(
        fromNode, toNode,
        EdgeLabels.ENTITY_IRI);
    assertEdgeMatches(edge, fromNode, toNode, EdgeLabels.ENTITY_IRI, Properties.empty());
  }

  private static void assertEdgeMatches(Edge actualNode,
                                        Node expectedFromNode,
                                        Node expectedToNode,
                                        String expectedNodeLabel,
                                        Properties expectedProperties) {
    assertThat(actualNode.getFromNode(), equalTo(expectedFromNode));
    assertThat(actualNode.getToNode(), equalTo(expectedToNode));
    assertThat(actualNode.getLabel(), equalTo(expectedNodeLabel));
    assertThat(actualNode.getProperties(), equalTo(expectedProperties));
  }
}