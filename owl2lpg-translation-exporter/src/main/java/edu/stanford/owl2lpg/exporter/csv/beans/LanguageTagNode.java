package edu.stanford.owl2lpg.exporter.csv.beans;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public class LanguageTagNode {

  @Nonnull
  @CsvBindByName(column = ":ID", required = true)
  private final String nodeId;

  @Nonnull
  @CsvBindByName(column = "lang:string", required = true)
  private final String propertyLang;

  @Nonnull
  @CsvBindAndSplitByName(column = ":LABEL",
      elementType = String.class, splitOn = ";",
      writeDelimiter = ";", required = true)
  private final ImmutableList<String> nodeLabels;

  private LanguageTagNode(@Nonnull String nodeId,
                          @Nonnull String propertyLang,
                          @Nonnull ImmutableList<String> nodeLabels) {
    this.nodeId = checkNotNull(nodeId);
    this.propertyLang = checkNotNull(propertyLang);
    this.nodeLabels = checkNotNull(nodeLabels);
  }

  public static LanguageTagNode of(@Nonnull Node node) {
    return new LanguageTagNode(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.LANGUAGE),
        node.getLabels());
  }

  @Nonnull
  public String getNodeId() {
    return nodeId;
  }

  @Nonnull
  public String getPropertyLang() {
    return propertyLang;
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
    LanguageTagNode that = (LanguageTagNode) o;
    return Objects.equal(nodeId, that.nodeId) &&
        Objects.equal(propertyLang, that.propertyLang) &&
        Objects.equal(nodeLabels, that.nodeLabels);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(nodeId, propertyLang, nodeLabels);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("nodeId", nodeId)
        .add("propertyLang", propertyLang)
        .add("nodeLabels", nodeLabels)
        .toString();
  }
}
