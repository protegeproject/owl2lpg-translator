package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

/**
 * Represents a graph node that has a list of labels and a set of key-value
 * properties to describe the node.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class Node {

  public static Node create(@Nonnull NodeId nodeId,
                            @Nonnull ImmutableList<String> labels,
                            @Nonnull Properties properties) {
    return new AutoValue_Node(nodeId, labels, properties);
  }

  public abstract NodeId getNodeId();

  public abstract ImmutableList<String> getLabels();

  public abstract Properties getProperties();
}