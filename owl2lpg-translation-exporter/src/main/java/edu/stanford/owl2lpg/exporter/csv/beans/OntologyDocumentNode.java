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
public abstract class OntologyDocumentNode {

  public static final String NODE_ID = ":ID";

  public static final String PROPERTY_DOCUMENT_ID = "ontologyDocumentId:string";

  public static final String NODE_LABELS = ":LABEL";

  @JsonCreator
  @Nonnull
  public static OntologyDocumentNode create(@JsonProperty(NODE_ID) @Nonnull String nodeId,
                                            @JsonProperty(PROPERTY_DOCUMENT_ID) @Nonnull String ontologyDocumentId,
                                            @JsonProperty(NODE_LABELS) @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_OntologyDocumentNode(nodeId, ontologyDocumentId, nodeLabels);
  }

  @Nonnull
  public static OntologyDocumentNode of(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.ONTOLOGY_DOCUMENT_ID),
        node.getLabels());
  }

  @JsonProperty(NODE_ID)
  @Nonnull
  public abstract String getNodeId();

  @JsonProperty(PROPERTY_DOCUMENT_ID)
  @Nonnull
  public abstract String getOntologyDocumentId();

  @JsonProperty(NODE_LABELS)
  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
