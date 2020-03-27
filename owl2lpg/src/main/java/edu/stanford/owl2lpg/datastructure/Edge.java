package edu.stanford.owl2lpg.datastructure;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a graph connector (or an edge) from one node to the other.
 * The edge can have a label and a set of key-value properties that
 * describe the edge.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Edge {

  private final Node fromNode;
  private final AnyNode toNode;
  private final String label;
  private final Properties properties;

  private Edge(@Nonnull Node fromNode,
               @Nonnull AnyNode toNode,
               @Nonnull String label,
               @Nonnull Properties properties) {
    this.fromNode = checkNotNull(fromNode);
    this.toNode = checkNotNull(toNode);
    this.label = checkNotNull(label);
    this.properties = checkNotNull(properties);
  }

  public static Edge create(Node fromNode, AnyNode toNode, String label, Properties properties) {
    return new Edge(fromNode, toNode, label, properties);
  }

  public static Edge create(Node fromNode, AnyNode toNode, String label) {
    return new Edge(fromNode, toNode, label, new Properties());
  }

  public Node getFromNode() {
    return fromNode;
  }

  public AnyNode getToNode() {
    return toNode;
  }

  public String getLabel() {
    return label;
  }

  public Properties getProperties() {
    return properties;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Edge edge = (Edge) o;
    return Objects.equal(fromNode, edge.fromNode) &&
        Objects.equal(toNode, edge.toNode) &&
        Objects.equal(label, edge.label) &&
        Objects.equal(properties, edge.properties);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(fromNode, toNode, label, properties);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("fromNode", fromNode)
        .add("toNode", toNode)
        .add("label", label)
        .add("properties", properties)
        .toString();
  }
}
