package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

/**
 * Represents a graph connector (or an edge) from one node to the other.
 * The edge can have a label and a set of key-value properties that
 * describe the edge.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class Edge {

  public static Edge create(@Nonnull Node fromNode,
                            @Nonnull Node toNode,
                            @Nonnull String label,
                            @Nonnull Properties properties) {
    return new AutoValue_Edge(fromNode, toNode, label, properties);
  }

  public boolean isReflexive() {
    return getFromNode().equals(getToNode());
  }

  public abstract Node getFromNode();

  public abstract Node getToNode();

  public abstract String getLabel();

  public abstract Properties getProperties();
}
