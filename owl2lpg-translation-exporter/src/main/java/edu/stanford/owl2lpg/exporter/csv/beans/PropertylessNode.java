package edu.stanford.owl2lpg.exporter.csv.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class PropertylessNode {

  public static final String NODE_ID = ":ID";

  public static final String NODE_LABELS = ":LABEL";

  @JsonCreator
  @Nonnull
  public static PropertylessNode create(@JsonProperty(NODE_ID) @Nonnull String nodeId,
                                        @JsonProperty(NODE_LABELS) @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_PropertylessNode(nodeId, nodeLabels);
  }

  @Nonnull
  public static PropertylessNode of(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.getNodeId().toString(),
        node.getLabels());
  }

  @JsonProperty(NODE_ID)
  @Nonnull
  public abstract String getNodeId();

  @JsonProperty(NODE_LABELS)
  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
