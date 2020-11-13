package edu.stanford.owl2lpg.model;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class NodeIdTest {

  @Test
  public void shouldCreateNodeId() {
    var nodeId = NodeId.create("abcde");
    assertThat(nodeId.asString(), equalTo("abcde"));
    assertThat(nodeId.toString(), equalTo("NodeId_abcde"));
  }
}