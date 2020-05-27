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
public abstract class CardinalityAxiomNode {

  public static final String NODE_ID = ":ID";

  public static final String PROPERTY_CARDINALITY = "cardinality:int";

  public static final String NODE_LABELS = ":LABEL";

  @JsonCreator
  @Nonnull
  public static CardinalityAxiomNode create(@JsonProperty(NODE_ID) @Nonnull String nodeId,
                                            @JsonProperty(PROPERTY_CARDINALITY) @Nonnull Integer propertyCardinality,
                                            @JsonProperty(NODE_LABELS) @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_CardinalityAxiomNode(nodeId, propertyCardinality, nodeLabels);
  }

  @Nonnull
  public static CardinalityAxiomNode of(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.printNodeId(),
        node.getProperties().get(PropertyFields.CARDINALITY),
        node.getLabels().asList());
  }

  @JsonProperty(NODE_ID)
  @Nonnull
  public abstract String getNodeId();

  @JsonProperty(PROPERTY_CARDINALITY)
  @Nonnull
  public abstract Integer getPropertyCardinality();

  @JsonProperty(NODE_LABELS)
  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
