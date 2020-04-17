package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import edu.stanford.owl2lpg.exporter.csv.bean.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class GraphDataElement {

  @Nullable
  @CsvBindByName(column = ":ID")
  private final String nodeId;

  @Nullable
  @CsvBindByName(column = "iri:string")
  private final String propertyIri;

  @Nullable
  @CsvBindByName(column = "lexicalForm:string")
  private final String propertyLexicalForm;

  @Nullable
  @CsvBindByName(column = "lang:string")
  private final String propertyLanguageTag;

  @Nullable
  @CsvBindByName(column = "nodeID:string")
  private final String propertyNodeId;

  @Nullable
  @CsvBindByName(column = "cardinality:int")
  private final Integer propertyCardinality;

  @Nullable
  @CsvBindAndSplitByName(column = ":LABEL",
      elementType = String.class, splitOn = ";",
      writeDelimiter = ";")
  private final ImmutableList<String> nodeLabels;

  @Nullable
  @CsvBindByName(column = ":START_ID")
  private final String startNodeId;

  @Nullable
  @CsvBindByName(column = ":END_ID")
  private final String endNodeId;

  @Nullable
  @CsvBindByName(column = ":TYPE")
  private final String edgeType;

  private GraphDataElement(@Nullable String nodeId,
                           @Nullable String propertyIri,
                           @Nullable String propertyLexicalForm,
                           @Nullable String propertyLanguageTag,
                           @Nullable String propertyNodeId,
                           @Nullable Integer propertyCardinality,
                           @Nullable ImmutableList<String> nodeLabels,
                           @Nullable String startNodeId,
                           @Nullable String endNodeId,
                           @Nullable String edgeType) {
    this.nodeId = nodeId;
    this.propertyIri = propertyIri;
    this.propertyLexicalForm = propertyLexicalForm;
    this.propertyLanguageTag = propertyLanguageTag;
    this.propertyNodeId = propertyNodeId;
    this.propertyCardinality = propertyCardinality;
    this.nodeLabels = nodeLabels;
    this.startNodeId = startNodeId;
    this.endNodeId = endNodeId;
    this.edgeType = edgeType;
  }

  public static GraphDataElement of(@Nonnull PropertylessNode bean) {
    checkNotNull(bean);
    return new GraphDataElement(bean.getNodeId(), null, null, null, null, null, bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull EntityNode bean) {
    checkNotNull(bean);
    return new GraphDataElement(bean.getNodeId(), bean.getPropertyIri(), null, null, null, null,
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull IriNode bean) {
    checkNotNull(bean);
    return new GraphDataElement(bean.getNodeId(), bean.getPropertyIri(), null, null, null, null,
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull LiteralNode bean) {
    checkNotNull(bean);
    return new GraphDataElement(bean.getNodeId(), null, bean.getPropertyLexicalForm(), null, null, null
        , bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull LanguageTagNode bean) {
    checkNotNull(bean);
    return new GraphDataElement(bean.getNodeId(), null, null, bean.getPropertyLang(), null, null,
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull AnonymousIndividualNode bean) {
    checkNotNull(bean);
    return new GraphDataElement(bean.getNodeId(), null, null, null, bean.getPropertyNodeId(), null,
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull CardinalityAxiomNode bean) {
    checkNotNull(bean);
    return new GraphDataElement(bean.getNodeId(), null, null, null, null, bean.getPropertyCardinality(),
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull PropertylessEdge bean) {
    checkNotNull(bean);
    return new GraphDataElement(null, null, null, null, null, null, null, bean.getStartNodeId(), bean.getEndNodeId(),
        bean.getEdgeType());
  }

  @Nullable
  public String getNodeId() {
    return nodeId;
  }

  @Nullable
  public String getPropertyIri() {
    return propertyIri;
  }

  @Nullable
  public String getPropertyLexicalForm() {
    return propertyLexicalForm;
  }

  @Nullable
  public String getPropertyLanguageTag() {
    return propertyLanguageTag;
  }

  @Nullable
  public String getPropertyNodeId() {
    return propertyNodeId;
  }

  @Nullable
  public Integer getPropertyCardinality() {
    return propertyCardinality;
  }

  @Nullable
  public ImmutableList<String> getNodeLabels() {
    return nodeLabels;
  }

  @Nullable
  public String getStartNodeId() {
    return startNodeId;
  }

  @Nullable
  public String getEndNodeId() {
    return endNodeId;
  }

  @Nullable
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
    GraphDataElement that = (GraphDataElement) o;
    return Objects.equal(nodeId, that.nodeId) &&
        Objects.equal(propertyIri, that.propertyIri) &&
        Objects.equal(propertyLexicalForm, that.propertyLexicalForm) &&
        Objects.equal(propertyLanguageTag, that.propertyLanguageTag) &&
        Objects.equal(propertyNodeId, that.propertyNodeId) &&
        Objects.equal(propertyCardinality, that.propertyCardinality) &&
        Objects.equal(nodeLabels, that.nodeLabels) &&
        Objects.equal(startNodeId, that.startNodeId) &&
        Objects.equal(endNodeId, that.endNodeId) &&
        Objects.equal(edgeType, that.edgeType);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(nodeId, propertyIri, propertyLexicalForm,
        propertyLanguageTag, propertyNodeId, propertyCardinality,
        nodeLabels, startNodeId, endNodeId, edgeType);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("nodeId", nodeId)
        .add("propertyIri", propertyIri)
        .add("propertyLexicalForm", propertyLexicalForm)
        .add("propertyLanguageTag", propertyLanguageTag)
        .add("propertyNodeId", propertyNodeId)
        .add("propertyCardinality", propertyCardinality)
        .add("nodeLabels", nodeLabels)
        .add("startNodeId", startNodeId)
        .add("endNodeId", endNodeId)
        .add("edgeType", edgeType)
        .toString();
  }
}
