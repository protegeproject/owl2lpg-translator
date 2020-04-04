package edu.stanford.owl2lpg.model;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class NodeTest {

  private Node node;

  @Mock
  private ImmutableList<String> labels;

  @Mock
  private Properties properties;

  @Before
  public void setUp() {
    node = Node.create(1, labels, properties);
  }

  @Test
  public void shouldCreateNode() {
    assertThat(node, is(notNullValue()));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenLabelsNull() {
    Node.create(1, null, properties);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenPropertiesNull() {
    Node.create(1, labels, null);
  }

  @Test
  public void getLabels() {
    var actualLabels = node.getLabels();
    assertThat(actualLabels, equalTo(labels));
  }

  @Test
  public void getProperties() {
    var actualProperties = node.getProperties();
    assertThat(actualProperties, equalTo(properties));
  }
}