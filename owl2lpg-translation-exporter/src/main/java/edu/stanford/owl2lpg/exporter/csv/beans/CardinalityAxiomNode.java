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
import static edu.stanford.owl2lpg.exporter.csv.beans.Utils.NodeID;

public class CardinalityAxiomNode {

  @Nonnull
  @CsvBindByName(column = ":ID", required = true)
  private final String nodeId;

  @Nonnull
  @CsvBindByName(column = "cardinality:int", required = true)
  private final Integer propertyCardinality;

  @Nonnull
  @CsvBindAndSplitByName(column = ":LABEL",
      elementType = String.class, splitOn = ";",
      writeDelimiter = ";", required = true)
  private final ImmutableList<String> nodeLabels;

  private CardinalityAxiomNode(@Nonnull String nodeId,
                               @Nonnull Integer propertyCardinality,
                               @Nonnull ImmutableList<String> nodeLabels) {
    this.nodeId = checkNotNull(nodeId);
    this.propertyCardinality = checkNotNull(propertyCardinality);
    this.nodeLabels = checkNotNull(nodeLabels);
  }

  public static CardinalityAxiomNode of(@Nonnull Node node) {
    return new CardinalityAxiomNode(
        NodeID(node.getNodeId()),
        node.getProperties().get(PropertyNames.CARDINALITY),
        node.getLabels());
  }

  @Nonnull
  public String getNodeId() {
    return nodeId;
  }

  @Nonnull
  public Integer getPropertyCardinality() {
    return propertyCardinality;
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
    CardinalityAxiomNode that = (CardinalityAxiomNode) o;
    return Objects.equal(nodeId, that.nodeId) &&
        Objects.equal(propertyCardinality, that.propertyCardinality) &&
        Objects.equal(nodeLabels, that.nodeLabels);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(nodeId, propertyCardinality, nodeLabels);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("nodeId", nodeId)
        .add("propertyCardinality", propertyCardinality)
        .add("nodeLabels", nodeLabels)
        .toString();
  }
}
