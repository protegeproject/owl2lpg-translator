package edu.stanford.owl2lpg.exporter.csv.beans;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.opencsv.bean.CsvBindByName;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.versioning.translator.AxiomTranslatorEx.PropertyFields;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class BranchNode {

  @Nonnull
  @CsvBindByName(column = ":ID", required = true)
  private final String nodeId;

  @Nonnull
  @CsvBindByName(column = "branchId:string", required = true)
  private final String branchId;

  @Nonnull
  @CsvBindByName(column = ":LABEL", required = true)
  private final ImmutableList<String> nodeLabels;

  private BranchNode(@Nonnull String nodeId,
                     @Nonnull String branchId,
                     @Nonnull ImmutableList<String> nodeLabels) {
    this.nodeId = checkNotNull(nodeId);
    this.branchId = checkNotNull(branchId);
    this.nodeLabels = checkNotNull(nodeLabels);
  }

  public static BranchNode of(@Nonnull Node node) {
    return new BranchNode(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.BRANCH_ID),
        node.getLabels());
  }

  @Nonnull
  public String getNodeId() {
    return nodeId;
  }

  @Nonnull
  public String getBranchId() {
    return branchId;
  }

  @Nonnull
  public ImmutableList<String> getNodeLabels() {
    return nodeLabels;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BranchNode that = (BranchNode) o;
    return Objects.equal(nodeId, that.nodeId) &&
        Objects.equal(branchId, that.branchId) &&
        Objects.equal(nodeLabels, that.nodeLabels);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(nodeId, branchId, nodeLabels);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("nodeId", nodeId)
        .add("branchId", branchId)
        .add("nodeLabels", nodeLabels)
        .toString();
  }
}
