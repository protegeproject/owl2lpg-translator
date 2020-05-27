package edu.stanford.owl2lpg.exporter.csv.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class IriNode {

  public static final String NODE_ID = ":ID";

  public static final String PROPERTY_IRI = "iri:string";

  public static final String NODE_LABELS = ":LABEL";

  @JsonCreator
  @Nonnull
  public static IriNode create(@JsonProperty(NODE_ID) @Nonnull String nodeId,
                               @JsonProperty(PROPERTY_IRI) @Nonnull String propertyIri,
                               @JsonProperty(NODE_LABELS) @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_IriNode(nodeId, propertyIri, nodeLabels);
  }

  @Nonnull
  public static IriNode of(@Nonnull Node node) {
    return create(
        node.getNodeId().toString(),
        Objects.requireNonNull(node.getProperties().get(PropertyFields.IRI)),
        node.getLabels().getValues());
  }

  @JsonProperty(NODE_ID)
  @Nonnull
  public abstract String getNodeId();

  @JsonProperty(PROPERTY_IRI)
  @Nonnull
  public abstract String getPropertyIri();

  @JsonProperty(NODE_LABELS)
  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
