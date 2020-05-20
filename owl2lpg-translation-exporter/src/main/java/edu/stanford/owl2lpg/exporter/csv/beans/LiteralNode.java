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
public abstract class LiteralNode {

  public static LiteralNode create(@Nonnull String nodeId,
                                   @Nonnull String propertyLexicalForm,
                                   @Nonnull String propertyDatatype,
                                   @Nonnull String propertyLanguage,
                                   @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_LiteralNode(nodeId, propertyLexicalForm,
        propertyDatatype, propertyLanguage, nodeLabels);
  }

  public static LiteralNode create(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.LEXICAL_FORM),
        node.getProperties().get(PropertyFields.DATATYPE),
        node.getProperties().get(PropertyFields.LANGUAGE),
        node.getLabels());
  }

  @Nonnull
  public abstract String getNodeId();

  @Nonnull
  public abstract String getPropertyLexicalForm();

  @Nonnull
  public abstract String getPropertyDatatype();

  @Nonnull
  public abstract String getPropertyLanguage();

  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
