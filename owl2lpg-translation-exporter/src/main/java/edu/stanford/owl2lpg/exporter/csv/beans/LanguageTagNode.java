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
public abstract class LanguageTagNode {

  public static LanguageTagNode create(@Nonnull String nodeId,
                                       @Nonnull String propertyLang,
                                       @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_LanguageTagNode(nodeId, propertyLang, nodeLabels);
  }

  public static LanguageTagNode of(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.LANGUAGE),
        node.getLabels());
  }

  @Nonnull
  public abstract String getNodeId();

  @Nonnull
  public abstract String getPropertyLang();

  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
