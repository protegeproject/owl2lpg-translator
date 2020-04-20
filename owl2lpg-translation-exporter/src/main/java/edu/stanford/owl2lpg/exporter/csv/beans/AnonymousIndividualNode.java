package edu.stanford.owl2lpg.exporter.csv.beans;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public class AnonymousIndividualNode {

  @Nonnull
  @CsvBindByName(column = ":ID", required = true)
  private final String nodeId;

  @Nonnull
  @CsvBindByName(column = "nodeID:string", required = true)
  private final String propertyNodeId;

  @Nonnull
  @CsvBindAndSplitByName(column = ":LABEL",
      elementType = String.class, splitOn = ";",
      writeDelimiter = ";", required = true)
  private final ImmutableList<String> nodeLabels;

  private AnonymousIndividualNode(@Nonnull String nodeId,
                                  @Nonnull String propertyNodeId,
                                  @Nonnull ImmutableList<String> nodeLabels) {
    this.nodeId = checkNotNull(nodeId);
    this.propertyNodeId = checkNotNull(propertyNodeId);
    this.nodeLabels = checkNotNull(nodeLabels);
  }

  public static AnonymousIndividualNode of(@Nonnull Node node,
                                           @Nonnull NodeIdProvider nodeIdProvider) {
    return new AnonymousIndividualNode(
        nodeIdProvider.getId(),
        node.getProperties().get(PropertyNames.NODE_ID),
        node.getLabels());
  }

  @Nonnull
  public String getNodeId() {
    return nodeId;
  }

  @Nonnull
  public String getPropertyNodeId() {
    return propertyNodeId;
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
    AnonymousIndividualNode that = (AnonymousIndividualNode) o;
    return Objects.equal(nodeId, that.nodeId) &&
        Objects.equal(propertyNodeId, that.propertyNodeId) &&
        Objects.equal(nodeLabels, that.nodeLabels);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(nodeId, propertyNodeId, nodeLabels);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("nodeId", nodeId)
        .add("propertyNodeId", propertyNodeId)
        .add("nodeLabels", nodeLabels)
        .toString();
  }
}
