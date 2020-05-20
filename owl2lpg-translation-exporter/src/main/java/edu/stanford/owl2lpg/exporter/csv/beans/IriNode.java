package edu.stanford.owl2lpg.exporter.csv.beans;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class IriNode {

  public static IriNode create(@Nonnull String nodeId,
                               @Nonnull String propertyIri,
                               @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_IriNode(nodeId, propertyIri, nodeLabels);
  }

  public static IriNode of(@Nonnull Node node) {
    return create(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.IRI),
        node.getLabels());
  }

  @Nonnull
  public abstract String getNodeId();

  @Nonnull
  public abstract String getPropertyIri();

  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
