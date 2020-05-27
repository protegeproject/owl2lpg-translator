package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a graph node that has a list of labels and a set of key-value
 * properties to describe the node.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class Node {

  @Nonnull
  public static Node create(@Nonnull NodeId nodeId,
                            @Nonnull NodeLabels labels,
                            @Nonnull Properties properties) {
    return new AutoValue_Node(nodeId, labels, properties);
  }

  @Nonnull
  public static Node create(@Nonnull NodeId nodeId,
                            @Nonnull NodeLabels labels) {
    return create(nodeId, labels, Properties.empty());
  }

  public boolean isTypeOf(NodeLabels nodeLabels) {
    return getLabels().isa(nodeLabels);
  }

  @Nullable
  public <E> E getProperty(String key) {
    return getProperties().get(key);
  }

  public String printNodeId() {
    return getNodeId().toString();
  }

  public String printLabels() {
    return getLabels().printLabels();
  }

  public String printProperties() {
    return getProperties().printProperties();
  }

  @Nonnull
  public abstract NodeId getNodeId();

  @Nonnull
  public abstract NodeLabels getLabels();

  @Nonnull
  public abstract Properties getProperties();
}