package edu.stanford.owl2lpg.exporter.csv.beans;

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

  public static CardinalityAxiomNode create(@Nonnull String nodeId,
                                            @Nonnull Integer propertyCardinality,
                                            @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_CardinalityAxiomNode(nodeId, propertyCardinality, nodeLabels);
  }

  public static CardinalityAxiomNode of(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.CARDINALITY),
        node.getLabels());
  }

  @Nonnull
  public abstract String getNodeId();

  @Nonnull
  public abstract Integer getPropertyCardinality();

  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
