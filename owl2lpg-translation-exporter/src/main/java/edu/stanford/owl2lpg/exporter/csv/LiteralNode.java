package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;

import javax.annotation.Nonnull;

public class LiteralNode {

  @Nonnull
  @CsvBindByName(column = ":ID", required = true)
  private final String nodeId;

  @Nonnull
  @CsvBindByName(column = "lexicalForm:string", required = true)
  private final String propertyLexicalForm;

  @Nonnull
  @CsvBindAndSplitByName(column = ":LABEL",
      elementType = String.class, splitOn = ";",
      writeDelimiter = ";", required = true)
  private final ImmutableList<String> nodeLabels;

  private LiteralNode(@Nonnull String nodeId,
                      @Nonnull String propertyLexicalForm,
                      @Nonnull ImmutableList<String> nodeLabels) {
    this.nodeId = nodeId;
    this.propertyLexicalForm = propertyLexicalForm;
    this.nodeLabels = nodeLabels;
  }

  public static LiteralNode create(@Nonnull String nodeId,
                                   @Nonnull String propertyLexicalForm,
                                   @Nonnull ImmutableList<String> nodeLabels) {
    return new LiteralNode(nodeId, propertyLexicalForm, nodeLabels);
  }

  @Nonnull
  public String getNodeId() {
    return nodeId;
  }

  @Nonnull
  public String getPropertyLexicalForm() {
    return propertyLexicalForm;
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
    LiteralNode that = (LiteralNode) o;
    return Objects.equal(nodeId, that.nodeId) &&
        Objects.equal(propertyLexicalForm, that.propertyLexicalForm) &&
        Objects.equal(nodeLabels, that.nodeLabels);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(nodeId, propertyLexicalForm, nodeLabels);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("nodeId", nodeId)
        .add("propertyLexicalForm", propertyLexicalForm)
        .add("nodeLabels", nodeLabels)
        .toString();
  }
}
