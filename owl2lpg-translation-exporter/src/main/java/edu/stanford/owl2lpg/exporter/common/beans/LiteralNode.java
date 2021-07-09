package edu.stanford.owl2lpg.exporter.common.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class LiteralNode {

  public static final String NODE_ID = ":ID";

  public static final String PROPERTY_LEXICAL_FORM = "lexicalForm:string";

  public static final String PROPERTY_DATATYPE = "datatype:string";

  public static final String PROPERTY_LANGUAGE = "language:string";

  public static final String NODE_LABELS = ":LABEL";

  @JsonCreator
  @Nonnull
  public static LiteralNode create(@JsonProperty(NODE_ID) @Nonnull String nodeId,
                                   @JsonProperty(PROPERTY_LEXICAL_FORM) @Nonnull String propertyLexicalForm,
                                   @JsonProperty(PROPERTY_DATATYPE) @Nullable String propertyDatatype,
                                   @JsonProperty(PROPERTY_LANGUAGE) @Nullable String propertyLanguage,
                                   @JsonProperty(NODE_LABELS) @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_LiteralNode(nodeId, propertyLexicalForm,
        propertyDatatype, propertyLanguage, nodeLabels);
  }

  @Nonnull
  public static LiteralNode create(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.printNodeId(),
        node.getProperty(PropertyFields.LEXICAL_FORM),
        node.getProperty(PropertyFields.DATATYPE),
        node.getProperty(PropertyFields.LANGUAGE),
        node.getLabels().asList());
  }

  @JsonProperty(NODE_ID)
  @Nonnull
  public abstract String getNodeId();

  @JsonProperty(PROPERTY_LEXICAL_FORM)
  @Nonnull
  public abstract String getPropertyLexicalForm();

  @JsonProperty(PROPERTY_DATATYPE)
  @Nullable
  public abstract String getPropertyDatatype();

  @JsonProperty(PROPERTY_LANGUAGE)
  @Nullable
  public abstract String getPropertyLanguage();

  @JsonProperty(NODE_LABELS)
  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
