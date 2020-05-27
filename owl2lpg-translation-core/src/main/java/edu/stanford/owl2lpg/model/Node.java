package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;

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

  @Nonnull
  public abstract NodeId getNodeId();

  @Nonnull
  public abstract NodeLabels getLabels();

  @Nonnull
  public abstract Properties getProperties();
}