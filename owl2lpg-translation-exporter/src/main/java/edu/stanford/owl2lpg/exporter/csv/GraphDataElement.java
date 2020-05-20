package edu.stanford.owl2lpg.exporter.csv;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.exporter.csv.beans.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class GraphDataElement {

  public static GraphDataElement create(@Nullable String nodeId,
                                        @Nullable String projectId,
                                        @Nullable String branchId,
                                        @Nullable String ontoDocId,
                                        @Nullable String propertyIri,
                                        @Nullable String propertyLexicalForm,
                                        @Nullable String propertyLanguageTag,
                                        @Nullable String propertyNodeId,
                                        @Nullable Integer propertyCardinality,
                                        @Nullable ImmutableList<String> nodeLabels,
                                        @Nullable String startNodeId,
                                        @Nullable String endNodeId,
                                        @Nullable String edgeType) {
    return new AutoValue_GraphDataElement(nodeId, projectId, branchId, ontoDocId,
        propertyIri, propertyLexicalForm, propertyLanguageTag, propertyNodeId,
        propertyCardinality, nodeLabels, startNodeId, endNodeId, edgeType);
  }

  public static GraphDataElement of(@Nonnull PropertylessNode bean) {
    checkNotNull(bean);
    return create(bean.getNodeId(), null, null, null, null, null, null, null, null,
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull ProjectNode bean) {
    checkNotNull(bean);
    return create(bean.getNodeId(), bean.getProjectId(), null, null, null, null, null, null, null,
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull BranchNode bean) {
    checkNotNull(bean);
    return create(bean.getNodeId(), null, bean.getBranchId(), null, null, null, null, null, null,
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull OntologyDocumentNode bean) {
    checkNotNull(bean);
    return create(bean.getNodeId(), null, null, bean.getOntologyDocumentId(), null, null, null, null,
        null,
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull EntityNode bean) {
    checkNotNull(bean);
    return create(bean.getNodeId(), null, null, null, bean.getPropertyIri(), null, null, null, null,
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull IriNode bean) {
    checkNotNull(bean);
    return create(bean.getNodeId(), null, null, null, bean.getPropertyIri(), null, null, null, null,
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull LiteralNode bean) {
    checkNotNull(bean);
    return create(bean.getNodeId(), null, null, null, null, bean.getPropertyLexicalForm(), null, null,
        null
        , bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull LanguageTagNode bean) {
    checkNotNull(bean);
    return create(bean.getNodeId(), null, null, null, null, null, bean.getPropertyLang(), null, null,
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull AnonymousIndividualNode bean) {
    checkNotNull(bean);
    return create(bean.getNodeId(), null, null, null, null, null, null, bean.getPropertyNodeId(), null,
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull CardinalityAxiomNode bean) {
    checkNotNull(bean);
    return create(bean.getNodeId(), null, null, null, null, null, null, null,
        bean.getPropertyCardinality(),
        bean.getNodeLabels(), null, null, null);
  }

  public static GraphDataElement of(@Nonnull PropertylessEdge bean) {
    checkNotNull(bean);
    return create(null, null, null, null, null, null, null, null, null, null, bean.getStartNodeId(),
        bean.getEndNodeId(),
        bean.getEdgeType());
  }

  @Nullable
  public abstract String getNodeId();

  @Nullable
  public abstract String getProjectId();

  @Nullable
  public abstract String getBranchId();

  @Nullable
  public abstract String getOntologyDocumentId();

  @Nullable
  public abstract String getPropertyIri();

  @Nullable
  public abstract String getPropertyLexicalForm();

  @Nullable
  public abstract String getPropertyLanguageTag();

  @Nullable
  public abstract String getPropertyNodeId();

  @Nullable
  public abstract Integer getPropertyCardinality();

  @Nullable
  public abstract ImmutableList<String> getNodeLabels();

  @Nullable
  public abstract String getStartNodeId();

  @Nullable
  public abstract String getEndNodeId();

  @Nullable
  public abstract String getEdgeType();
}
