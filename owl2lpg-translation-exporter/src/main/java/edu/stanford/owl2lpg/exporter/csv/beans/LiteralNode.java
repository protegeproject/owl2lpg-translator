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

public class LiteralNode {

  @Nonnull
  @CsvBindByName(column = ":ID", required = true)
  private final String nodeId;

  @Nonnull
  @CsvBindByName(column = "lexicalForm:string", required = true)
  private final String propertyLexicalForm;

  @Nonnull
  @CsvBindByName(column = "datatype:string", required = true)
  private final String propertyDatatype;

  @Nonnull
  @CsvBindByName(column = "language:string", required = false)
  private final String propertyLanguage;

  @Nonnull
  @CsvBindAndSplitByName(column = ":LABEL",
      elementType = String.class, splitOn = ";",
      writeDelimiter = ";", required = true)
  private final ImmutableList<String> nodeLabels;

  private LiteralNode(@Nonnull String nodeId,
                      @Nonnull String propertyLexicalForm,
                      @Nonnull String propertyDatatype,
                      @Nonnull String propertyLanguage,
                      @Nonnull ImmutableList<String> nodeLabels) {
    this.nodeId = checkNotNull(nodeId);
    this.propertyLexicalForm = checkNotNull(propertyLexicalForm);
    this.propertyDatatype = checkNotNull(propertyDatatype);
    this.propertyLanguage = checkNotNull(propertyLanguage);
    this.nodeLabels = checkNotNull(nodeLabels);
  }

  public static LiteralNode create(@Nonnull Node node) {
    return new LiteralNode(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.LEXICAL_FORM),
        node.getProperties().get(PropertyFields.DATATYPE),
        node.getProperties().get(PropertyFields.LANGUAGE),
        node.getLabels());
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
  public String getPropertyDatatype() {
    return propertyDatatype;
  }

  @Nonnull
  public String getPropertyLanguage() {
    return propertyLanguage;
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
        Objects.equal(propertyDatatype, that.propertyDatatype) &&
        Objects.equal(propertyLanguage, that.propertyLanguage) &&
        Objects.equal(nodeLabels, that.nodeLabels);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(nodeId, propertyLexicalForm, propertyDatatype, propertyLanguage, nodeLabels);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("nodeId", nodeId)
        .add("propertyLexicalForm", propertyLexicalForm)
        .add("propertyDatatype", propertyDatatype)
        .add("propertyLanguage", propertyLanguage)
        .add("nodeLabels", nodeLabels)
        .toString();
  }
}
