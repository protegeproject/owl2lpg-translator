package edu.stanford.owl2lpg.exporter.csv.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;

import javax.annotation.Nonnull;

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
                                   @JsonProperty(PROPERTY_DATATYPE) @Nonnull String propertyDatatype,
                                   @JsonProperty(PROPERTY_LANGUAGE) @Nonnull String propertyLanguage,
                                   @JsonProperty(NODE_LABELS) @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_LiteralNode(nodeId, propertyLexicalForm,
        propertyDatatype, propertyLanguage, nodeLabels);
  }

  @Nonnull
  public static LiteralNode create(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.LEXICAL_FORM),
        node.getProperties().get(PropertyFields.DATATYPE),
        node.getProperties().get(PropertyFields.LANGUAGE),
        node.getLabels().getValues());
  }

  @JsonProperty(NODE_ID)
  @Nonnull
  public abstract String getNodeId();

  @JsonProperty(PROPERTY_LEXICAL_FORM)
  @Nonnull
  public abstract String getPropertyLexicalForm();

  @JsonProperty(PROPERTY_DATATYPE)
  @Nonnull
  public abstract String getPropertyDatatype();

  @JsonProperty(PROPERTY_LANGUAGE)
  @Nonnull
  public abstract String getPropertyLanguage();

  @JsonProperty(NODE_LABELS)
  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
