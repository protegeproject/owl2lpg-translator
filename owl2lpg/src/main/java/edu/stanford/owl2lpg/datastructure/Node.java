package edu.stanford.owl2lpg.datastructure;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a graph node that has a list of labels and a set of key-value
 * properties to describe the node.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Node extends AnyNode {

  private final List<String> labels;
  private final Properties properties;

  private Node(@Nonnull List<String> labels, @Nonnull Properties properties) {
    this.labels = checkNotNull(labels);
    this.properties = checkNotNull(properties);
  }

  public static Node create(List<String> labels, Properties properties) {
    return new Node(labels, properties);
  }

  public static Node create(List<String> labels) {
    return new Node(labels, new Properties());
  }

  public List<String> getLabels() {
    return labels;
  }

  public Properties getProperties() {
    return properties;
  }

  @Override
  public boolean isNode() {
    return true;
  }

  @Override
  public boolean isGraph() {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Node node = (Node) o;
    return Objects.equal(labels, node.labels) &&
        Objects.equal(properties, node.properties);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(labels, properties);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("labels", labels)
        .add("properties", properties)
        .toString();
  }
}