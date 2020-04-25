package edu.stanford.owl2lpg.exporter.csv.beans;

import com.google.common.base.CaseFormat;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.opencsv.bean.CsvBindByName;
import edu.stanford.owl2lpg.model.Edge;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public class PropertylessEdge {

  @Nonnull
  @CsvBindByName(column = ":START_ID", required = true)
  private final String startNodeId;

  @Nonnull
  @CsvBindByName(column = ":END_ID", required = true)
  private final String endNodeId;

  @Nonnull
  @CsvBindByName(column = ":TYPE", required = true)
  private final String edgeType;

  private PropertylessEdge(@Nonnull String startNodeId,
                           @Nonnull String endNodeId,
                           @Nonnull String edgeType) {
    this.startNodeId = checkNotNull(startNodeId);
    this.endNodeId = checkNotNull(endNodeId);
    this.edgeType = checkNotNull(edgeType);
  }

  public static PropertylessEdge of(@Nonnull Edge edge) {
    return new PropertylessEdge(
        edge.getFromNode().getNodeId().toString(),
        edge.getToNode().getNodeId().toString(),
        CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, edge.getLabel()));
  }

  @Nonnull
  public String getStartNodeId() {
    return startNodeId;
  }

  @Nonnull
  public String getEndNodeId() {
    return endNodeId;
  }

  @Nonnull
  public String getEdgeType() {
    return edgeType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertylessEdge that = (PropertylessEdge) o;
    return Objects.equal(startNodeId, that.startNodeId) &&
        Objects.equal(endNodeId, that.endNodeId) &&
        Objects.equal(edgeType, that.edgeType);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(startNodeId, endNodeId, edgeType);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("startNodeId", startNodeId)
        .add("endNodeId", endNodeId)
        .add("edgeType", edgeType)
        .toString();
  }
}
