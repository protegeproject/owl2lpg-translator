package edu.stanford.owl2lpg.exporter.common.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class BranchNode {

  public static final String NODE_ID = ":ID";

  public static final String PROPERTY_BRANCH_ID = "branchId:string";

  public static final String NODE_LABELS = ":LABEL";

  @JsonCreator
  @Nonnull
  public static BranchNode create(@JsonProperty(NODE_ID) @Nonnull String nodeId,
                                  @JsonProperty(PROPERTY_BRANCH_ID) @Nonnull String branchId,
                                  @JsonProperty(NODE_LABELS) @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_BranchNode(nodeId, branchId, nodeLabels);
  }

  @Nonnull
  public static BranchNode of(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.printNodeId(),
        node.getProperty(PropertyFields.BRANCH_ID),
        node.getLabels().asList());
  }

  @JsonProperty(NODE_ID)
  @Nonnull
  public abstract String getNodeId();

  @JsonProperty(PROPERTY_BRANCH_ID)
  @Nonnull
  public abstract String getBranchId();

  @JsonProperty(NODE_LABELS)
  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
