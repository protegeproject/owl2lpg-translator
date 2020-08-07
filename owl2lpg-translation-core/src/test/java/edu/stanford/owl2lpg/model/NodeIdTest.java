package edu.stanford.owl2lpg.model;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class NodeIdTest {

  @Test
  public void shouldCreateNodeId() {
    var nodeId = NodeId.create(1);
    assertThat(nodeId.asString(), equalTo("0000000000000001"));
    assertThat(nodeId.toString(), equalTo("NodeId_0000000000000001"));
  }
}