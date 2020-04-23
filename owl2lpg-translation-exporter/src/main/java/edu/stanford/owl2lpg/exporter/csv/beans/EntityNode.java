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

public class EntityNode {

  @Nonnull
  @CsvBindByName(column = ":ID", required = true)
  private final String nodeId;

  @Nonnull
  @CsvBindByName(column = "iri:string", required = true)
  private final String propertyIri;

  @Nonnull
  @CsvBindAndSplitByName(column = ":LABEL",
      elementType = String.class, splitOn = ";",
      writeDelimiter = ";", required = true)
  private final ImmutableList<String> nodeLabels;

  private EntityNode(@Nonnull String nodeId,
                     @Nonnull String propertyIri,
                     @Nonnull ImmutableList<String> nodeLabels) {
    this.nodeId = checkNotNull(nodeId);
    this.propertyIri = checkNotNull(propertyIri);
    this.nodeLabels = checkNotNull(nodeLabels);
  }

  public static EntityNode of(@Nonnull Node node) {
    return new EntityNode(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyNames.IRI),
        node.getLabels());
  }

  @Nonnull
  public String getNodeId() {
    return nodeId;
  }

  @Nonnull
  public String getPropertyIri() {
    return propertyIri;
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
    EntityNode that = (EntityNode) o;
    return Objects.equal(nodeId, that.nodeId) &&
        Objects.equal(propertyIri, that.propertyIri) &&
        Objects.equal(nodeLabels, that.nodeLabels);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(nodeId, propertyIri, nodeLabels);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("nodeId", nodeId)
        .add("propertyIri", propertyIri)
        .add("nodeLabels", nodeLabels)
        .toString();
  }
}
