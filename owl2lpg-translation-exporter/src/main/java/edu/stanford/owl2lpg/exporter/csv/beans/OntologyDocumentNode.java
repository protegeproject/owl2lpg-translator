package edu.stanford.owl2lpg.exporter.csv.beans;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.versioning.translator.AxiomTranslatorEx.PropertyFields;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class OntologyDocumentNode {

  public static OntologyDocumentNode create(@Nonnull String nodeId,
                                            @Nonnull String ontologyDocumentId,
                                            @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_OntologyDocumentNode(nodeId, ontologyDocumentId, nodeLabels);
  }

  public static OntologyDocumentNode of(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.ONTOLOGY_DOCUMENT_ID),
        node.getLabels());
  }

  @Nonnull
  public abstract String getNodeId();

  @Nonnull
  public abstract String getOntologyDocumentId();

  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
