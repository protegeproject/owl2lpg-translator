package edu.stanford.owl2lpg.exporter.csv.beans;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.versioning.translator.AxiomTranslatorEx.PropertyFields;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class BranchNode {

  public static BranchNode create(@Nonnull String nodeId,
                                  @Nonnull String branchId,
                                  @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_BranchNode(nodeId, branchId, nodeLabels);
  }

  public static BranchNode of(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.BRANCH_ID),
        node.getLabels());
  }

  @Nonnull
  public abstract String getNodeId();

  @Nonnull
  public abstract String getBranchId();

  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
